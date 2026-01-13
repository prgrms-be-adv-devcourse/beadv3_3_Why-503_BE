package io.why503.performanceservice.domain.concert_hall.model.Ett;

import io.why503.performanceservice.domain.concert_hall.model.dto.ConcertHallUpdateReq;
import io.why503.performanceservice.domain.seat.model.Ett.SeatEtt;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Entity
@Getter
@NoArgsConstructor
@Table(name = "concert_hall")
public class ConcertHallEtt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "concert_hall_sq")
    private Long sq;
    @Column(name = "concert_hall_name")
    private String name;
    @Column(name = "concert_hall_post")
    private String post;
    @Column(name = "concert_hall_basic_addr")
    private String basicAddr;
    @Column(name = "concert_hall_detail_addr")
    private String detailAddr;
    @Column(name = "concert_hall_stat", length = 1)
    private String stat;
    @Setter
    @Column(name = "concert_hall_seat_scale")
    private int seatScale;
    @Column(name = "concert_hall_structure")
    private String structure;
    @Column(name = "concert_hall_latitude", precision = 10, scale = 8)
    private BigDecimal latitude;
    @Column(name = "concert_hall_longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    @OneToMany(mappedBy = "concertHall", cascade = CascadeType.ALL)
    private List<SeatEtt> seats = new ArrayList<>();

    @Builder
    public ConcertHallEtt(
         String name,
         String post,
         String basicAddr,
         String detailAddr,
         String stat,
         int seatScale,
         String structure,
         BigDecimal latitude,
         BigDecimal longitude
    ) {
        this.name = name;
        this.post = post;
        this.basicAddr = basicAddr;
        this.detailAddr = detailAddr;
        this.stat = stat;
        this.seatScale = seatScale;
        this.structure = structure;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void update(
            ConcertHallUpdateReq concertHallRegisterReq
    ) {
        this.name = concertHallRegisterReq.name();
        this.post = concertHallRegisterReq.post();
        this.basicAddr = concertHallRegisterReq.basicAddr();
        this.detailAddr = concertHallRegisterReq.detailAddr();
        this.stat = concertHallRegisterReq.stat();
        this.seatScale = concertHallRegisterReq.seatScale();
        this.structure = concertHallRegisterReq.structure();
        this.latitude = concertHallRegisterReq.latitude();
        this.longitude = concertHallRegisterReq.longitude();
        
    }

}
