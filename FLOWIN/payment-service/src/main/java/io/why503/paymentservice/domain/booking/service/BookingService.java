package io.why503.paymentservice.domain.booking.service;

import io.why503.paymentservice.domain.booking.mapper.BookingMapper;
import io.why503.paymentservice.domain.booking.model.dto.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.BookingResponse;
import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import io.why503.paymentservice.domain.booking.model.vo.TicketStatus;
import io.why503.paymentservice.domain.booking.repository.BookingRepository;
import io.why503.paymentservice.domain.booking.repository.TicketRepository;
import io.why503.paymentservice.global.client.AccountClient;
import io.why503.paymentservice.global.client.PerformanceClient;
import io.why503.paymentservice.global.client.dto.AccountResponse;
import io.why503.paymentservice.global.client.dto.PointUseRequest;
import io.why503.paymentservice.global.client.dto.RoundSeatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 예매 서비스 (Core Business Logic)
 * - 담당: 예매 생성, 조회, 취소, 확정
 * - 연동: AccountService(유저), PerformanceService(공연/좌석)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final BookingMapper bookingMapper;
    private final AccountClient accountClient;
    private final PerformanceClient performanceClient;

    /**
     * 예매 생성 (Pre-Booking)
     * - 공연 서비스에서 좌석 정보를 가져와 티켓 스냅샷을 생성합니다.
     * - 포인트 검증 후 결제 대기(PENDING) 상태로 저장합니다.
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, Long userSq) {
        log.info(">>> [Booking] 예매 생성 요청 | UserSq={}", userSq);

        // 1. 회원 정보 조회
        if (accountClient.getAccount(userSq) == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        // 2. 좌석 선점 요청 ID 추출 (람다식 유지)
        List<Long> roundSeatIds = bookingRequest.getTickets().stream()
                .map(ticketRequest -> ticketRequest.getRoundSeatSq())
                .collect(Collectors.toList());

        // 3. 공연 서비스 호출 (좌석 선점 및 정보 수신)
        List<RoundSeatResponse> reservedSeats = performanceClient.reserveRoundSeats(roundSeatIds);

        if (reservedSeats.size() != roundSeatIds.size()) {
            throw new IllegalStateException("좌석 선점 실패: 일부 좌석을 확보하지 못했습니다.");
        }

        // 4. 예매 엔티티 생성
        Booking booking = Booking.builder()
                .userSq(userSq)
                .bookingStatus(BookingStatus.PENDING)
                .build();

        int totalTicketPrice = 0;

        // 5. 티켓 스냅샷 생성
        for (RoundSeatResponse seatInfo : reservedSeats) {
            Ticket ticket = Ticket.builder()
                    .roundSeatSq(seatInfo.getRoundSeatSq())
                    .showName(seatInfo.getShowName())
                    .concertHallName(seatInfo.getConcertHallName())
                    .roundDate(seatInfo.getRoundDate())
                    .grade(seatInfo.getGrade())
                    .seatArea(seatInfo.getSeatArea())
                    .areaSeatNumber(seatInfo.getAreaSeatNumber())
                    .originalPrice(seatInfo.getPrice())
                    .finalPrice(seatInfo.getPrice())
                    .ticketStatus(TicketStatus.RESERVED)
                    .build();

            booking.addTicket(ticket);
            totalTicketPrice += seatInfo.getPrice();
        }

        // 6. 금액 및 포인트 설정
        booking.setTotalAmount(totalTicketPrice);
        booking.applyPoints(0); // 초기화

        // 7. 저장
        Booking savedBooking = bookingRepository.save(booking);
        log.info(">>> [Booking] 예매 데이터 저장 완료 | ID={}, PG결제액={}", savedBooking.getBookingSq(), savedBooking.getPgAmount());

        return bookingMapper.EntityToResponse(savedBooking);
    }

    /**
     * 포인트 적용 (사용자 선택)
     * - 사용자가 입력한 포인트를 검증하고 최종 결제 금액에 반영합니다.
     */
    @Transactional
    public BookingResponse applyPointToBooking(Long bookingSq, Long userSq, Integer pointToUse) {
        log.info(">>> [Booking] 포인트 적용 요청 | ID={}, Point={}", bookingSq, pointToUse);

        Booking booking = findBookingOrThrow(bookingSq);

        if (!booking.getUserSq().equals(userSq)) {
            throw new IllegalArgumentException("본인의 예매만 수정할 수 있습니다.");
        }

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태에서만 포인트를 적용할 수 있습니다.");
        }

        // 1. 유저 보유 포인트 조회 및 검증
        AccountResponse account = accountClient.getAccount(userSq);
        int requestPoint = (pointToUse != null) ? pointToUse : 0;

        if (account.getPoint() < requestPoint) {
            throw new IllegalArgumentException(String.format("포인트 부족 (보유: %d / 요청: %d)", account.getPoint(), requestPoint));
        }

        // 2. 포인트 적용 (Entity 내부에서 pgAmount 재계산됨)
        booking.applyPoints(requestPoint);

        return bookingMapper.EntityToResponse(booking);
    }

    /**
     * 예매 확정
     * - PG사 결제 승인 후 호출되어 예매 상태를 'CONFIRMED'로 변경합니다.
     * - 공연 서비스에 판매 완료(SOLD)를 통보합니다.
     */
    @Transactional
    public void confirmBooking(Long bookingSq, String paymentKey, String paymentMethod) {
        Booking booking = findBookingOrThrow(bookingSq);
        log.info(">>> [Booking] 예매 확정 처리 | ID={}, Method={}", bookingSq, paymentMethod);

        // 1. 내부 상태 변경
        booking.confirm(paymentKey, paymentMethod);

        // 2. 포인트 사용 처리 (사용한 포인트가 있을 경우에만)
        if (booking.getUsedPoint() > 0) {
            log.info(">>> [Booking] 포인트 차감 요청 | UserSq={}, Amount={}", booking.getUserSq(), booking.getUsedPoint());
            try {
                accountClient.usePoint(PointUseRequest.builder()
                        .userSq(booking.getUserSq())
                        .amount(booking.getUsedPoint())
                        .build());
            } catch (Exception e) {
                log.error(">>> [Compensate] 포인트 차감 실패: {}", e.getMessage());
                throw new IllegalStateException("포인트 차감 실패로 인해 결제를 취소합니다.");
            }
        }

        // 3. 공연 서비스 확정 통보 (람다식 유지)
        List<Long> seatIds = booking.getTickets().stream()
                .map(ticket -> ticket.getRoundSeatSq())
                .collect(Collectors.toList());

        try {
            performanceClient.confirmRoundSeats(seatIds);
        } catch (Exception e) {
            // [중요] 좌석 확정 실패 -> "포인트 환불" 보상 트랜잭션 수행해야 함
            log.error(">>> [Compensate] 좌석 확정 실패 -> 포인트 환불 진행");

            if (booking.getUsedPoint() > 0) {
                try {
                    // 아까 깎았던 포인트 다시 돌려주기
                    accountClient.refundPoint(PointUseRequest.builder()
                            .userSq(booking.getUserSq())
                            .amount(booking.getUsedPoint())
                            .build());
                    log.info(">>> [Compensate] 포인트 환불 완료");
                } catch (Exception refundError) {
                    // 최악의 상황: 결제 취소도 해야 하는데 환불도 실패함.
                    // 이런 로그는 별도 파일로 남겨서 개발자가 수동 처리해야 함 (Alery, Slack 등)
                    log.error(">>> [CRITICAL] 포인트 환불 실패! 수동 확인 필요. UserSq={}, Amount={}",
                            booking.getUserSq(), booking.getUsedPoint());
                }
            }

            // 보상 처리 후 예외를 던져서 내부 DB(Booking)를 롤백시키고,
            // 상위(PaymentService)로 에러를 전파해야 함 (거기서 PG 취소 하게)
            throw new IllegalStateException("좌석 확정 실패로 인해 결제를 취소합니다.");
        }
    }

    /**
     * 예매 전체 취소
     * - 결제 전: 데이터 삭제 (Hard Delete)
     * - 결제 후: 취소 상태 변경 (Soft Delete) 및 환불 처리
     */
    @Transactional
    public void cancelBooking(Long bookingSq) {
        Booking booking = findBookingOrThrow(bookingSq);
        BookingStatus status = booking.getBookingStatus();
        log.info(">>> [Booking] 예매 전체 취소 요청 | ID={}, Status={}", bookingSq, status);

        // 1. 외부 서비스 좌석 해제
        releaseSeatsInShowService(booking.getTickets());

        // 2. 상태별 처리
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

    /**
     * 티켓 개별(부분) 취소
     * - 티켓 하나만 취소하며, 남은 티켓이 없으면 전체 취소로 자동 전환됩니다.
     */
    @Transactional
    public void cancelTicket(Long bookingSq, Long ticketSq) {
        Booking booking = findBookingOrThrow(bookingSq);
        BookingStatus status = booking.getBookingStatus();
        log.info(">>> [Booking] 티켓 부분 취소 요청 | BookingID={}, TicketID={}", bookingSq, ticketSq);

        // 1. 대상 티켓 찾기 (람다식 유지)
        Ticket targetTicket = booking.getTickets().stream()
                .filter(t -> t.getTicketSq().equals(ticketSq))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓입니다."));

        // 2. 외부 서비스 좌석 해제 (단건)
        List<Long> seatIdList = new ArrayList<>();
        seatIdList.add(targetTicket.getRoundSeatSq());

        try {
            performanceClient.cancelRoundSeats(seatIdList);
        } catch (Exception e) {
            log.error(">>> [Booking] 부분 좌석 해제 실패 | SeatID={}", targetTicket.getRoundSeatSq());
        }

        // 3. 상태별 처리
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

    /**
     * 자동 취소 (스케줄러용)
     * - 결제 대기 상태로 10분이 지난 건들을 삭제합니다.
     */
    @Transactional
    public int cancelExpiredBookings() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(10);
        List<Booking> expiredBookings = bookingRepository.findExpired(BookingStatus.PENDING, expirationTime);

        if (expiredBookings.isEmpty()) return 0;

        int delCount = 0;
        for (Booking booking : expiredBookings) {
            try {
                releaseSeatsInShowService(booking.getTickets());
                bookingRepository.delete(booking);
                delCount++;
            } catch (Exception e) {
                log.error(">>> [Booking] 자동 취소 실패 | ID={}", booking.getBookingSq());
            }
        }
        return delCount;
    }

    /**
     * 예매 단건 조회
     */
    public BookingResponse getBooking(Long bookingSq) {
        return bookingMapper.EntityToResponse(findBookingOrThrow(bookingSq));
    }

    /**
     * 사용자별 예매 목록 조회
     */
    public List<BookingResponse> getBookingsByUser(Long userSq) {
        return bookingRepository.findByUserSq(userSq).stream()
                .map(booking -> bookingMapper.EntityToResponse(booking)) // 람다식 유지
                .collect(Collectors.toList());
    }

    /**
     * QR 입장 처리
     */
    @Transactional
    public void enterTicket(String ticketUuid) {
        Ticket ticket = ticketRepository.findByTicketUuid(ticketUuid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 티켓입니다."));
        ticket.use();
        log.info(">>> [입장 완료] TicketUuid={}", ticket.getTicketUuid());
    }

    // --- Private Helper Methods ---

    private Booking findBookingOrThrow(Long bookingSq) {
        return bookingRepository.findByBookingSq(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다. (ID=" + bookingSq + ")"));
    }

    private void releaseSeatsInShowService(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) return;

        List<Long> seatIds = tickets.stream()
                .map(ticket -> ticket.getRoundSeatSq()) // 람다식 유지
                .collect(Collectors.toList());

        try {
            performanceClient.cancelRoundSeats(seatIds);
        } catch (Exception e) {
            log.error(">>> [Booking] 좌석 해제 요청 실패 | IDs={}", seatIds);
        }
    }
}
