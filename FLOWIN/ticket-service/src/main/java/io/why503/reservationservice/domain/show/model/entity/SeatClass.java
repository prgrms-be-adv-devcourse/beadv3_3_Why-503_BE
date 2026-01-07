package io.why503.reservationservice.domain.show.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "seat_class")
public class SeatClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_class_sq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_sq")
    private Show show;

    @Column(name = "seat_class")
    private String name; // VIP, R, S

    @Column(name = "seat_price")
    private Integer price;
}