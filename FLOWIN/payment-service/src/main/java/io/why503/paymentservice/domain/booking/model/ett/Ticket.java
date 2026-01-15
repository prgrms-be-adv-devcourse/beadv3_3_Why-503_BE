package io.why503.paymentservice.domain.booking.model.ett;

import io.why503.paymentservice.domain.booking.model.vo.TicketStat;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_sq")
    private Long ticketSq;

    @Column(name = "showing_seat_sq", nullable = false)
    private Long showingSeatSq;

    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;

    @Column(name = "discount_amount", nullable = false)
    @Builder.Default
    private Integer discountAmount = 0;

    @Column(name = "final_price", nullable = false)
    private Integer finalPrice;

    @Column(name = "ticket_status", nullable = false)
    @Enumerated(EnumType.ORDINAL) // 0, 1, 2... 숫자로 저장
    @Builder.Default
    private TicketStat ticketStat = TicketStat.AVAILABLE; // 기본값 0

    @Column(name = "ticket_uuid", nullable = false)
    @Builder.Default
    private String ticketUuid = UUID.randomUUID().toString(); // QR용 코드 자동 생성

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_sq", nullable = false)
    private Booking booking;

    // 비즈니스 로직

    // 결제 완료 처리 (Booking에서 호출)
    public void paid() {
        this.ticketStat = TicketStat.PAID;
    }
    // 티켓 취소 비즈니스 로직
    public void cancel() {
        this.ticketStat = TicketStat.CANCELLED; // 4번 상태로 변경
    }

    // 저장 전 null 방어 로직
    @PrePersist
    public void prePersist() {
        if (this.originalPrice == null) this.originalPrice = 0;
        if (this.discountAmount == null) this.discountAmount = 0;
        if (this.finalPrice == null) this.finalPrice = 0;
        if (this.ticketStat == null) this.ticketStat = TicketStat.AVAILABLE;
        if (this.ticketUuid == null) this.ticketUuid = java.util.UUID.randomUUID().toString();
    }
}
