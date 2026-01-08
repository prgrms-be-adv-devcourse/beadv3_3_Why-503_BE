package io.why503.reservationservice.Domain.Showing.Model.Ett;

import io.why503.reservationservice.Domain.Concert.Model.Ett.ShowSeat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "showing_seat")
public class ShowingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showing_seat_sq")
    private Long showingSeatSq;

    @Column(name = "showing_sq")
    private Long showingSq;

    @Column(name = "showing_seat_stat")
    private Integer showingSeatStat; // Service의 .getShowingSeatStat()과 매칭됨

    @Column(name = "showing_seat_stat_time")
    private LocalDateTime showingSeatStatTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_seat_sq")
    private ShowSeat showSeat; // ShowSeat 엔티티와 연결

    public void changeStatus(Integer status) {
        this.showingSeatStat = status;
        this.showingSeatStatTime = LocalDateTime.now();
    }

    public void hold() {
        this.changeStatus(1);
    }
}