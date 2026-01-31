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
        // 1. 요청 검증
        if (request.tickets().isEmpty()) {
            throw new IllegalArgumentException("티켓 정보가 없습니다.");
        }

        // 2. 외부 서비스 좌석 선점 요청
        List<Long> seatSqs = request.tickets().stream()
                .map(ticketRequest -> ticketRequest.roundSeatSq())
                .toList();

        List<RoundSeatResponse> reservedSeats = performanceClient.reserveRoundSeats(userSq, seatSqs);

        // 3. 선점 결과 검증 및 보상 트랜잭션
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
        // orderId 생성 (중복 방지를 위해 UUID 사용)
        String orderId = "BOOKING-" + UUID.randomUUID();
        Booking booking = Booking.builder()
                .userSq(userSq)
                .orderId(orderId)
                .build();

        // 5. Ticket 생성 및 Booking에 추가 (Cascade 활용)
        // 요청 정보를 Map으로 변환하여 매핑 효율화
        Map<Long, TicketRequest> requestMap = request.tickets().stream()
                .collect(Collectors.toMap(ticketRequest -> ticketRequest.roundSeatSq(), Function.identity()));

        for (RoundSeatResponse seatInfo : reservedSeats) {
            TicketRequest ticketReq = requestMap.get(seatInfo.roundSeatSq());
            DiscountPolicy policy = DiscountPolicy.from(ticketReq.discountPolicy());
            long discountAmount = calculateDiscountAmount(seatInfo.price(), policy);

            Ticket ticket = Ticket.builder()
                    .booking(booking) // 연관관계 설정 (생성자 시점)
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

            // [중요] addTicket 내부에서 금액(finalAmount) 누적 계산됨
            booking.addTicket(ticket);
        }

        // 6. 통합 저장 (CascadeType.ALL 덕분에 티켓도 같이 저장됨)
        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.entityToResponse(savedBooking);
    }

    @Override
    public BookingResponse findBooking(Long userSq, Long bookingSq) {
        // Repository에서 @EntityGraph로 Tickets까지 이미 가져왔음
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        if (!booking.getUserSq().equals(userSq)) {
            throw new IllegalArgumentException("본인의 예매 내역만 조회할 수 있습니다.");
        }

        return bookingMapper.entityToResponse(booking);
    }

    @Override
    public List<BookingResponse> findBookingsByUser(Long userSq) {
        // Repository에서 @EntityGraph로 Tickets까지 이미 가져왔음 (N+1 해결)
        List<Booking> bookings = bookingRepository.findAllByUserSqOrderByCreatedDtDesc(userSq);

        return bookings.stream()
                .map(booking -> bookingMapper.entityToResponse(booking))
                .toList();
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long userSq, Long bookingSq, List<Long> ticketSqs, String reason) {
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        if (!booking.getUserSq().equals(userSq)) {
            throw new IllegalArgumentException("본인의 예매만 취소할 수 있습니다.");
        }

        // 결제 완료된 건은 환불 불가 안내 (PaymentService 통해야 함)
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalStateException("이미 결제된 예매는 결제 취소를 이용해주세요.");
        }

        List<Long> canceledSeatSqs = new ArrayList<>();

        // 1. 전체 취소
        if (ticketSqs == null || ticketSqs.isEmpty()) {
            booking.cancel(reason);
            // 취소된 좌석 ID 수집
            booking.getTickets().stream()
                    .map(ticket -> ticket.getRoundSeatSq())
                    .forEach(e -> canceledSeatSqs.add(e));
        }
        // 2. 부분 취소
        else {
            long currentCancelAmount = 0;

            // Booking 내의 티켓 목록을 순회하며 처리
            for (Ticket ticket : booking.getTickets()) {
                if (ticketSqs.contains(ticket.getSq())) {
                    if (ticket.getStatus() == TicketStatus.CANCELLED) continue;

                    ticket.cancel();
                    currentCancelAmount += ticket.getFinalPrice();
                    canceledSeatSqs.add(ticket.getRoundSeatSq());
                }
            }

            // 유효성 검증: 요청한 티켓 중 실제로 취소된 게 하나도 없다면? (잘못된 ID 등)
            if (canceledSeatSqs.isEmpty()) {
                throw new IllegalArgumentException("취소 가능한 티켓이 없거나 잘못된 요청입니다.");
            }

            // Booking 상태 업데이트 (부분취소 금액 반영)
            booking.partialCancel(currentCancelAmount);
        }

        // 3. 외부 서비스 좌석 해제 요청
        if (!canceledSeatSqs.isEmpty()) {
            performanceClient.cancelRoundSeats(canceledSeatSqs);
        }

        return bookingMapper.entityToResponse(booking);
    }

    // [추가됨] 스케줄러용 만료 예매 정리
    @Override
    @Transactional
    public int cancelExpiredBookings(int expirationMinutes) {
        LocalDateTime criteriaDt = LocalDateTime.now().minusMinutes(expirationMinutes);

        // EntityGraph 덕분에 티켓 정보도 같이 로드됨
        List<Booking> expiredBookings = bookingRepository.findAllByStatusAndCreatedDtBefore(
                BookingStatus.PENDING, criteriaDt
        );

        if (expiredBookings.isEmpty()) return 0;

        List<Long> seatsToRelease = new ArrayList<>();

        for (Booking booking : expiredBookings) {
            try {
                // DB 상태 취소 처리
                booking.cancel("입금 기한 만료 자동 취소");

                // 해제 대상 좌석 수집
                booking.getTickets().stream()
                        .map(ticket -> ticket.getRoundSeatSq())
                        .forEach(e -> seatsToRelease.add(e));

            } catch (Exception e) {
                log.error("만료 예매 취소 중 오류 (ID: {}): {}", booking.getSq(), e.getMessage());
            }
        }

        // 외부 서비스 호출 (배치 처리)
        if (!seatsToRelease.isEmpty()) {
            try {
                performanceClient.cancelRoundSeats(seatsToRelease);
            } catch (Exception e) {
                log.error("자동 취소 좌석 해제 실패: {}", e.getMessage());
                // 필요 시 재시도 큐에 적재 로직 추가
            }
        }

        return expiredBookings.size();
    }

    // 할인 계산 로직
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
        // 규칙: 람다식 사용 (n) -> n
        // 조회 결과가 없으면 null 반환 (호출하는 쪽에서 null 체크 수행)
        return bookingRepository.findByOrderId(orderId)
                .orElse(null);
    }
}