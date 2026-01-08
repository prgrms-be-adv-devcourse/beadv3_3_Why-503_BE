package io.why503.reservationservice.Domain.Concert.Model.Ett;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "concert_hall")
public class ConcertHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_hall_sq")
    private Long id;

    @Column(name = "concert_hall_name", nullable = false)
    private String name;

    @Column(name = "concert_hall_seat_scale")
    private Integer seatScale;

    @Column(name = "concert_hall_latitude")
    private BigDecimal latitude;

    @Column(name = "concert_hall_longitude")
    private BigDecimal longitude;
}