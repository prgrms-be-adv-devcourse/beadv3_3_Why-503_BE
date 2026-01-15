package io.why503.paymentservice.domain.booking.sv;

import io.why503.paymentservice.domain.booking.mapper.BookingMapper;
import io.why503.paymentservice.domain.booking.model.dto.BookingReqDto;
import io.why503.paymentservice.domain.booking.model.dto.BookingResDto;
import io.why503.paymentservice.domain.booking.model.dto.TicketReqDto;
import io.why503.paymentservice.domain.booking.model.ett.Booking;
import io.why503.paymentservice.domain.booking.model.ett.Ticket;
import io.why503.paymentservice.domain.booking.model.vo.BookingStat;
import io.why503.paymentservice.domain.booking.repo.BookingRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingSv {

    private final BookingRepo bookingRepo;
    private final BookingMapper bookingMapper;

    /**
     * 예매 생성
     * 1. 회차좌석 서비스(Showing-Seat)에 좌석 선점 요청 (검증)
     * 2. 예매 정보 및 티켓 데이터 DB 저장 (생성)
     */
    @Transactional
    public BookingResDto createBooking(BookingReqDto req) {

        // 1. [외부 연동] 회차좌석 서비스에 좌석 선점(Lock) 요청
        // -> MSA 환경: 타 서비스의 API를 호출하여 재고(좌석) 가능 여부를 확인하고 잠금을 겁니다.
        if (req.getTickets() != null) {
            for (TicketReqDto ticketReq : req.getTickets()) {
                Long seatId = ticketReq.getShowingSeatSq();

                // TODO: [Feign Client] showingSeatClient.reserve(seatId);
                log.info(">>> [Request] 회차좌석 서비스 좌석 선점 요청 (SeatId: {})", seatId);
            }
        }

        // 2. [변환] 요청 DTO -> Booking 엔티티 변환
        Booking booking = bookingMapper.toEntity(req);

        // 3. [연관관계] Booking(부모) - Ticket(자식) 참조 설정
        // -> JPA Cascade 저장 시, Ticket 엔티티에 booking_sq(FK)가 누락되지 않도록 연결합니다.
        if (booking.getTickets() != null) {
            for (Ticket ticket : booking.getTickets()) {
                ticket.setBooking(booking);
            }
        }

        // 4. [저장] 예매 및 티켓 정보 저장
        Booking savedBooking = bookingRepo.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    /**
     * 예매 상세 조회
     * - N+1 문제를 방지하기 위해 Ticket 정보를 함께 로딩합니다.
     */
    public BookingResDto getBooking(Long bookingSq) {
        Booking booking = findBookingThrow(bookingSq);
        return bookingMapper.toDto(booking);
    }

    /**
     * 예매 확정 (결제 승인)
     * - PG사 결제가 완료된 후 호출됩니다.
     */
    @Transactional
    public void confirmBooking(Long bookingSq, String paymentKey) {
        Booking booking = findBookingThrow(bookingSq);

        // TODO: 추후 PG사 승인 API 연동 시 트랜잭션 분리 고려 (외부 네트워크 지연 방지)
        booking.confirm(paymentKey, "CARD");
    }

    /**
     * 예매 전체 취소
     * - 결제 전(PENDING): 데이터 완전 삭제 (Hard Delete)
     * - 결제 후(CONFIRMED): 취소 상태로 변경 (Soft Delete / Refund)
     */
    @Transactional
    public void cancelBooking(Long bookingSq) {
        Booking booking = findBookingThrow(bookingSq);
        BookingStat status = booking.getBookingStat();

        if (status == BookingStat.PENDING) {
            // 결제 전이므로 기록을 남기지 않고 깔끔하게 삭제
            bookingRepo.delete(booking);

        } else if (status == BookingStat.CONFIRMED || status == BookingStat.PARTIAL_CANCEL) {
            // 이미 결제된 건이므로 취소 기록을 남김 (환불 로직 필요)
            booking.cancel("사용자 요청에 의한 전체 취소");

        } else if (status == BookingStat.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예매입니다.");
        }
    }

    /**
     * 티켓 개별 취소 (부분 취소)
     * - 특정 티켓만 취소하며, 모든 티켓 취소 시 예매 전체가 취소됩니다.
     */
    @Transactional
    public void cancelTicket(Long bookingSq, Long ticketSq) {
        Booking booking = findBookingThrow(bookingSq);
        BookingStat status = booking.getBookingStat();

        if (status == BookingStat.PENDING) {
            // [결제 전] 선점 취소 -> 데이터 삭제
            booking.deleteTicket(ticketSq);

            if (booking.getTickets().isEmpty()) {
                bookingRepo.delete(booking); // 남은 티켓 없으면 예매 자체를 삭제
            }

        } else if (status == BookingStat.CONFIRMED || status == BookingStat.PARTIAL_CANCEL) {
            // [결제 후] 부분 환불 -> 상태 변경
            // TODO: PG사 부분 취소 API 연동 위치
            booking.cancelTicket(ticketSq, "사용자 요청에 의한 개별 취소");

        } else {
            throw new IllegalStateException("취소할 수 없는 예매 상태입니다. (상태: " + status + ")");
        }
    }

    /**
     * 만료된 예매 자동 취소 (스케줄러)
     * - 10분이 지나도 결제되지 않은(PENDING) 예매를 찾아 삭제합니다.
     */
    @Transactional
    public int cancelExpiredBookings() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(10);

        // 만료 대상 조회
        List<Booking> expiredBookings = bookingRepo.findExpired(
                BookingStat.PENDING,
                expirationTime
        );

        int delCount = 0;
        for (Booking booking : expiredBookings) {
            try {
                // 1. [MSA] Performance Service에 해제 요청할 좌석 ID 수집
                List<Long> seatIds = new ArrayList<>();
                for (Ticket ticket : booking.getTickets()) {
                    seatIds.add(ticket.getShowingSeatSq());
                }

                // 2. [MSA] 좌석 선점 해제 요청 (Mock)
                // performanceClient.releaseSeats(seatIds);

                // 3. 만료된 데이터 삭제
                bookingRepo.delete(booking);
                delCount++;

            } catch (Exception e) {
                log.error("[자동취소실패] bookingSq={}, reason={}", booking.getBookingSq(), e.getMessage());
            }
        }
        return delCount;
    }

    // Helpers
    private Booking findBookingThrow(Long bookingSq) {
        return bookingRepo.findByBookingSq(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매 번호입니다: " + bookingSq));
    }
}