package io.why503.reservationservice.Domain.Concert.Model.Ett;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seat")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_sq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_hall_sq", nullable = false)
    private ConcertHall concertHall;

    @Column(name = "seat_no")
    private Integer seatNo;

    @Column(name = "seat_area")
    private String seatArea;

    @Column(name = "area_seat_no")
    private Integer areaSeatNo;
}