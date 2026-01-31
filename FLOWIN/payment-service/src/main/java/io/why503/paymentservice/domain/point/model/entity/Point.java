package io.why503.paymentservice.domain.point.model.entity;

import io.why503.paymentservice.domain.point.model.enums.PointStatus;
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
 * 포인트 충전 요청 엔티티
 * - 포인트 충전을 위한 결제 요청 정보를 관리합니다.
 * - 실제 포인트 잔액 증감은 별도의 Account(Point) 서비스로 전파됩니다.
 */
@Entity
@Getter
@Table(name = "point")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sq")
    private Long sq;

    @Column(name = "user_sq", nullable = false)
    private Long userSq;

    @Column(name = "order_id", nullable = false, unique = true, length = 64)
    private String orderId;

    @Column(name = "charge_amount", nullable = false)
    private Long chargeAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PointStatus status = PointStatus.READY;

    @CreatedDate
    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @LastModifiedDate
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    @Builder
    public Point(Long userSq, String orderId, Long chargeAmount) {
        // 해피 패스 금지: 필수 값 및 유효성 검증
        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("회원 번호(userSq)는 필수이며 양수여야 합니다.");
        }
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("주문 번호(orderId)는 필수입니다.");
        }
        if (chargeAmount == null || chargeAmount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0원보다 커야 합니다.");
        }

        this.userSq = userSq;
        this.orderId = orderId;
        this.chargeAmount = chargeAmount;
        this.status = PointStatus.READY; // 초기 상태
    }

    /**
     * 충전 완료 (결제 성공 시)
     * - READY 상태에서만 완료 가능
     */
    public void complete() {
        if (this.status != PointStatus.READY) {
            throw new IllegalStateException("대기(READY) 상태의 요청만 완료 처리할 수 있습니다. 현재: " + this.status);
        }
        this.status = PointStatus.DONE;
    }

    /**
     * 충전 취소 (결제 실패 또는 취소 시)
     * - READY 상태에서만 취소 가능 (이미 완료된 건은 별도 환불 로직 필요)
     */
    public void cancel() {
        if (this.status != PointStatus.READY) {
            throw new IllegalStateException("대기(READY) 상태의 요청만 취소 처리할 수 있습니다. 현재: " + this.status);
        }
        this.status = PointStatus.CANCELED;
    }
}