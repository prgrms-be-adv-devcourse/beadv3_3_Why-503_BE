package io.why503.paymentservice.domain.booking.sv;

import io.why503.paymentservice.domain.booking.mapper.BookingMapper;
import io.why503.paymentservice.domain.booking.model.dto.BookingReqDto;
import io.why503.paymentservice.domain.booking.model.dto.BookingResDto;
import io.why503.paymentservice.domain.booking.model.dto.TicketReqDto;
import io.why503.paymentservice.domain.booking.model.ett.Booking;
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

    // 예매 생성
    @Transactional
    public BookingResDto createBooking(BookingReqDto req) {
        // 이미 선점(RESERVED)되었거나 결제(PAID)된 티켓은 구매 불가!
        List<TicketStatus> soldStatuses = List.of(TicketStatus.RESERVED, TicketStatus.PAID);

        if (req.getTickets() != null) {
            for (TicketReqDto ticketReq : req.getTickets()) {
                // DB에 물어봅니다: "이 좌석(101번) 혹시 팔렸나요?"
                boolean isSold = ticketRepo.existsByShowingSeatSqAndTicketStatusIn(
                        ticketReq.getShowingSeatSq(),
                        soldStatuses
                );

                // 팔렸으면 에러를 냅니다! (예매 중단)
                if (isSold) {
                    throw new IllegalStateException("이미 선택된 좌석입니다. (좌석번호: " + ticketReq.getShowingSeatSq() + ")");
                }
            }
        }

        Booking booking = bookingMapper.toEntity(req);
        Booking savedBooking = bookingRepo.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    // 예매 상세 조회
    public BookingResDto getBooking(Long bookingSq) {
        // Fetch Join을 사용하여 티켓 정보까지 한 번에 조회 (N+1 문제 방지)
        Booking booking = bookingRepo.findByIdWithTickets(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매 번호입니다: " + bookingSq));
        return bookingMapper.toDto(booking);
    }

    // 예매 확정 (결제 승인)
    @Transactional
    public void confirmBooking(Long bookingSq, String paymentKey) {
        // 추후 PG사 승인 연동 시, 외부 API 호출은 트랜잭션 범위 밖으로 분리 필요

        Booking booking = bookingRepo.findByIdWithTickets(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매 번호입니다: " + bookingSq));

        booking.confirm(paymentKey);
    }

    // 예매 취소 (전체 취소)
    @Transactional
    public void cancelBooking(Long bookingSq) {
        Booking booking = bookingRepo.findByIdWithTickets(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매 번호입니다: " + bookingSq));
        booking.cancel();
    }

    // 티켓 개별 취소 (부분 취소)
    @Transactional
    public void cancelTicket(Long bookingSq, Long ticketSq) {
        Booking booking = bookingRepo.findByIdWithTickets(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("예매 정보를 찾을 수 없습니다."));

        // 추후 PG사 환불 연동 시, 외부 API 호출은 트랜잭션 범위 밖으로 분리 필요 (DB 커넥션 점유 시간 최소화)

        booking.cancelTicket(ticketSq, "사용자 요청에 의한 개별 취소");
    }

}
