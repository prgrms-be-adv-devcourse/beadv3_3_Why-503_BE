package io.why503.reservationservice.domain.concerts.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    //좌석 배치도
    @Column(name = "concert_hall_width")
    private Integer width;

    @Column(name = "concert_hall_height")
    private Integer height;
}