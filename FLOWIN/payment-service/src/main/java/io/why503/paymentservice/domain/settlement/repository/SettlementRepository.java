package io.why503.paymentservice.domain.settlement.repository;

import io.why503.paymentservice.domain.settlement.model.entity.Settlement;
import io.why503.paymentservice.domain.settlement.model.enums.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 정산 데이터의 영속성 관리 및 조건별 조회 기능을 제공하는 저장소
 */
@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    // 기획사별 정산 이력을 최신순으로 제공
    List<Settlement> findByCompanySqOrderByCreatedDtDesc(Long companySq);

    // 공연별 정산 집계 내역 확인
    List<Settlement> findByShowSqOrderByCreatedDtDesc(Long showSq);

    // 특정 단계에 머물러 있는 정산 대상 추출
    List<Settlement> findBySettlementStatus(SettlementStatus settlementStatus);

    // 공연에 대한 정산 데이터 존재 여부 확인
    boolean existsByShowSq(Long showSq);
}