package io.why503.ticketservice.domain.concerts.model.entity;

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
    @JoinColumn(name = "concert_hall_sq")
    private ConcertHall concertHall;

    @Column(name = "seat_no", nullable = false)
    private String seatNo; // "A-1"

    // 배치도 상의 좌표
    @Column(name = "seat_x")
    private Integer x;

    @Column(name = "seat_y")
    private Integer y;
}