package io.why503.performanceservice.domain.seat.model.Ett;

import io.why503.performanceservice.domain.concert_hall.model.Ett.ConcertHallEtt;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor
@Table(name = "seat")
public class SeatEtt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_sq")
    private Long sq;

    @Column(name = "seat_no", nullable = false)
    private int seatNo;

    @Column(name = "seat_area", length = 10, nullable = false)
    private String seatArea;

    @Column(name = "area_seat_no", nullable = false)
    private int areaSeatNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_hall_sq", nullable = false)
    private ConcertHallEtt concertHallEtt;


    @Builder
    public SeatEtt(
            int seatNo,
            String seatArea,
            int areaSeatNo
    ) {
        this.seatNo = seatNo;
        this.seatArea = seatArea;
        this.areaSeatNo = areaSeatNo;
    }

}