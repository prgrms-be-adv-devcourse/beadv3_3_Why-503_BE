package io.why503.paymentservice.domain.booking.service.impl;

import io.why503.paymentservice.domain.booking.mapper.BookingMapper;
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
import java.util.NoSuchElementException; // [추가] 404 처리를 위해 필요
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PerformanceClient performanceClient;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponse createBooking(Long userSq, BookingRequest request) {
        // 1. 요청 검증 (입력값 오류 -> 400 Bad Request 유지)
        if (request.tickets().isEmpty()) {
            throw new IllegalArgumentException("티켓 정보가 없습니다.");
        }

        // 2. 외부 서비스 좌석 선점 요청
        List<Long> seatSqs = request.tickets().stream()
                .map(ticketRequest -> ticketRequest.roundSeatSq())
                .toList();

        List<RoundSeatResponse> reservedSeats = performanceClient.reserveRoundSeats(userSq, seatSqs);

        // 3. 선점 결과 검증 및 보상 트랜잭션 (상태 충돌 -> 409 Conflict 유지)
        if (reservedSeats.size() != seatSqs.size()) {
            List<Long> reservedIds = reservedSeats.stream()
                    .map(roundSeatResponse -> roundSeatResponse.roundSeatSq())
                    .toList();
            if (!reservedIds.isEmpty()) {
                performanceClient.cancelRoundSeats(reservedIds);
            }
            throw new IllegalStateException("요청한 좌석 중 일부를 선점할 수 없습니다.");
        }

        // 4. Booking 엔티티 생성
        String orderId = "BOOKING-" + UUID.randomUUID();
        Booking booking = Booking.builder()
                .userSq(userSq)
                .orderId(orderId)
                .build();

        // 5. Ticket 생성 및 Booking에 추가
        Map<Long, TicketRequest> requestMap = request.tickets().stream()
                .collect(Collectors.toMap(ticketRequest -> ticketRequest.roundSeatSq(), Function.identity()));

        for (RoundSeatResponse seatInfo : reservedSeats) {
            TicketRequest ticketReq = requestMap.get(seatInfo.roundSeatSq());
            DiscountPolicy policy = DiscountPolicy.from(ticketReq.discountPolicy());
            long discountAmount = calculateDiscountAmount(seatInfo.price(), policy);

            Ticket ticket = Ticket.builder()
                    .booking(booking)
                    .roundSeatSq(seatInfo.roundSeatSq())
                    .showName(seatInfo.showName())
                    .hallName(seatInfo.concertHallName())
                    .roundDt(seatInfo.roundDateTime())
                    .seatGrade(seatInfo.grade())
                    .seatArea(seatInfo.seatArea())
                    .seatAreaNum(seatInfo.areaSeatNum())
                    .originalPrice(seatInfo.price())
                    .discountPolicy(policy)
                    .discountAmount(discountAmount)
                    .build();

            booking.addTicket(ticket);
        }

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.entityToResponse(savedBooking);
    }

    @Override
    public BookingResponse findBooking(Long userSq, Long bookingSq) {
        // [수정] 조회 실패 시 NoSuchElementException -> 404 Not Found
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예매입니다."));

        // [수정] 권한 없음 시 SecurityException -> 403 Forbidden
        if (!booking.getUserSq().equals(userSq)) {
            throw new SecurityException("본인의 예매 내역만 조회할 수 있습니다.");
        }

        return bookingMapper.entityToResponse(booking);
    }

    @Override
    public List<BookingResponse> findBookingsByUser(Long userSq) {
        List<Booking> bookings = bookingRepository.findAllByUserSqOrderByCreatedDtDesc(userSq);

        return bookings.stream()
                .map(booking -> bookingMapper.entityToResponse(booking))
                .toList();
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long userSq, Long bookingSq, List<Long> ticketSqs, String reason) {
        // [수정] 조회 실패 시 NoSuchElementException -> 404 Not Found
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예매입니다."));

        // [수정] 권한 없음 시 SecurityException -> 403 Forbidden
        if (!booking.getUserSq().equals(userSq)) {
            throw new SecurityException("본인의 예매만 취소할 수 있습니다.");
        }

        // 이미 완료된 건 취소 불가 -> 409 Conflict 유지
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalStateException("이미 결제된 예매는 결제 취소를 이용해주세요.");
        }

        List<Long> canceledSeatSqs = new ArrayList<>();

        // 1. 전체 취소
        if (ticketSqs == null || ticketSqs.isEmpty()) {
            booking.cancel(reason);
            booking.getTickets().stream()
                    .map(ticket -> ticket.getRoundSeatSq())
                    .forEach(e -> canceledSeatSqs.add(e));
        }
        // 2. 부분 취소
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

            // 잘못된 티켓 ID 요청 -> 400 Bad Request 유지
            if (canceledSeatSqs.isEmpty()) {
                throw new IllegalArgumentException("취소 가능한 티켓이 없거나 잘못된 요청입니다.");
            }

            booking.partialCancel(currentCancelAmount);
        }

        if (!canceledSeatSqs.isEmpty()) {
            performanceClient.cancelRoundSeats(canceledSeatSqs);
        }

        return bookingMapper.entityToResponse(booking);
    }

    // 스케줄러용 메서드 (내부 로직)
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

    @Override
    public Booking findByOrderId(String orderId) {
        return bookingRepository.findByOrderId(orderId)
                .orElse(null);
    }
}