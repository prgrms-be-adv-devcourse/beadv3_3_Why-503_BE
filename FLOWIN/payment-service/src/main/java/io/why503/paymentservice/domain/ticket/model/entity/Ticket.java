package io.why503.paymentservice.domain.ticket.model.entity;

import io.why503.paymentservice.domain.payment.model.entity.Payment;
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
 * 공연 회차 좌석(RoundSeat)과 1:1로 매핑되는 티켓 슬롯 엔티티
 * - 초기 생성 시점: RoundSeat 생성 시점에 같이 생성됨 (공석 상태)
 * - 결제 시점: 사용자 정보, 결제 정보, 예매 ID 등이 채워짐 (판매됨)
 */
@Entity
@Getter
@Table(name = "ticket", indexes = {
        @Index(name = "idx_ticket_user", columnList = "user_sq")
})
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sq")
    private Long sq;

    // 슬롯 기준 (1좌석 = 1티켓 강제, Unique Constraint)
    @Column(name = "round_seat_sq", nullable = false, unique = true)
    private Long roundSeatSq;

    // 구매자 정보 (NULL = 공석)
    @Column(name = "user_sq")
    private Long userSq;

    // 결제 정보 (NULL = 공석/미결제, 같은 서비스 내 객체 참조 유지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_sq")
    private Payment payment;

    // 예매 참조 (NULL = 미예매, 타 서비스(Reservation) ID 참조)
    @Column(name = "booking_sq")
    private Long bookingSq;

    // 가격 정보 (결제 시점에 확정, NULL 가능)
    @Column(name = "original_price")
    private Long originalPrice;

    @Column(name = "discount", length = 20)
    private String discount; // DiscountPolicy.name() 등 문자열 저장

    @Column(name = "final_price")
    private Long finalPrice;

    // 메타 정보
    @CreatedDate
    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @LastModifiedDate
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    // 초기 슬롯 생성용 빌더 (필수값인 roundSeatSq만 받음)
    @Builder
    public Ticket(Long roundSeatSq) {
        if (roundSeatSq == null) {
            throw new IllegalArgumentException("회차 좌석 ID(RoundSeatSq)는 필수입니다.");
        }
        this.roundSeatSq = roundSeatSq;
    }

    /**
     * 티켓 판매(발권) 처리
     * - 결제 성공 시 빈 슬롯에 사용자 및 결제 정보를 채워넣음
     */
    public void issue(Long userSq, Payment payment, Long bookingSq,
                      Long originalPrice, String discount, Long finalPrice) {
        // 이미 판매된 티켓인지 더블 체크 (동시성 제어는 DB Unique 제약이나 락으로 보완 필요)
        if (this.userSq != null || this.payment != null) {
            throw new IllegalStateException("이미 판매된 티켓 슬롯입니다. TicketSQ: " + this.sq);
        }

        this.userSq = userSq;
        this.payment = payment;
        this.bookingSq = bookingSq;
        this.originalPrice = originalPrice;
        this.discount = discount;
        this.finalPrice = finalPrice;
    }

    /**
     * 티켓 판매 취소 (환불)
     * - 데이터를 다시 NULL로 초기화하여 공석 상태로 만듦
     */
    public void clear() {
        this.userSq = null;
        this.payment = null;
        this.bookingSq = null;
        this.originalPrice = null;
        this.discount = null;
        this.finalPrice = null;
    }

    // --- 상태 확인용 편의 메서드 ---

    // 판매 완료 여부 (정보가 채워져 있으면 true)
    public boolean isSold() {
        return this.payment != null && this.userSq != null;
    }

    // 구매 가능 여부
    public boolean isAvailable() {
        return !isSold();
    }
}