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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 결제 수단, 금액 정보 및 승인 상태를 관리하는 결제 엔티티
 */
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

    @Column(name = "order_id", nullable = false, unique = true, length = 64)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ref_type", nullable = false, length = 20)
    private PaymentRefType refType;

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

    @Column(name = "pg_key")
    private String pgKey;

    @CreatedDate
    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @Column(name = "approved_dt")
    private LocalDateTime approvedDt;

    @Column(name = "cancelled_dt")
    private LocalDateTime canceledDt;

    @Builder
    public Payment(Long userSq, String orderId, PaymentRefType refType, PaymentMethod method,
                   Long totalAmount, Long pgAmount, Long pointAmount) {
        /*
         * 1. 필수 데이터 존재 여부 검증
         * 2. 결제 수단별 금액 정합성 확인
         * 3. 초기 결제 대기 상태로 설정
         */
        if (userSq == null || userSq <= 0) {
            // [400] 필수값 오류
            throw PaymentExceptionFactory.paymentBadRequest("회원 번호는 필수입니다.");
        }
        if (orderId == null || orderId.isBlank()) {
            // [400] 필수값 오류
            throw PaymentExceptionFactory.paymentBadRequest("주문 번호는 필수입니다.");
        }
        if (refType == null) {
            // [400] 필수값 오류
            throw PaymentExceptionFactory.paymentBadRequest("결제 대상 구분은 필수입니다.");
        }
        if (method == null) {
            // [400] 필수값 오류
            throw PaymentExceptionFactory.paymentBadRequest("결제 수단은 필수입니다.");
        }
        if (totalAmount == null || totalAmount < 0) {
            // [400] 필수값/유효성 오류
            throw PaymentExceptionFactory.paymentBadRequest("총 금액은 필수입니다.");
        }

        long safePgAmount = (pgAmount != null) ? pgAmount : 0L;
        long safePointAmount = (pointAmount != null) ? pointAmount : 0L;

        if (safePgAmount < 0 || safePointAmount < 0) {
            throw PaymentExceptionFactory.paymentBadRequest("결제 금액은 음수일 수 없습니다.");
        }
        if (safePgAmount + safePointAmount != totalAmount) {
            throw PaymentExceptionFactory.paymentBadRequest("결제 금액 합계가 총 금액과 일치하지 않습니다.");
        }

        this.userSq = userSq;
        this.orderId = orderId;
        this.refType = refType;
        this.method = method;
        this.status = PaymentStatus.READY;
        this.totalAmount = totalAmount;
        this.pgAmount = safePgAmount;
        this.pointAmount = safePointAmount;
    }

    // 외부 결제 기관의 승인 키를 등록하고 상태를 완료로 변경
    public void approve(String pgKey) {
        if (this.status != PaymentStatus.READY) {
            throw PaymentExceptionFactory.paymentConflict("대기 상태에서만 승인 가능합니다.");
        }

        boolean isPgInvolved = this.method == PaymentMethod.CARD || this.method == PaymentMethod.MIX;
        if (isPgInvolved && (pgKey == null || pgKey.isBlank())) {
            throw PaymentExceptionFactory.paymentBadRequest("외부 결제 승인 키가 누락되었습니다.");
        }

        this.pgKey = pgKey;
        this.status = PaymentStatus.DONE;
        this.approvedDt = LocalDateTime.now();
    }

    // 결제 건을 취소 상태로 변경하고 취소 시각 기록
    public void cancel() {
        if (this.status == PaymentStatus.CANCELED) {
            throw PaymentExceptionFactory.paymentConflict("이미 취소된 결제입니다.");
        }
        this.status = PaymentStatus.CANCELED;
        this.canceledDt = LocalDateTime.now();
    }
}