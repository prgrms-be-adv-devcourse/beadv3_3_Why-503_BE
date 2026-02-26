package io.why503.paymentservice.domain.payment.model.entity;

import io.why503.paymentservice.domain.payment.model.enums.PaymentMethod;
import io.why503.paymentservice.domain.payment.model.enums.PaymentRefType;
import io.why503.paymentservice.domain.payment.model.enums.PaymentStatus;
import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
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
 * 결제 거래 정보를 관리하는 엔티티
 * - 결제 대상 구분(예매/포인트) 및 PG 승인 정보와 금액 구성을 유지
 */
@Entity
@Getter
@Table(name = "payment")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sq;

    @Column(name = "user_sq", nullable = false)
    private Long userSq;

    @Enumerated(EnumType.STRING)
    @Column(name = "ref_type", nullable = false, length = 20)
    private PaymentRefType refType;

    @Column(name = "booking_sq")
    private Long bookingSq;

    @Column(name = "order_id", nullable = false, unique = true, length = 64)
    private String orderId;

    @Column(name = "payment_key", length = 200)
    private String paymentKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 20)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "pg_amount", nullable = false)
    private Long pgAmount;

    @Column(name = "point_amount", nullable = false)
    private Long pointAmount;

    @Column(name = "remain_pg_amount", nullable = false)
    private Long remainPgAmount;

    @Column(name = "remain_point_amount", nullable = false)
    private Long remainPointAmount;

    @Column(name = "approved_dt")
    private LocalDateTime approvedDt;

    @CreatedDate
    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @LastModifiedDate
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    @Builder
    public Payment(Long userSq, PaymentRefType refType, Long bookingSq, String orderId,
                   PaymentMethod method, Long totalAmount, Long pgAmount, Long pointAmount) {
        if (refType == PaymentRefType.BOOKING && bookingSq == null) {
            throw PaymentExceptionFactory.paymentBadRequest("예매 결제 시 예매 번호는 필수입니다.");
        }

        this.userSq = userSq;
        this.refType = refType;
        this.bookingSq = bookingSq;
        this.orderId = orderId;
        this.method = method;
        this.status = PaymentStatus.READY;
        this.totalAmount = totalAmount;
        this.pgAmount = pgAmount;
        this.pointAmount = pointAmount;
        this.remainPgAmount = pgAmount;
        this.remainPointAmount = pointAmount;
    }

    // 외부 PG사 승인 완료 정보를 기록하고 결제 상태를 확정
    public void complete(String paymentKey) {
        if (this.status != PaymentStatus.READY) {
            throw PaymentExceptionFactory.paymentConflict("준비 상태의 결제만 완료할 수 있습니다.");
        }
        this.paymentKey = paymentKey;
        this.status = PaymentStatus.DONE;
        this.approvedDt = LocalDateTime.now();
    }

    public void cancel(long refundPg, long refundPoint) {
        if (this.status == PaymentStatus.CANCELED) {
            throw PaymentExceptionFactory.paymentConflict("이미 전액 취소된 결제입니다.");
        }

        // [추가] 음수 입력 방지
        if (refundPg < 0 || refundPoint < 0) {
            throw PaymentExceptionFactory.paymentBadRequest("환불 금액은 0원 이상이어야 합니다.");
        }

        // 1. 잔액 차감
        this.remainPgAmount -= refundPg;
        this.remainPointAmount -= refundPoint;

        // (안전장치) 혹시 모를 음수 방지 (Service 계층에서 검증하겠지만 2중 방어)
        if (this.remainPgAmount < 0) this.remainPgAmount = 0L;
        if (this.remainPointAmount < 0) this.remainPointAmount = 0L;

        // 2. 상태 결정 logic
        if (this.remainPgAmount == 0 && this.remainPointAmount == 0) {
            this.status = PaymentStatus.CANCELED;
        } else {
            this.status = PaymentStatus.PARTIAL_CANCELED;
        }
    }

    public void cancel() {
        this.cancel(this.remainPgAmount, this.remainPointAmount);
    }
}