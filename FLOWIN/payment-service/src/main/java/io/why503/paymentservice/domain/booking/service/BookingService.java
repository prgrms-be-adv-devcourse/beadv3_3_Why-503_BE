package io.why503.paymentservice.domain.booking.service;

import io.why503.paymentservice.domain.booking.mapper.BookingMapper;
import io.why503.paymentservice.domain.booking.model.dto.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.BookingResponse;
import io.why503.paymentservice.domain.booking.model.dto.TicketRequest;
import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import io.why503.paymentservice.domain.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// [MSA]: 외부 서비스(Account, ShowingSeat 등) 호출
// [Biz]: 주요 비즈니스 로직 (생성, 취소, 확정)
// [Scheduler]: 스케줄러 동작
// [Err]: 에러 발생

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
//    private final AccountClient accountClient;

    /**
     * 예매 생성
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest req) {
        log.info(">>> [MSA] 회원 검증 요청 | UserSq={}", req.getUserSq());
//
//        // 1-1. 회원 정보 조회
//        AccountRes member = accountClient.getAccount(req.getUserSq());
//        if (member == null) {
//            throw new IllegalArgumentException("존재하지 않는 회원입니다. (UserSq=" + req.getUserSq() + ")");
//        }

        // 1. [외부 연동] 회차좌석 서비스 좌석 선점
        if (req.getTickets() != null) {
            for (TicketRequest ticketRequest : req.getTickets()) {
                Long seatId = ticketRequest.getShowingSeatSq();
                // TODO: [Feign Client] showingSeatClient.reserve(seatId);
                log.info(">>> [MSA] 좌석 선점 요청 | SeatId={}", seatId);
            }
        }

        // 2. [변환] 및 [연관관계 설정]
        Booking booking = bookingMapper.toEntity(req);
        if (booking.getTickets() != null) {
            for (Ticket ticket : booking.getTickets()) {
                ticket.setBooking(booking);
            }
        }

        // 3. [저장]
        Booking savedBooking = bookingRepository.save(booking);
        log.info(">>> [Biz] 예매 생성 완료 | BookingSq={}, TicketCount={}", savedBooking.getBookingSq(), savedBooking.getTickets().size());

        return bookingMapper.toDto(savedBooking);
    }

    /**
     * 예매 상세 조회
     */
    public BookingResponse getBooking(Long bookingSq) {
        Booking booking = findBookingThrow(bookingSq);
        return bookingMapper.toDto(booking);
    }

    /**
     * 예매 확정 (결제 승인)
     */
    @Transactional
    public void confirmBooking(Long bookingSq, String paymentKey) {
        Booking booking = findBookingThrow(bookingSq);
        log.info(">>> [Biz] 예매 확정 처리 시작 | BookingSq={}, PaymentKey={}", bookingSq, paymentKey);

        booking.confirm(paymentKey, "CARD");
    }

    /**
     * 예매 전체 취소
     */
    @Transactional
    public void cancelBooking(Long bookingSq) {
        Booking booking = findBookingThrow(bookingSq);
        BookingStatus status = booking.getBookingStatus();
        log.info(">>> [Biz] 예매 전체 취소 요청 | BookingSq={}, Status={}", bookingSq, status);

        if (status == BookingStatus.PENDING) {
            bookingRepository.delete(booking);
            log.info(">>> [Biz] 미결제 예매 삭제 완료 | BookingSq={}", bookingSq);

        } else if (status == BookingStatus.CONFIRMED || status == BookingStatus.PARTIAL_CANCEL) {
            booking.cancel("사용자 요청에 의한 전체 취소");
            log.info(">>> [Biz] 결제 예매 취소 처리(환불) 완료 | BookingSq={}", bookingSq);

        } else if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예매입니다. (BookingSq=" + bookingSq + ")");
        }
    }

    /**
     * 티켓 개별 취소
     */
    @Transactional
    public void cancelTicket(Long bookingSq, Long ticketSq) {
        Booking booking = findBookingThrow(bookingSq);
        BookingStatus status = booking.getBookingStatus();
        log.info(">>> [Biz] 티켓 부분 취소 요청 | BookingSq={}, TicketSq={}", bookingSq, ticketSq);

        if (status == BookingStatus.PENDING) {
            booking.deleteTicket(ticketSq);
            if (booking.getTickets().isEmpty()) {
                bookingRepository.delete(booking);
                log.info(">>> [Biz] 모든 티켓 삭제로 예매 데이터 삭제 | BookingSq={}", bookingSq);
            }
        } else if (status == BookingStatus.CONFIRMED || status == BookingStatus.PARTIAL_CANCEL) {
            booking.cancelTicket(ticketSq, "사용자 요청에 의한 개별 취소");
        } else {
            throw new IllegalStateException("취소 불가능한 예매 상태입니다. (BookingSq=" + bookingSq + ", Status=" + status + ")");
        }
    }

    /**
     * 만료된 예매 자동 취소 (스케줄러)
     */
    @Transactional
    public int cancelExpiredBookings() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(10);
        List<Booking> expiredBookings = bookingRepository.findExpired(BookingStatus.PENDING, expirationTime);

        if (expiredBookings.isEmpty()) return 0;

        log.info(">>> [Scheduler] 만료된 예매 정리 시작 | TargetCount={}", expiredBookings.size());

        int delCount = 0;
        for (Booking booking : expiredBookings) {
            try {
                // MSA 좌석 해제 요청 로직 (생략)
                // performanceClient.releaseSeats(...);

                bookingRepository.delete(booking);
                delCount++;
            } catch (Exception e) {
                log.error(">>> [Err] 만료 예매 삭제 실패 | BookingSq={}, Reason={}", booking.getBookingSq(), e.getMessage());
            }
        }

        log.info(">>> [Scheduler] 만료된 예매 정리 완료 | DeletedCount={}", delCount);
        return delCount;
    }

    // Helpers
    private Booking findBookingThrow(Long bookingSq) {
        return bookingRepository.findByBookingSq(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다. (BookingSq=" + bookingSq + ")"));
    }
}