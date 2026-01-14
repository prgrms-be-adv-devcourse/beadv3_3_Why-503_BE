package io.why503.paymentservice.domain.booking.sv;

import io.why503.paymentservice.domain.booking.mapper.BookingMapper;
import io.why503.paymentservice.domain.booking.model.dto.BookingReqDto;
import io.why503.paymentservice.domain.booking.model.dto.BookingResDto;
import io.why503.paymentservice.domain.booking.model.dto.TicketReqDto;
import io.why503.paymentservice.domain.booking.model.ett.Booking;
import io.why503.paymentservice.domain.booking.model.ett.Ticket;
import io.why503.paymentservice.domain.booking.model.type.BookingStatus;
import io.why503.paymentservice.domain.booking.model.type.TicketStatus;
import io.why503.paymentservice.domain.booking.repo.BookingRepo;
import io.why503.paymentservice.domain.booking.repo.TicketRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
     */
    @Transactional
    public BookingResDto createBooking(BookingReqDto req) {
        // 이미 팔린 것으로 간주할 상태들
        List<TicketStatus> soldStatuses = List.of(TicketStatus.RESERVED, TicketStatus.PAID);

        if (req.getTickets() != null) {
            for (TicketReqDto ticketReq : req.getTickets()) {

                boolean isSold = ticketRepo.existsByShowingSeatSqAndTicketStatusIn(
                        ticketReq.getShowingSeatSq(),
                        soldStatuses
                );

                if (isSold) {
                    throw new IllegalStateException("이미 선택된 좌석입니다. (좌석번호: " + ticketReq.getShowingSeatSq() + ")");
                }
            }
        }

        try {
            // 검증 통과 -> 예매 생성
            Booking booking = bookingMapper.toEntity(req);
            Booking savedBooking = bookingRepo.save(booking);
            return bookingMapper.toDto(savedBooking);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("동시에 다른 사용자가 좌석을 선점했습니다. 다시 시도해주세요.");
        }
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
     * 예매 전체 취소
     * - 결제 전(PENDING): 데이터 완전 삭제 (Delete)
     * - 결제 후(CONFIRMED): 취소 상태로 변경 (Soft Delete / Refund)
     */
    @Transactional
    public void cancelBooking(Long bookingSq) {
        Booking booking = bookingRepo.findByIdWithTickets(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매 번호입니다: " + bookingSq));

        // [분기 처리]
        if (booking.getBookingStatus() == BookingStatus.PENDING) {
            // 1. 결제 전: 깔끔하게 삭제
            // CascadeType.ALL 덕분에 딸려있는 Ticket들도 같이 삭제됩니다.
            bookingRepo.delete(booking);

        } else if (booking.getBookingStatus() == BookingStatus.CONFIRMED ||
                booking.getBookingStatus() == BookingStatus.PARTIAL_CANCEL) {
            // 2. 결제 후: 기록 남기고 취소 처리
            // TODO: PG사 전체 환불 API 연동 필요
            booking.cancel("사용자 요청에 의한 전체 취소");

        } else if (booking.getBookingStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예매입니다.");
        }
    }

    /**
     * 티켓 개별 취소 (부분 취소)
     */
    @Transactional
    public void cancelTicket(Long bookingSq, Long ticketSq) {
        // 1. Booking 조회 (티켓과 함께)
        Booking booking = bookingRepo.findByIdWithTickets(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("예매 정보를 찾을 수 없습니다."));

        // 2. 현재 상태에 따른 분기 처리
        if (booking.getBookingStatus() == BookingStatus.PENDING) {
            // A. 결제 전: 선점 취소 (데이터 삭제)
            booking.deleteTicket(ticketSq);

            // 티켓이 하나도 안 남았다면 예매 자체를 삭제
            if (booking.getTickets().isEmpty()) {
                bookingRepo.delete(booking);
            }

        } else if (booking.getBookingStatus() == BookingStatus.CONFIRMED ||
                booking.getBookingStatus() == BookingStatus.PARTIAL_CANCEL) {
            // B. 결제 후: 부분 환불 (데이터 유지, 상태 변경)
            // TODO: PG사 부분 취소 API 연동 위치
            booking.cancelTicket(ticketSq, "사용자 요청에 의한 개별 취소");

        } else {
            throw new IllegalStateException("취소할 수 없는 예매 상태입니다. (상태: " + booking.getBookingStatus() + ")");
        }

        // Dirty Checking으로 인해 변경 사항(삭제 or 상태변경)이 자동 반영됨
    }
}