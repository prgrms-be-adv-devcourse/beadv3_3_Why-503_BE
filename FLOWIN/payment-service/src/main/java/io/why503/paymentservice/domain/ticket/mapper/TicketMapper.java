package io.why503.paymentservice.domain.ticket.mapper;

import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import io.why503.paymentservice.domain.ticket.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.ticket.model.entity.Ticket;
import org.springframework.stereotype.Component;

/**
 * 티켓 데이터의 엔티티와 응답 객체 간 변환을 관리하는 컴포넌트
 * - 도메인 모델을 클라이언트 제공 형식으로 가공 및 추출
 */
@Component
public class TicketMapper {

    // 도메인 엔티티를 외부 노출용 데이터 구조로 가공
    public TicketResponse entityToResponse(Ticket ticket) {
        if (ticket == null) {
            throw PaymentExceptionFactory.paymentBadRequest("변환할 Ticket Entity는 필수입니다.");
        }

        String status = ticket.isSold() ? "SOLD" : "AVAILABLE";

        return new TicketResponse(
                ticket.getSq(),
                ticket.getRoundSeatSq(),
                ticket.getBookingSq(),
                ticket.getUserSq(),
                status,
                ticket.getOriginalPrice(),
                ticket.getDiscountPolicy(),
                ticket.getFinalPrice(),
                ticket.getCreatedDt()
        );
    }

    // 신규 회차 좌석 정보에 할당할 티켓 기본 데이터 생성
    public Ticket requestToEntity(Long roundSeatSq) {
        if (roundSeatSq == null || roundSeatSq <= 0) {
            throw PaymentExceptionFactory.paymentBadRequest("티켓 생성을 위한 좌석 ID는 필수이며 양수여야 합니다.");
        }

        return Ticket.builder()
                .roundSeatSq(roundSeatSq)
                .build();
    }
}