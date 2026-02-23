package io.why503.paymentservice.domain.settlement.model.entity;

import io.why503.paymentservice.domain.settlement.model.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 공연 정산 정보와 정산 상태 관리를 담당하는 엔티티
 */
@Entity
@Table(name = "settlement")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sq;

    @Column(name = "company_sq", nullable = false)
    private Long companySq;

    @Column(name = "show_sq", nullable = false)
    private Long showSq;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "fee_amount", nullable = false)
    private Long feeAmount;

    @Column(name = "settlement_amount", nullable = false)
    private Long settlementAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_status", nullable = false, length = 20)
    private SettlementStatus settlementStatus;

    @Column(name = "settled_dt")
    private LocalDateTime settledDt;

    @CreatedDate
    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @LastModifiedDate
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    @Builder
    public Settlement(Long companySq, Long showSq, Long totalAmount, Long feeAmount, Long settlementAmount, SettlementStatus settlementStatus) {
        this.companySq = companySq;
        this.showSq = showSq;
        this.totalAmount = totalAmount;
        this.feeAmount = feeAmount;
        this.settlementAmount = settlementAmount;
        this.settlementStatus = settlementStatus;
    }

    // 정산 프로세스 완료 처리 및 완료 시점 기록
    public void completeSettlement() {
        this.settlementStatus = SettlementStatus.COMPLETED;
        this.settledDt = LocalDateTime.now();
    }
}