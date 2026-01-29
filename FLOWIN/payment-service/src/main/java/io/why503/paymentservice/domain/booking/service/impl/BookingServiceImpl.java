package io.why503.paymentservice.domain.booking.service.impl;

import io.why503.paymentservice.domain.booking.mapper.BookingMapper;
import io.why503.paymentservice.domain.booking.model.dto.request.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.request.TicketRequest;
import io.why503.paymentservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.model.enums.BookingStatus;
import io.why503.paymentservice.domain.booking.model.enums.TicketStatus;
import io.why503.paymentservice.domain.booking.repository.BookingRepository;
import io.why503.paymentservice.domain.booking.repository.TicketRepository;
import io.why503.paymentservice.domain.booking.service.BookingService;
import io.why503.paymentservice.global.client.AccountClient;
import io.why503.paymentservice.global.client.PerformanceClient;
import io.why503.paymentservice.global.client.dto.request.PointUseRequest;
import io.why503.paymentservice.global.client.dto.response.AccountResponse;
import io.why503.paymentservice.global.client.dto.response.RoundSeatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 예매 서비스 구현체 (Core Business Logic Implementation)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final BookingMapper bookingMapper;
    private final AccountClient accountClient;
    private final PerformanceClient performanceClient;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, Long userSq) {
        log.info(">>> [Booking] 예매 생성 요청 | UserSq={}", userSq);

        if (accountClient.getAccount(userSq) == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        List<Long> roundSeatSqs = new ArrayList<>();
        for (TicketRequest req : bookingRequest.tickets()) {
            Long l = req.roundSeatSq();
            roundSeatSqs.add(l);
        }

        List<RoundSeatResponse> reservedSeats = performanceClient.reserveRoundSeats(userSq, roundSeatSqs);

        if (reservedSeats.size() != roundSeatSqs.size()) {
            throw new IllegalStateException("좌석 선점 실패: 일부 좌석을 확보하지 못했습니다.");
        }

        Booking booking = Booking.builder()
                .userSq(userSq)
                .status(BookingStatus.PENDING)
                .build();

        int totalTicketPrice = 0;

        for (RoundSeatResponse seatInfo : reservedSeats) {
            Ticket ticket = Ticket.builder()
                    .roundSeatSq(seatInfo.roundSeatSq())
                    .showName(seatInfo.showName())
                    .concertHallName(seatInfo.concertHallName())
                    .roundDateTime(seatInfo.roundDateTime())
                    .grade(seatInfo.grade())
                    .seatArea(seatInfo.seatArea())
                    .areaSeatNum(seatInfo.areaSeatNum())
                    .originalPrice(seatInfo.price())
                    .finalPrice(seatInfo.price())
                    .status(TicketStatus.RESERVED)
                    .build();

            booking.addTicket(ticket);
            totalTicketPrice += seatInfo.price();
        }

        booking.setFinalAmount(totalTicketPrice);
        booking.applyPoints(0);

        Booking savedBooking = bookingRepository.save(booking);
        log.info(">>> [Booking] 예매 데이터 저장 완료 | ID={}, PG결제액={}", savedBooking.getSq(), savedBooking.getPgAmount());

        return bookingMapper.entityToResponse(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponse applyPointToBooking(Long bookingSq, Long userSq, Long pointToUse) {
        log.info(">>> [Booking] 포인트 적용 요청 | ID={}, Point={}", bookingSq, pointToUse);

        Booking booking = findBookingOrThrow(bookingSq);
        validateOwner(booking, userSq);

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태에서만 포인트를 적용할 수 있습니다.");
        }

        AccountResponse account = accountClient.getAccount(userSq);
        long userCurrentPoint = (account.userPoint() != null) ? account.userPoint() : 0L;
        long requestPoint = (pointToUse != null) ? pointToUse : 0L;

        if (userCurrentPoint < requestPoint) {
            throw new IllegalArgumentException(
                    String.format("포인트 부족 (보유: %d / 요청: %d)", userCurrentPoint, requestPoint)
            );
        }

        booking.applyPoints((int) requestPoint);

        return bookingMapper.entityToResponse(booking);
    }

    @Override
    @Transactional
    public void confirmBooking(Long bookingSq, String paymentKey, String paymentMethod, Long userSq) {
        Booking booking = findBookingOrThrow(bookingSq);
        validateOwner(booking, userSq);
        log.info(">>> [Booking] 예매 확정 처리 | ID={}, Method={}", bookingSq, paymentMethod);

        booking.confirm(paymentKey, paymentMethod);

        if (booking.getUsedPoint() > 0) {
            try {
                accountClient.decreasePoint(userSq, new PointUseRequest((long) booking.getUsedPoint()));
            } catch (Exception e) {
                log.error(">>> [Compensate] 포인트 차감 실패: {}", e.getMessage());
                throw new IllegalStateException("포인트 차감 실패로 인해 결제를 취소합니다.");
            }
        }

        List<Long> seatIds = new ArrayList<>();
        for (Ticket ticket : booking.getTickets()) {
            Long roundSeatSq = ticket.getRoundSeatSq();
            seatIds.add(roundSeatSq);
        }

        try {
            performanceClient.confirmRoundSeats(userSq, seatIds);
        } catch (Exception e) {
            log.error(">>> [Compensate] 좌석 확정 실패 -> 포인트 환불 진행");
            if (booking.getUsedPoint() > 0) {
                try {
                    accountClient.increasePoint(userSq, new PointUseRequest((long) booking.getUsedPoint()));
                } catch (Exception refundError) {
                    log.error(">>> [CRITICAL] 포인트 환불 실패! 수동 확인 필요. UserSq={}, Amount={}",
                            booking.getUserSq(), booking.getUsedPoint());
                }
            }
            throw new IllegalStateException("좌석 확정 실패로 인해 결제를 취소합니다.");
        }
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingSq, Long userSq) {
        Booking booking = findBookingOrThrow(bookingSq);
        validateOwner(booking, userSq);

        BookingStatus status = booking.getStatus();
        log.info(">>> [Booking] 예매 전체 취소 요청 | ID={}, Status={}", bookingSq, status);

        releaseSeatsInPerformanceService(booking.getTickets());

        if (status == BookingStatus.PENDING) {
            bookingRepository.delete(booking);
            log.info(">>> [Booking] 미결제 예매 삭제 완료");
        } else if (status == BookingStatus.CONFIRMED || status == BookingStatus.PARTIAL_CANCEL) {
            booking.cancel("사용자 요청에 의한 전체 취소");
            log.info(">>> [Booking] 결제 예매 취소 처리 완료");
        } else if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예매입니다.");
        }
    }

    @Override
    @Transactional
    public void cancelTicket(Long bookingSq, Long ticketSq, Long userSq) {
        Booking booking = findBookingOrThrow(bookingSq);
        validateOwner(booking, userSq);

        BookingStatus status = booking.getStatus();
        log.info(">>> [Booking] 티켓 부분 취소 요청 | BookingID={}, TicketID={}", bookingSq, ticketSq);

        Ticket targetTicket = booking.getTickets().stream()
                .filter(t -> t.getSq().equals(ticketSq))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓입니다."));

        try {
            performanceClient.cancelRoundSeats(Collections.singletonList(targetTicket.getRoundSeatSq()));
        } catch (Exception e) {
            log.error(">>> [Booking] 부분 좌석 해제 실패 | SeatID={}", targetTicket.getRoundSeatSq());
        }

        if (status == BookingStatus.PENDING) {
            booking.deleteTicket(ticketSq);
            if (booking.getTickets().isEmpty()) {
                bookingRepository.delete(booking);
            }
        } else if (status == BookingStatus.CONFIRMED || status == BookingStatus.PARTIAL_CANCEL) {
            booking.cancelTicket(ticketSq, "사용자 요청에 의한 개별 취소");
        } else {
            throw new IllegalStateException("취소 불가능한 상태입니다.");
        }
    }

    @Override
    @Transactional
    public int cancelExpiredBookings(int expirationMinutes) {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(expirationMinutes);
        List<Booking> expiredBookings = bookingRepository.findExpired(BookingStatus.PENDING, expirationTime);

        if (expiredBookings.isEmpty()) return 0;

        int delCount = 0;
        for (Booking booking : expiredBookings) {
            try {
                releaseSeatsInPerformanceService(booking.getTickets());
                bookingRepository.delete(booking);
                delCount++;
            } catch (Exception e) {
                log.error(">>> [Booking] 만료 건 자동 삭제 실패 | ID={}, Reason={}", booking.getSq(), e.getMessage());
            }
        }
        return delCount;
    }

    @Override
    public BookingResponse getBooking(Long bookingSq, Long userSq) {
        Booking booking = findBookingOrThrow(bookingSq);
        validateOwner(booking, userSq);
        return bookingMapper.entityToResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsByUser(Long userSq) {
        List<BookingResponse> list = new ArrayList<>();
        for (Booking booking : bookingRepository.findByUserSq(userSq)) {
            BookingResponse bookingResponse = bookingMapper.entityToResponse(booking);
            list.add(bookingResponse);
        }
        return list;
    }

    @Override
    @Transactional
    public void enterTicket(String ticketUuid) {
        Ticket ticket = ticketRepository.findByUuid(ticketUuid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 티켓입니다."));
        ticket.use();
        log.info(">>> [입장 완료] TicketUuid={}", ticket.getUuid());
    }

    // --- Private Helper Methods ---

    private Booking findBookingOrThrow(Long bookingSq) {
        return bookingRepository.findBySq(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다. (ID=" + bookingSq + ")"));
    }

    private void validateOwner(Booking booking, Long userSq) {
        if (!booking.getUserSq().equals(userSq)) {
            throw new IllegalArgumentException("본인의 예매만 조회/취소/확정할 수 있습니다.");
        }
    }

    private void releaseSeatsInPerformanceService(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) return;

        List<Long> seatIds = new ArrayList<>();
        for (Ticket ticket : tickets) {
            Long roundSeatSq = ticket.getRoundSeatSq();
            seatIds.add(roundSeatSq);
        }

        try {
            performanceClient.cancelRoundSeats(seatIds);
            log.debug(">>> [Booking -> Performance] 좌석 해제 요청 완료: Seats={}", seatIds);
        } catch (Exception e) {
            log.error(">>> [Booking] 좌석 해제 요청 실패 (수동 확인 요망): {}", seatIds);
        }
    }

    // Interface에 정의되지 않은 내부용 메서드 (Controller 직접 호출 지양)
    public Booking getMyBooking(Long bookingSq, Long userSq) {
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        if (!booking.getUserSq().equals(userSq)) {
            throw new IllegalArgumentException("본인의 예매 내역만 접근할 수 있습니다.");
        }
        return booking;
    }
}