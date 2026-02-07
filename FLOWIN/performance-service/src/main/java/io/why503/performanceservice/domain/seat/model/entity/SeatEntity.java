package io.why503.performanceservice.domain.seat.model.entity;

import io.why503.performanceservice.domain.hall.model.entity.HallEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "seat",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_seat_area_num",
            columnNames = { "hall_sq", "area", "area_num" }
        )
    },
    indexes = {
        @Index(name = "idx_seat_hall", columnList = "hall_sq")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sq")
    private Long sq;

    @Column(name = "num")
    private Integer num;

    @Column(name = "area", nullable = false, length = 20)
    private String area;

    @Column(name = "area_num", nullable = false)
    private Integer numInArea;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hall_sq", nullable = false)
    private HallEntity hall;

    @Builder
    public SeatEntity(
            Integer num,
            String area,
            Integer numInArea,
            HallEntity hall
    ) {
        this.num = num;
        this.area = area;
        this.numInArea = numInArea;
        this.hall = hall;
    }
}
