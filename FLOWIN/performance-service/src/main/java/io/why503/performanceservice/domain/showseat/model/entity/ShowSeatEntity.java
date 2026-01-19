package io.why503.performanceservice.domain.showseat.model.entity;

import io.why503.performanceservice.domain.seat.model.entity.SeatEtt;
import io.why503.performanceservice.domain.show.model.entity.ShowEtt;
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
    private Long showSeatSq;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade", nullable = false)
    private ShowSeatGrade grade;

    @Column(name = "price", nullable = false)
    private int price;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_sq", nullable = false)
    private SeatEtt seat;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "show_sq", nullable = false)
    private ShowEtt show;

    /* 생성자 */
    public ShowSeatEntity(
            ShowEtt show,
            SeatEtt seat,
            ShowSeatGrade grade,
            int price
    ) {
        this.show = show;
        this.seat = seat;
        this.grade = grade;
        this.price = price;
    }

    /* 정책 변경용 메서드 */
    public void changeGrade(ShowSeatGrade grade) {
        this.grade = grade;
    }

    public void changePrice(int price) {
        this.price = price;
    }
}
