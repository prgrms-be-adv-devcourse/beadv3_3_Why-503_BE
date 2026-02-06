package io.why503.paymentservice.domain.ticket.service;

import io.why503.paymentservice.domain.payment.model.entity.Payment;
import io.why503.paymentservice.domain.ticket.model.dto.request.TicketCreateRequest;
import io.why503.paymentservice.domain.ticket.model.dto.response.TicketResponse;

import java.util.List;

/**
 * 티켓 슬롯의 생성(공석 확보) 및 조회, 상태 관리를 담당하는 서비스 인터페이스
 * - 구현체(Impl)에서 비즈니스 로직을 수행함
 */
public interface TicketService {

    /**
     * 공연 회차 좌석 생성 시점에 맞춰 티켓 슬롯을 일괄 생성 (초기 공석 상태)
     * @param request 회차 ID와 생성할 좌석 ID 목록
     */
    void createTicketSlots(TicketCreateRequest request);

    /**
     * 특정 좌석 ID(RoundSeatSq)에 해당하는 티켓 슬롯 조회
     * @param roundSeatSq 회차 좌석 ID
     * @return 티켓 상세 정보
     */
    TicketResponse findTicketByRoundSeat(Long roundSeatSq);

    /**
     * 여러 좌석 ID에 해당하는 티켓 슬롯 목록 일괄 조회
     * - 예매/결제 시 다수의 좌석을 처리할 때 사용
     * @param roundSeatSqs 조회할 좌석 ID 목록
     * @return 티켓 상세 정보 목록
     */
    List<TicketResponse> findTicketsByRoundSeats(List<Long> roundSeatSqs);

    /**
     * 사용자의 티켓 목록 전체 조회
     * - 마이페이지 등에서 사용
     * @param userSq 사용자 식별자
     * @return 사용자가 보유한 티켓 목록
     */
    List<TicketResponse> findMyTickets(Long userSq);

    /**
     * 사용자의 특정 티켓 상세 조회
     * - 본인 소유 확인 필수
     * @param userSq 사용자 식별자
     * @param ticketSq 티켓 식별자
     * @return 티켓 상세 정보
     */
    TicketResponse findMyTicket(Long userSq, Long ticketSq);

    /**
     * [Internal] 결제 완료 후 티켓 발권 처리
     */
    void issueTickets(Long userSq, Payment payment, Long bookingSq, List<Long> roundSeatSqs);

    /**
     * [Internal] 결제 취소/환불 시 티켓 상태 초기화
     */
    void resetTickets(List<Long> roundSeatSqs);
}