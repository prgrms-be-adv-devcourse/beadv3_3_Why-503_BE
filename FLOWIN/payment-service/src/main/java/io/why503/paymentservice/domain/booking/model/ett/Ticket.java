package io.why503.paymentservice.domain.booking.model.ett;

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
    @Builder.Default
    private Integer ticketStatus = 0; // 0:발권, 1:사용, 2:취소

    @Column(name = "ticket_uuid", nullable = false)
    @Builder.Default
    private String ticketUuid = UUID.randomUUID().toString(); // QR용 코드 자동 생성

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_sq", nullable = false)
    private Booking booking;
}
