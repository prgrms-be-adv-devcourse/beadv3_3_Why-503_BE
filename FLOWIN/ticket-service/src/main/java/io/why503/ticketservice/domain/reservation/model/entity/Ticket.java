package io.why503.reservationservice.domain.reservation.model.entity;

import io.why503.reservationservice.domain.show.model.entity.ShowingSeat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_sq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketing_sq")
    private Reservation reservation;

    // 어떤 재고를 샀는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showing_seat_sq")
    private ShowingSeat showingSeat;

    @Column(name = "ticket_no")
    private String ticketNo;

    @Column(name = "ticket_seat_price")
    private Integer price;

    @Column(name = "ticket_stat")
    private String status;
}