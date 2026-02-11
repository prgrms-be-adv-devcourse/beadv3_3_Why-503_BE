package io.why503.paymentservice.domain.ticket.service;

import io.why503.paymentservice.domain.payment.model.entity.Payment;
import io.why503.paymentservice.domain.ticket.model.dto.request.TicketCreateRequest;
import io.why503.paymentservice.domain.ticket.model.dto.response.TicketResponse;
import io.why503.paymentservice.global.client.dto.response.BookingSeatResponse;

import java.util.List;

/**
 * 티켓 데이터의 생명주기 관리와 소유권 전환을 담당하는 서비스 인터페이스
 * - 슬롯 확보, 발권 승인 및 환불에 따른 데이터 초기화 수행
 */
public interface TicketService {

    // 공연 좌석 정보와 동기화된 티켓 기초 데이터 일괄 생성
    void createTicketSlots(TicketCreateRequest request);

    // 좌석 식별자를 통한 티켓의 현재 판매 상태 및 상세 정보 확인
    TicketResponse findTicketByRoundSeat(Long roundSeatSq);

    // 다수의 좌석에 할당된 티켓 정보를 일괄적으로 추출
    List<TicketResponse> findTicketsByRoundSeats(List<Long> roundSeatSqs);

    // 사용자가 구매하여 보유 중인 전체 티켓 이력 조회
    List<TicketResponse> findMyTickets(Long userSq);

    // 특정 티켓에 대한 상세 정보 및 소유권 검증 조회
    TicketResponse findMyTicket(Long userSq, Long ticketSq);

    // 결제 완료 시점에 맞춘 티켓 소유권 할당 및 발권 확정
    void issueTickets(Long userSq, Payment payment, Long bookingSq, List<BookingSeatResponse> bookingSeats);

    // 환불 또는 거래 무효화 시 티켓 정보를 판매 전 상태로 복구
    void resetTickets(List<Long> roundSeatSqs);
}