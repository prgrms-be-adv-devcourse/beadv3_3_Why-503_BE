package io.why503.reservationservice.domain.reservation.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticketing") // DB 테이블 이름 매핑
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticketing_sq")
    private Long id;

    @Column(name = "user_sq")
    private Long userId;

    @Column(name = "ticketing_date")
    private LocalDateTime reservedAt;

    @Column(name = "ticketing_stat")
    private String status; // PENDING, PAID
}