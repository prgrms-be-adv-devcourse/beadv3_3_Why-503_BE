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
 * 결제 트랜잭션 엔티티
 * - [SQL 동기화 완료] ref_type, booking_sq, pg_amount 필드 반영
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
    private PaymentRefType refType; // BOOKING, POINT

    @Column(name = "booking_sq")
    private Long bookingSq; // 예매 결제 시 사용

    @Column(name = "order_id", nullable = false, unique = true, length = 64)
    private String orderId;

    @Column(name = "payment_key", length = 200)
    private String paymentKey; // PG사 응답 키

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 20)
    private PaymentMethod method; // CARD, EASY_PAY...

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status; // READY, DONE, CANCELED

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "pg_amount", nullable = false)
    private Long pgAmount; // 실제 PG 결제액

    @Column(name = "point_amount", nullable = false)
    private Long pointAmount; // 사용된 포인트

    @Column(name = "approved_dt")
    private LocalDateTime approvedDt; // 결제 승인 일시

    @CreatedDate
    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @LastModifiedDate
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    @Builder
    public Payment(Long userSq, PaymentRefType refType, Long bookingSq, String orderId,
                   PaymentMethod method, Long totalAmount, Long pgAmount, Long pointAmount) {
        // [해피 패스 금지] 예매 결제인데 bookingSq가 없으면 예외
        if (refType == PaymentRefType.BOOKING && bookingSq == null) {
            throw PaymentExceptionFactory.paymentBadRequest("예매 결제 시 예매 번호는 필수입니다.");
        }

        this.userSq = userSq;
        this.refType = refType;
        this.bookingSq = bookingSq;
        this.orderId = orderId;
        this.method = method;
        this.status = PaymentStatus.READY; // 초기 상태는 READY
        this.totalAmount = totalAmount;
        this.pgAmount = pgAmount;
        this.pointAmount = pointAmount;
    }

    /**
     * 결제 승인 완료 처리
     * @param paymentKey PG사 식별 키
     */
    public void complete(String paymentKey) {
        if (this.status != PaymentStatus.READY) {
            throw PaymentExceptionFactory.paymentConflict("준비 상태의 결제만 완료할 수 있습니다.");
        }
        this.paymentKey = paymentKey;
        this.status = PaymentStatus.DONE;
        this.approvedDt = LocalDateTime.now();
    }

    /**
     * 결제 취소 처리
     */
    public void cancel() {
        if (this.status == PaymentStatus.CANCELED) {
            throw PaymentExceptionFactory.paymentConflict("이미 취소된 결제입니다.");
        }
        this.status = PaymentStatus.CANCELED;
    }
}