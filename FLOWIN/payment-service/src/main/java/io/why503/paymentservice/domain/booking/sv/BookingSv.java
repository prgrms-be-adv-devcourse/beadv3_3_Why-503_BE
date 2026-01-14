package io.why503.paymentservice.domain.booking.sv;

import io.why503.paymentservice.domain.booking.mapper.BookingMapper;
import io.why503.paymentservice.domain.booking.model.dto.BookingReqDto;
import io.why503.paymentservice.domain.booking.model.dto.BookingResDto;
import io.why503.paymentservice.domain.booking.model.dto.TicketReqDto;
import io.why503.paymentservice.domain.booking.model.ett.Booking;
import io.why503.paymentservice.domain.booking.model.ett.Ticket;
import io.why503.paymentservice.domain.booking.model.type.TicketStatus;
import io.why503.paymentservice.domain.booking.repo.BookingRepo;
import io.why503.paymentservice.domain.booking.repo.TicketRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingSv {

    private final BookingRepo bookingRepo;
    private final TicketRepo ticketRepo;
    private final BookingMapper bookingMapper;

    /**
     * 예매 생성
     * 동시성 제어를 위해 비관적 락(Pessimistic Lock)을 사용합니다.
     */
    @Transactional
    public BookingResDto createBooking(BookingReqDto req) {
        // 구매 불가 상태 정의 (이미 선점되었거나 결제된 티켓)
        List<TicketStatus> soldStatuses = List.of(TicketStatus.RESERVED, TicketStatus.PAID);

        if (req.getTickets() != null) {
            for (TicketReqDto ticketReq : req.getTickets()) {
                // [Critical] 동시성 이슈 방지: 배타적 락(Write Lock) 획득
                // 해당 좌석이 비어있음을 보장받은 상태에서만 진행
                List<Ticket> soldTickets = ticketRepo.findWithLockByShowingSeatSqAndTicketStatusIn(
                        ticketReq.getShowingSeatSq(),
                        soldStatuses
                );

                if (!soldTickets.isEmpty()) {
                    throw new IllegalStateException("이미 선택된 좌석입니다. (좌석번호: " + ticketReq.getShowingSeatSq() + ")");
                }
            }
        }

        Booking booking = bookingMapper.toEntity(req);
        Booking savedBooking = bookingRepo.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    /**
     * 예매 상세 조회
     */
    public BookingResDto getBooking(Long bookingSq) {
        // 성능 최적화: Fetch Join으로 Ticket 목록까지 즉시 로딩 (N+1 문제 방지)
        Booking booking = bookingRepo.findByIdWithTickets(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매 번호입니다: " + bookingSq));

        return bookingMapper.toDto(booking);
    }

    /**
     * 예매 확정 (결제 승인)
     */
    @Transactional
    public void confirmBooking(Long bookingSq, String paymentKey) {
        // TODO: 추후 PG사 승인 API 연동 시 트랜잭션 분리 고려 (외부 네트워크 지연 방지)

        Booking booking = bookingRepo.findByIdWithTickets(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매 번호입니다: " + bookingSq));

        booking.confirm(paymentKey, "CARD");
    }

    /**
     * 예매 취소 (전체 취소)
     */
    @Transactional
    public void cancelBooking(Long bookingSq) {
        Booking booking = bookingRepo.findByIdWithTickets(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매 번호입니다: " + bookingSq));

        booking.cancel();
    }

    /**
     * 티켓 개별 취소 (부분 취소)
     */
    @Transactional
    public void cancelTicket(Long bookingSq, Long ticketSq) {
        // TODO: 추후 PG사 부분 환불 API 연동 필요

        Booking booking = bookingRepo.findByIdWithTickets(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("예매 정보를 찾을 수 없습니다."));

        booking.cancelTicket(ticketSq, "사용자 요청에 의한 개별 취소");
    }
}