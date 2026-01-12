package io.why503.paymentservice.domain.ticketing.model.ett;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_sq")
    private Long ticketSq;

    @Column(name = "ticket_no", nullable = false, length = 50)
    private String ticketNo;

    @Column(name = "ticket_price", nullable = false)
    private Integer ticketPrice;

    // 0:유효, 1:사용완료, 2:취소
    @Column(name = "ticket_stat", nullable = false)
    private Integer ticketStat;

    @Column(name = "showing_seat_sq", nullable = false)
    private Long showingSeatSq; // [MSA] Concert Service ID 참조

    // [내부 관계] Ticketing 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketing_sq", nullable = false)
    private Ticketing ticketing;

    // --- [생성자 추가 로직] ---
    // Builder로 생성 시 UUID 자동 주입을 위해 커스텀 할 수도 있지만,
    // Service 단에서 UUID를 만들어서 넣는 것이 테스트하기 더 편합니다.
    // 여기서는 Setter 대신 연관관계 설정을 위한 메서드만 둡니다.

    public void setTicketing(Ticketing ticketing) {
        this.ticketing = ticketing;
    }

    // --- [비즈니스 로직] ---
    public void cancel() {
        this.ticketStat = 2;
    }

    public void use() {
        this.ticketStat = 1;
    }
}