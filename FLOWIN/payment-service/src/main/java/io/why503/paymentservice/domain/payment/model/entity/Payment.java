package io.why503.paymentservice.domain.payment.model.entity;

import io.why503.paymentservice.domain.payment.model.enums.PaymentMethod;
import io.why503.paymentservice.domain.payment.model.enums.PaymentRefType;
import io.why503.paymentservice.domain.payment.model.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "payment")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sq")
    private Long sq;

    @Column(name = "user_sq", nullable = false)
    private Long userSq;

    // Booking 또는 Point 엔티티의 orderId와 매핑
    @Column(name = "order_id", nullable = false, unique = true, length = 64)
    private String orderId;

    // 결제 대상 구분 (BOOKING, POINT)
    @Enumerated(EnumType.STRING)
    @Column(name = "ref_type", nullable = false, length = 20)
    private PaymentRefType refType;

    // 결제 수단 (CARD, POINT, MIX)
    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 20)
    private PaymentMethod method;

    @Column(name = "pg_key", length = 200)
    private String pgKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    // [금액 상세]
    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "pg_amount", nullable = false)
    private Long pgAmount;

    @Column(name = "point_amount", nullable = false)
    private Long pointAmount;

    // [일시]
    @Column(name = "approved_dt")
    private LocalDateTime approvedDt;

    @Column(name = "cancelled_dt")
    private LocalDateTime cancelledDt;

    @CreatedDate
    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Builder
    public Payment(Long userSq, String orderId, PaymentRefType refType, PaymentMethod method,
                   Long totalAmount, Long pgAmount, Long pointAmount) {

        // 1. 필수 값 검증
        if (userSq == null || userSq <= 0) throw new IllegalArgumentException("userSq는 필수입니다.");
        if (orderId == null || orderId.isBlank()) throw new IllegalArgumentException("orderId는 필수입니다.");
        if (refType == null) throw new IllegalArgumentException("refType은 필수입니다.");
        if (method == null) throw new IllegalArgumentException("method는 필수입니다.");

        // 2. 금액 무결성 검증 (해피 패스 금지)
        long safePgAmount = (pgAmount == null) ? 0L : pgAmount;
        long safePointAmount = (pointAmount == null) ? 0L : pointAmount;

        if (totalAmount == null || totalAmount <= 0) {
            throw new IllegalArgumentException("총 결제 금액은 0원보다 커야 합니다.");
        }
        if (safePgAmount < 0 || safePointAmount < 0) {
            throw new IllegalArgumentException("결제 금액은 음수일 수 없습니다.");
        }
        if (safePgAmount + safePointAmount != totalAmount) {
            throw new IllegalArgumentException("PG 금액과 포인트 금액의 합이 총 금액과 일치하지 않습니다.");
        }

        this.userSq = userSq;
        this.orderId = orderId;
        this.refType = refType;
        this.method = method;
        this.status = PaymentStatus.READY; // 초기 상태
        this.totalAmount = totalAmount;
        this.pgAmount = safePgAmount;
        this.pointAmount = safePointAmount;
    }

    /**
     * 결제 승인 완료
     */
    public void approve(String pgKey) {
        if (this.status != PaymentStatus.READY) {
            throw new IllegalStateException("READY 상태에서만 승인 가능합니다.");
        }

        // CARD나 MIX 인데 pgKey가 없으면 안됨
        boolean isPgInvolved = this.method == PaymentMethod.CARD || this.method == PaymentMethod.MIX;
        if (isPgInvolved && (pgKey == null || pgKey.isBlank())) {
            throw new IllegalArgumentException("카드/복합 결제 시 pgKey는 필수입니다.");
        }

        this.pgKey = pgKey;
        this.status = PaymentStatus.DONE;
        this.approvedDt = LocalDateTime.now();
    }

    /**
     * 결제 취소
     */
    public void cancel() {
        if (this.status != PaymentStatus.DONE) {
            throw new IllegalStateException("완료(DONE)된 결제만 취소할 수 있습니다.");
        }
        this.status = PaymentStatus.CANCELED;
        this.cancelledDt = LocalDateTime.now();
    }
}