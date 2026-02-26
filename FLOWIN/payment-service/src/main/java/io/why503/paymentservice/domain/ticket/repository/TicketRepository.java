package io.why503.paymentservice.domain.ticket.repository;

import io.why503.paymentservice.domain.ticket.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 티켓 발권 정보 및 슬롯 데이터의 영속성 관리를 담당하는 레포지토리
 * - 좌석 식별자 기반 조회 및 사용자별 보유 티켓 추출 기능을 제공
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // 특정 좌석에 할당된 티켓 데이터 단건 조회
    Optional<Ticket> findByRoundSeatSq(Long roundSeatSq);

    // 예매 식별자를 공유하는 모든 티켓 묶음 추출
    List<Ticket> findAllByBookingSq(Long bookingSq);

    // 다건의 좌석 식별자에 대응하는 티켓 정보를 일괄 추출
    List<Ticket> findAllByRoundSeatSqIn(List<Long> roundSeatSqs);

    // 사용자가 구매한 티켓 이력을 최신순으로 정렬하여 조회
    List<Ticket> findAllByUserSqOrderByCreatedDtDesc(Long userSq);
}