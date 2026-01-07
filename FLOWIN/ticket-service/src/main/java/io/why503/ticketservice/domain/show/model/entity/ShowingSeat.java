package io.why503.reservationservice.domain.show.model.entity;

import io.why503.reservationservice.domain.concerts.model.entity.Seat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "showing_seat")
public class ShowingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showing_seat_sq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showing_sq")
    private Showing showing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_class_sq")
    private SeatClass seatClass;

    // 🔥 물리적 위치 연결 (필수)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_sq")
    private Seat seat;

    // 상태 관리 (AVAILABLE, HELD, SOLD) - Enum 사용 권장
    @Column(name = "showing_seat_stat")
    private String status;

    // 🔥 동시성 제어 (낙관적 락)
    @Version
    private Long version;

    // 비즈니스 로직: 좌석 점유
    public void hold() {
        this.status = "HELD";
    }
}