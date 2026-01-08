package io.why503.reservationservice.Domain.Booking.Model.Ett;

import io.why503.reservationservice.Domain.Showing.Model.Ett.ShowingSeat;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_sq")
    private Long ticketSq;

    @Column(name = "ticket_no")
    private String ticketNo;

    @Column(name = "ticket_real_price")
    private Integer ticketRealPrice; // 정가

    @Column(name = "ticket_dis")
    private Integer ticketDis;       // 할인금액 (없으면 0)

    @Column(name = "ticket_price")
    private Integer ticketPrice;     // 최종금액

    @Column(name = "ticket_stat")
    private Integer ticketStat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketing_sq")
    private Ticketing ticketing;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showing_seat_sq")
    private ShowingSeat showingSeat;

    // Ticket 클래스 내부에 추가
    public void applyDiscount(Integer discountAmount, Integer finalPrice) {
        this.ticketDis = discountAmount;
        this.ticketPrice = finalPrice;
    }

    // 상태 변경 메서드
    public void changeStatus(Integer status) {
        this.ticketStat = status;
    }
}