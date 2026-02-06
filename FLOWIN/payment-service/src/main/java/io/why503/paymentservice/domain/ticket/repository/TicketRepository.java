package io.why503.paymentservice.domain.ticket.repository;

import io.why503.paymentservice.domain.ticket.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 티켓(슬롯) 엔티티에 대한 데이터 액세스를 담당하는 레포지토리
 * - PaymentService 내에서 티켓의 생성, 조회, 상태 변경을 위해 사용됨
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // 회차 좌석 ID(Unique)로 티켓 슬롯 단건 조회
    Optional<Ticket> findByRoundSeatSq(Long roundSeatSq);

    // 예매 ID로 예매에 속한 티켓 목록 조회 (Booking 엔티티 삭제 -> ID 참조 변경)
    List<Ticket> findAllByBookingSq(Long bookingSq);

    // 여러 좌석 ID에 해당하는 티켓 목록 일괄 조회 (배치 처리 및 검증용)
    List<Ticket> findAllByRoundSeatSqIn(List<Long> roundSeatSqs);

    // 특정 사용자가 구매한 티켓 목록 조회 (인덱스 활용)
    List<Ticket> findAllByUserSqOrderByCreatedDtDesc(Long userSq);
}