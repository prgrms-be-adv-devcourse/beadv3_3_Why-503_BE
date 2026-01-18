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
    private final AccountClient accountClient;

    /**
     * 예매 생성
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, Long userSq) {
        log.info(">>> [Biz] 예매 프로세스 시작 | UserSq={}", userSq);

        // [MSA] 회원 정보 조회
        AccountResponse accountResponse = accountClient.getAccount(userSq);
        if (accountResponse == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }
        log.info(">>> [Step 1] 회원 확인 완료 | 성함: {}", accountResponse.getName());

        // [외부 연동] 회차좌석 서비스 좌석 선점
        if (bookingRequest.getTickets() != null) {
            for (TicketRequest ticketRequest : bookingRequest.getTickets()) {
                Long seatId = ticketRequest.getShowingSeatSq();
                // TODO: [Feign Client] showingSeatClient.reserve(seatId);
                log.info(">>> [MSA] 좌석 선점 요청 | SeatId={}", seatId);
            }
        }

        // [포인트 조회 및 검증] - 선점된 상태에서 사용자가 선택한 포인트가 보유량 이내인지 확인
        int requestedPoint= 0;
        if (bookingRequest.getUsedPoint() != null) {
            requestedPoint = bookingRequest.getUsedPoint();
        }

        if (accountResponse.getPoint() < requestedPoint) {
            throw new IllegalArgumentException("보유하신 포인트가 부족하여 사용할 수 없습니다. (현재 잔액: " + accountResponse.getPoint() + "원)");
        }
        log.info(">>> [Step 3] 포인트 검증 완료 | 사용요청: {}, 보유: {}", requestedPoint, accountResponse.getPoint());

        // [결제 및 저장] - 최종 예매 데이터 생성
        Booking booking = bookingMapper.requestToEntity(bookingRequest);
        booking.setUserSq(userSq); // 헤더에서 획득한 userSq 주입

        // 최종 결제 금액 계산 (Total - UsedPoint = pgAmount)
        booking.applyPoints(requestedPoint);

        if (booking.getTickets() != null) {
            for (Ticket ticket : booking.getTickets()) {
                ticket.setBooking(booking);
            }
        }

        // [저장]
        Booking savedBooking = bookingRepository.save(booking);
        log.info(">>> [Biz] 예매 생성 완료 | BookingSq={}, TicketCount={}",
                savedBooking.getBookingSq(), savedBooking.getTickets().size());

        return bookingMapper.EntityToResponse(savedBooking);
    }

    /**
     * 예매 상세 조회
     */
    public BookingResponse getBooking(Long bookingSq) {
        Booking booking = findBookingThrow(bookingSq);
        return bookingMapper.EntityToResponse(booking);
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


    //추후 테스트 진행
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUser(Long userSq) {
        // 1. DB에서 해당 유저의 예매 리스트 조회
        List<Booking> bookings = bookingRepository.findByUserSq(userSq);
        // 2. 엔티티 리스트를 DTO 리스트로 변환 (아까 수정한 매퍼 사용)
        List<BookingResponse> list = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingResponse bookingResponse = bookingMapper.EntityToResponse(booking);
            list.add(bookingResponse);
        }
        return list;
    }
}