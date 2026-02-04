package io.why503.paymentservice.domain.point.model.entity;

import io.why503.paymentservice.domain.point.model.enums.PointStatus;
import io.why503.paymentservice.domain.point.util.PointExceptionFactory;
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
 * 포인트 충전 요청 정보와 진행 상태를 관리하는 엔티티
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
    private PointStatus status;

    @CreatedDate
    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @LastModifiedDate
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    @Builder
    public Point(Long userSq, String orderId, Long chargeAmount) {
        /*
         * 1. 필수 값 및 금액 유효성 검증
         * 2. 필드 초기화 및 초기 상태 설정
         */
        if (userSq == null || userSq <= 0) {
            throw PointExceptionFactory.pointBadRequest("회원 번호는 필수이며 양수여야 합니다.");
        }
        if (orderId == null || orderId.isBlank()) {
            throw PointExceptionFactory.pointBadRequest("주문 번호는 필수입니다.");
        }
        if (chargeAmount == null || chargeAmount <= 0) {
            throw PointExceptionFactory.pointBadRequest("충전 금액은 0원보다 커야 합니다.");
        }

        this.userSq = userSq;
        this.orderId = orderId;
        this.chargeAmount = chargeAmount;
        this.status = PointStatus.READY;
    }

    // 결제 성공 시 충전 상태를 완료로 변경
    public void complete() {
        if (this.status != PointStatus.READY) {
            throw PointExceptionFactory.pointConflict("대기 상태의 요청만 완료 처리할 수 있습니다. 현재: " + this.status);
        }
        this.status = PointStatus.DONE;
    }

    // 사용자 요청 또는 결제 실패 시 충전 취소 처리
    public void cancel() {
        if (this.status == PointStatus.DONE) {
            throw PointExceptionFactory.pointConflict("이미 완료된 충전은 취소할 수 없습니다.");
        }
        this.status = PointStatus.CANCELED;
    }
}