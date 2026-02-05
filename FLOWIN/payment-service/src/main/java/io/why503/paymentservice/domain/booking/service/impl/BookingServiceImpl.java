package io.why503.paymentservice.domain.booking.service.impl;

import io.why503.paymentservice.domain.booking.mapper.BookingMapper;
import io.why503.paymentservice.domain.booking.mapper.TicketMapper;
import io.why503.paymentservice.domain.booking.model.dto.request.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.request.TicketRequest;
import io.why503.paymentservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.model.enums.BookingStatus;
import io.why503.paymentservice.domain.booking.model.enums.DiscountPolicy;
import io.why503.paymentservice.domain.booking.model.enums.TicketStatus;
import io.why503.paymentservice.domain.booking.repository.BookingRepository;
import io.why503.paymentservice.domain.booking.service.BookingService;
import io.why503.paymentservice.domain.booking.util.BookingExceptionFactory;
import io.why503.paymentservice.global.client.PerformanceClient;
import io.why503.paymentservice.global.client.dto.response.RoundSeatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 좌석 선점 및 예매 데이터의 생명주기와 상태 변경을 관리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PerformanceClient performanceClient;
    private final BookingMapper bookingMapper;
    private final TicketMapper ticketMapper;

    // 좌석 선점 처리 및 예매 정보 저장
    @Override
    @Transactional
    public BookingResponse createBooking(Long userSq, BookingRequest request) {
        /*
         * 1. 요청 검증 및 외부 좌석 선점 요청
         * 2. 선점 실패 시 보상 트랜잭션 수행
         * 3. 예매 시점의 정보를 스냅샷 형태로 티켓에 저장
         */
        if (request.tickets().isEmpty()) {
            throw BookingExceptionFactory.bookingBadRequest("티켓 정보가 없습니다.");
        }

        List<Long> seatSqs = request.tickets().stream()
                .map(ticketRequest -> ticketRequest.roundSeatSq())
                .toList();

        List<RoundSeatResponse> reservedSeats = performanceClient.reserveRoundSeats(userSq, seatSqs);

        if (reservedSeats.size() != seatSqs.size()) {
            List<Long> reservedIds = reservedSeats.stream()
                    .map(roundSeatResponse -> roundSeatResponse.roundSeatSq())
                    .toList();
            if (!reservedIds.isEmpty()) {
                performanceClient.cancelRoundSeats(reservedIds);
            }
            throw BookingExceptionFactory.bookingBadRequest("요청한 좌석 중 일부를 선점할 수 없습니다.");
        }

        String orderId = "BOOKING-" + UUID.randomUUID();
        Booking booking = Booking.builder()
                .userSq(userSq)
                .orderId(orderId)
                .build();

        Map<Long, TicketRequest> requestMap = request.tickets().stream()
                .collect(Collectors.toMap(ticketRequest -> ticketRequest.roundSeatSq(), Function.identity()));

        for (RoundSeatResponse seatInfo : reservedSeats) {
            TicketRequest ticketReq = requestMap.get(seatInfo.roundSeatSq());
            DiscountPolicy policy = DiscountPolicy.from(ticketReq.discountPolicy());
            long discountAmount = calculateDiscountAmount(seatInfo.price(), policy);

            Ticket ticket = ticketMapper.responseToEntity(booking, seatInfo, policy, discountAmount);

            booking.addTicket(ticket);
        }

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.entityToResponse(savedBooking);
    }

    // 예매 단건 상세 정보 조회
    @Override
    public BookingResponse findBooking(Long userSq, Long bookingSq) {
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("존재하지 않는 예매입니다."));

        if (!booking.getUserSq().equals(userSq)) {
            throw BookingExceptionFactory.bookingForbidden("본인의 예매 내역만 조회할 수 있습니다.");
        }

        return bookingMapper.entityToResponse(booking);
    }

    // 사용자의 과거 예매 목록 전체 조회
    @Override
    public List<BookingResponse> findBookingsByUser(Long userSq) {
        List<Booking> bookings = bookingRepository.findAllByUserSqOrderByCreatedDtDesc(userSq);

        return bookings.stream()
                .map(booking -> bookingMapper.entityToResponse(booking))
                .toList();
    }

    // 예매 취소 및 선점된 좌석 해제
    @Override
    @Transactional
    public BookingResponse cancelBooking(Long userSq, Long bookingSq, List<Long> ticketSqs, String reason) {
        /*
         * 1. 소유권 및 결제 여부 확인
         * 2. 전체 또는 선택한 티켓 단위의 부분 취소 수행
         * 3. 외부 서비스에 취소된 좌석 해제 요청
         */
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("존재하지 않는 예매입니다."));

        if (!booking.getUserSq().equals(userSq)) {
            throw BookingExceptionFactory.bookingForbidden("본인의 예매만 취소할 수 있습니다.");
        }

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw BookingExceptionFactory.bookingForbidden("이미 결제된 예매는 결제 취소를 이용해주세요.");
        }

        List<Long> canceledSeatSqs = new ArrayList<>();

        if (ticketSqs == null || ticketSqs.isEmpty()) {
            booking.cancel(reason);
            booking.getTickets().stream()
                    .map(ticket -> ticket.getRoundSeatSq())
                    .forEach(e -> canceledSeatSqs.add(e));
        }
        else {
            long currentCancelAmount = 0;

            for (Ticket ticket : booking.getTickets()) {
                if (ticketSqs.contains(ticket.getSq())) {
                    if (ticket.getStatus() == TicketStatus.CANCELLED) continue;

                    ticket.cancel();
                    currentCancelAmount += ticket.getFinalPrice();
                    canceledSeatSqs.add(ticket.getRoundSeatSq());
                }
            }

            if (canceledSeatSqs.isEmpty()) {
                throw BookingExceptionFactory.bookingBadRequest("취소 가능한 티켓이 없거나 잘못된 요청입니다.");
            }

            booking.partialCancel(currentCancelAmount);
        }

        if (!canceledSeatSqs.isEmpty()) {
            performanceClient.cancelRoundSeats(canceledSeatSqs);
        }

        return bookingMapper.entityToResponse(booking);
    }

    // 결제 기한이 지난 대기 상태의 예매 건 일괄 취소
    @Override
    @Transactional
    public int cancelExpiredBookings(int expirationMinutes) {
        LocalDateTime criteriaDt = LocalDateTime.now().minusMinutes(expirationMinutes);

        List<Booking> expiredBookings = bookingRepository.findAllByStatusAndCreatedDtBefore(
                BookingStatus.PENDING, criteriaDt
        );

        if (expiredBookings.isEmpty()) return 0;

        List<Long> seatsToRelease = new ArrayList<>();

        for (Booking booking : expiredBookings) {
            try {
                booking.cancel("입금 기한 만료 자동 취소");
                booking.getTickets().stream()
                        .map(ticket -> ticket.getRoundSeatSq())
                        .forEach(e -> seatsToRelease.add(e));
            } catch (Exception e) {
                log.error("만료 예매 취소 중 오류 (ID: {}): {}", booking.getSq(), e.getMessage());
            }
        }

        if (!seatsToRelease.isEmpty()) {
            try {
                performanceClient.cancelRoundSeats(seatsToRelease);
            } catch (Exception e) {
                log.error("자동 취소 좌석 해제 실패: {}", e.getMessage());
            }
        }

        return expiredBookings.size();
    }

    // 선택된 할인 정책 기반의 할인액 계산
    private long calculateDiscountAmount(long originalPrice, DiscountPolicy policy) {
        if (policy == null || policy == DiscountPolicy.NONE) return 0;

        double rate = switch (policy) {
            case YOUTH -> 0.1;
            case SENIOR -> 0.15;
            case DISABLED -> 0.2;
            case VETERAN -> 0.5;
            default -> 0.0;
        };
        return (long) (originalPrice * rate);
    }

    // 주문 식별자를 통한 단건 예매 조회
    @Override
    public Booking findByOrderId(String orderId) {
        return bookingRepository.findByOrderId(orderId)
                .orElse(null);
    }
}