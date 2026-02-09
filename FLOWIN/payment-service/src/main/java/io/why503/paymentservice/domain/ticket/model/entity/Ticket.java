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
 * 공연 좌석별 티켓 발권 정보 및 소유권을 관리하는 엔티티
 * - 개별 좌석에 대한 판매 상태와 결제 금액 구성을 유지
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

    @Column(name = "round_seat_sq", nullable = false, unique = true)
    private Long roundSeatSq;

    @Column(name = "user_sq")
    private Long userSq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_sq")
    private Payment payment;

    @Column(name = "booking_sq")
    private Long bookingSq;

    @Column(name = "original_price")
    private Long originalPrice;

    @Column(name = "discount", length = 20)
    private String discount;

    @Column(name = "final_price")
    private Long finalPrice;

    @CreatedDate
    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @LastModifiedDate
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    @Builder
    public Ticket(Long roundSeatSq) {
        if (roundSeatSq == null) {
            throw new IllegalArgumentException("회차 좌석 ID(RoundSeatSq)는 필수입니다.");
        }
        this.roundSeatSq = roundSeatSq;
    }

    // 결제 정보를 기반으로 티켓 소유권 할당 및 가격 확정
    public void issue(Long userSq, Payment payment, Long bookingSq,
                      Long originalPrice, String discount, Long finalPrice) {
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

    // 환불 처리에 따른 티켓 정보 초기화 및 공석 전환
    public void clear() {
        this.userSq = null;
        this.payment = null;
        this.bookingSq = null;
        this.originalPrice = null;
        this.discount = null;
        this.finalPrice = null;
    }

    public boolean isSold() {
        return this.payment != null && this.userSq != null;
    }

    public boolean isAvailable() {
        return !isSold();
    }
}