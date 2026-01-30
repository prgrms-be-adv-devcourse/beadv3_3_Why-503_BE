package io.why503.performanceservice.domain.showseat.model.entity;

import io.why503.performanceservice.domain.seat.model.entity.SeatEntity;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "show_seat",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_show_seat",
            columnNames = {"show_sq", "seat_sq"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowSeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_seat_sq")
    private Long sq;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    private ShowSeatGrade grade;

    @Column(name = "price", nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_sq", nullable = false)
    private SeatEntity seat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_sq", nullable = false)
    private ShowEntity show;

    /* 생성자 */
    public ShowSeatEntity(
            ShowSeatGrade grade,
            int price,
            ShowEntity show,
            SeatEntity seat
    ) {
        this.grade = grade;
        this.price = price;
        this.show = show;
        this.seat = seat;
    }

    /* 정책 변경용 메서드 */
    public void changeGrade(ShowSeatGrade grade) {
        this.grade = grade;
    }

    public void changePrice(int price) {
        this.price = price;
    }
}
