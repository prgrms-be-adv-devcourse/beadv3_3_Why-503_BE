package io.why503.paymentservice.domain.settlement.repository;

import io.why503.paymentservice.domain.settlement.model.entity.Settlement;
import io.why503.paymentservice.domain.settlement.model.enums.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    // 1. 특정 기획사의 모든 정산 내역 조회 (기획사 마이페이지/정산조회용)
    List<Settlement> findByCompanySqOrderByCreatedDtDesc(Long companySq);

    // 2. 특정 공연에 대한 정산 내역 조회 (공연별 정산 확인용)
    List<Settlement> findByShowSqOrderByCreatedDtDesc(Long showSq);

    // 3. 특정 상태의 정산 내역 조회 (배치 스케줄러가 'READY' 상태인 건을 찾아서 처리할 때 사용)
    List<Settlement> findBySettlementStatus(SettlementStatus settlementStatus);

    boolean existsByShowSq(Long showSq);
}