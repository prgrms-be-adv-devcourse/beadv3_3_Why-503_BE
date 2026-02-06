package io.why503.paymentservice.domain.ticket.mapper;

import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import io.why503.paymentservice.domain.ticket.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.ticket.model.entity.Ticket;
import org.springframework.stereotype.Component;

/**
 * 티켓 엔티티와 DTO 간의 데이터 변환을 담당하는 컴포넌트
 * - 엔티티의 상태(isSold)를 기반으로 응답용 상태 문자열을 생성
 */
@Component
public class TicketMapper {

    // 티켓 엔티티를 조회 응답 객체로 변환
    public TicketResponse toResponse(Ticket ticket) {
        if (ticket == null) {
            throw PaymentExceptionFactory.paymentBadRequest("변환할 Ticket Entity는 필수입니다.");
        }

        // 판매 여부에 따라 상태 문자열 결정
        String status = ticket.isSold() ? "SOLD" : "AVAILABLE";

        return new TicketResponse(
                ticket.getSq(),
                ticket.getRoundSeatSq(),
                ticket.getBookingSq(),
                ticket.getUserSq(),
                status,
                ticket.getOriginalPrice(),
                ticket.getDiscount(),
                ticket.getFinalPrice(),
                ticket.getCreatedDt()
        );
    }

    // 좌석 ID를 받아 초기 상태의 티켓 엔티티 생성
    public Ticket toEntity(Long roundSeatSq) {
        if (roundSeatSq == null || roundSeatSq <= 0) {
            throw PaymentExceptionFactory.paymentBadRequest("티켓 생성을 위한 좌석 ID(RoundSeatSq)는 필수이며 양수여야 합니다.");
        }

        return Ticket.builder()
                .roundSeatSq(roundSeatSq)
                .build();
    }
}