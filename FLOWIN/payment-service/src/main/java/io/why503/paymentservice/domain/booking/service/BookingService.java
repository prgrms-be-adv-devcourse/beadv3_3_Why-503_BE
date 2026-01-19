package io.why503.paymentservice.domain.booking.service;

import io.why503.paymentservice.domain.booking.mapper.BookingMapper;
import io.why503.paymentservice.domain.booking.model.dto.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.BookingResponse;
import io.why503.paymentservice.domain.booking.model.dto.TicketRequest;
import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import io.why503.paymentservice.domain.booking.repository.BookingRepository;
import io.why503.paymentservice.global.client.AccountClient;
import io.why503.paymentservice.global.client.dto.AccountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 예매 서비스 (비즈니스 로직)
 * - 담당: 예매 생성, 조회, 취소, 확정(결제 승인)
 * - 통신: AccountService(회원), ShowingSeatService(좌석)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final AccountClient accountClient;

    /**
     * 예매 생성 (Pre-Booking)
     * [Step 1] 회원 조회 -> [Step 2] 좌석 선점 -> [Step 3] 포인트 검증 -> [Step 4] 저장
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, Long userSq) {
        log.info(">>> [Step 1] 예매 생성 시작 | UserSq={}", userSq);

        // 1. [MSA] 회원 정보 조회
        AccountResponse accountResponse = accountClient.getAccount(userSq);
        if (accountResponse == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다. (UserSq=" + userSq + ")");
        }

        // 2. [MSA] 좌석 선점 요청
        if (bookingRequest.getTickets() != null) {
            for (TicketRequest ticketRequest : bookingRequest.getTickets()) {
                Long seatId = ticketRequest.getShowingSeatSq();
                // TODO: [Feign Client] showingSeatClient.reserve(seatId);
                log.info(">>> [Step 2] 좌석 선점 요청 | SeatId={}", seatId);
            }
        }

        // 3. 포인트 사용 검증
        int requestedPoint = 0;
        if (bookingRequest.getUsedPoint() != null) {
            requestedPoint = bookingRequest.getUsedPoint();
        }

        if (accountResponse.getPoint() < requestedPoint) {
            throw new IllegalArgumentException(String.format("보유 포인트가 부족합니다. (보유: %d원 / 요청: %d원)",
                    accountResponse.getPoint(), requestedPoint));
        }
        log.info(">>> [Step 3] 검증 완료 | 회원명: {}, 사용포인트: {}", accountResponse.getName(), requestedPoint);

        // 4. 엔티티 변환 및 데이터 결합
        Booking booking = bookingMapper.requestToEntity(bookingRequest);
        booking.setUserSq(userSq);           // User 정보 주입
        booking.applyPoints(requestedPoint); // 금액 계산 (Total - Point = PgAmount)

        // 연관관계 설정
        if (booking.getTickets() != null) {
            for (Ticket ticket : booking.getTickets()) {
                ticket.setBooking(booking);
            }
        }

        // 5. DB 저장
        Booking savedBooking = bookingRepository.save(booking);
        log.info(">>> [Step 4] 예매 데이터 저장 완료 | BookingSq={}, 결제대상금액(PG)={}",
                savedBooking.getBookingSq(), savedBooking.getPgAmount());

        return bookingMapper.EntityToResponse(savedBooking);
    }

    /**
     * 예매 단건 상세 조회
     */
    public BookingResponse getBooking(Long bookingSq) {
        Booking booking = findBookingThrow(bookingSq);
        return bookingMapper.EntityToResponse(booking);
    }

    /**
     * 사용자별 예매 내역 조회
     */
    public List<BookingResponse> getBookingsByUser(Long userSq) {
        List<Booking> bookings = bookingRepository.findByUserSq(userSq);

        List<BookingResponse> responseList = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingResponse response = bookingMapper.EntityToResponse(booking);
            responseList.add(response);
        }

        return responseList;
    }

    /**
     * 예매 확정 (결제 승인)
     * - PG 결제 성공 후 호출되어 상태를 확정(CONFIRMED)함
     */
    @Transactional
    public void confirmBooking(Long bookingSq, String paymentKey) {
        Booking booking = findBookingThrow(bookingSq);
        log.info(">>> [Biz] 예매 확정(승인) 처리 시작 | BookingSq={}, PaymentKey={}", bookingSq, paymentKey);

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
            log.info(">>> [Biz] 미결제 예매 삭제 완료 (Hard Delete)");

        } else if (status == BookingStatus.CONFIRMED || status == BookingStatus.PARTIAL_CANCEL) {
            booking.cancel("사용자 요청에 의한 전체 취소");
            log.info(">>> [Biz] 결제 예매 취소 완료 (Soft Delete/환불)");

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
            // 남은 티켓이 없으면 예매 자체를 삭제
            if (booking.getTickets().isEmpty()) {
                bookingRepository.delete(booking);
                log.info(">>> [Biz] 잔여 티켓 없음으로 예매 데이터 삭제");
            }
        } else if (status == BookingStatus.CONFIRMED || status == BookingStatus.PARTIAL_CANCEL) {
            booking.cancelTicket(ticketSq, "사용자 요청에 의한 개별 취소");
        } else {
            throw new IllegalStateException("취소 불가능한 예매 상태입니다. (Status=" + status + ")");
        }
    }

    /**
     * 만료된 예매 자동 취소 (스케줄러용)
     * - 10분 이상 결제 대기(PENDING) 상태인 건 삭제
     */
    @Transactional
    public int cancelExpiredBookings() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(10);
        List<Booking> expiredBookings = bookingRepository.findExpired(BookingStatus.PENDING, expirationTime);

        if (expiredBookings.isEmpty()) return 0;

        log.info(">>> [Scheduler] 만료된 예매 정리 시작 | 대상 건수={}", expiredBookings.size());

        int delCount = 0;
        for (Booking booking : expiredBookings) {
            try {
                // TODO: [MSA] 좌석 점유 해제 요청 로직
                // performanceClient.releaseSeats(...)

                bookingRepository.delete(booking);
                delCount++;
            } catch (Exception e) {
                log.error(">>> [Err] 예매 삭제 실패 | BookingSq={}, Reason={}", booking.getBookingSq(), e.getMessage());
            }
        }

        log.info(">>> [Scheduler] 만료된 예매 정리 완료 | 삭제 건수={}", delCount);
        return delCount;
    }

    // --- Private Methods ---

    private Booking findBookingThrow(Long bookingSq) {
        return bookingRepository.findByBookingSq(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다. (BookingSq=" + bookingSq + ")"));
    }
}