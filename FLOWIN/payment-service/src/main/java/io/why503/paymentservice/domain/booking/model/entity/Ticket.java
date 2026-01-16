package io.why503.paymentservice.domain.booking.model.entity;

import io.why503.paymentservice.domain.booking.model.vo.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticket")
public class Ticket {

    // =================================================================
    //  1. 식별자 및 외부 참조
    // =================================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_sq")
    private Long ticketSq;

    @Column(name = "showing_seat_sq", nullable = false)
    private Long showingSeatSq;

    @Column(name = "ticket_uuid", nullable = false)
    @Builder.Default
    private String ticketUuid = UUID.randomUUID().toString();

    // =================================================================
    //  2. 가격 정보
    // =================================================================
    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;

    @Column(name = "discount_amount", nullable = false)
    @Builder.Default
    private Integer discountAmount = 0;

    @Column(name = "final_price", nullable = false)
    private Integer finalPrice;

    // =================================================================
    //  3. 상태 정보
    // =================================================================
    @Column(name = "ticket_status", nullable = false)
    @Enumerated(EnumType.ORDINAL) // 숫자 저장 (0, 1, 2...)
    @Builder.Default
    private TicketStatus ticketStatus = TicketStatus.AVAILABLE;

    // =================================================================
    //  4. 연관 관계
    // =================================================================
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_sq", nullable = false)
    private Booking booking;

    // =================================================================
    //  5. 비즈니스 로직
    // =================================================================

    // 결제 완료 처리
    public void paid() {
        this.ticketStatus = TicketStatus.PAID;
    }

    // 취소 처리
    public void cancel() {
        this.ticketStatus = TicketStatus.CANCELLED;
    }

    // =================================================================
    //  6. 내부 로직 (Null 방어)
    // =================================================================
    @PrePersist
    public void prePersist() {
        if (this.originalPrice == null) this.originalPrice = 0;
        if (this.discountAmount == null) this.discountAmount = 0;
        if (this.finalPrice == null) this.finalPrice = 0;
        if (this.ticketStatus == null) this.ticketStatus = TicketStatus.AVAILABLE;
        if (this.ticketUuid == null) this.ticketUuid = UUID.randomUUID().toString();
    }
}