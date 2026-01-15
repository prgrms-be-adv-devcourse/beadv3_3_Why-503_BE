/**
 * SeatEtt (seat)
 *
 * 목적:
 * - 공연장(concert_hall)에 귀속되는 "고정 좌석 자원"을 표현하는 엔티티
 * - show와 직접 연결하지 않으며, show_seat에서 좌석-회차별 상태/가격을 관리한다.
 *
 * 주요 컬럼:
 * - seat_sq (PK)
 * - concert_hall_sq (FK, 공연장 소속)
 * - seat_area (구역, ex: A/B/VIP)
 * - area_seat_no (구역 내 좌석 번호, ex: 1..N)
 * - seat_no (전체 좌석 순번, optional)
 *
 * 무결성:
 * - (concert_hall_sq, seat_area, area_seat_no) UNIQUE
 *   -> 같은 공연장 내 동일 구역/좌석번호 중복 생성 방지
 *
 * 범위:
 * - seat는 생성/조회까지만 담당
 * - 가격/등급/판매상태/점유상태는 show_seat에서 담당 (본 이슈 제외)
 */


package io.why503.performanceservice.domain.seat.Model.Ett;

import io.why503.performanceservice.domain.concerthall.Model.Ett.ConcertHallEtt;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "seat",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_seat_concert_area_no",
            columnNames = { "concert_hall_sq", "seat_area", "area_seat_no" }
        )
    },
    indexes = {
        @Index(name = "idx_seat_concert_hall", columnList = "concert_hall_sq")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SeatEtt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_sq")
    private Long seatSq;

    @Column(name = "seat_no")
    private Integer seatNo;

    @Column(name = "seat_area", nullable = false, length = 20)
    private String seatArea;

    @Column(name = "area_seat_no", nullable = false)
    private Integer areaSeatNo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concert_hall_sq", nullable = false)
    private ConcertHallEtt concertHall;

    /* =======================
       생성자 (비즈니스용)
       ======================= */

    public SeatEtt(
            ConcertHallEtt concertHall,
            String seatArea,
            Integer areaSeatNo,
            Integer seatNo
    ) {
        this.concertHall = concertHall;
        this.seatArea = seatArea;
        this.areaSeatNo = areaSeatNo;
        this.seatNo = seatNo;
    }
}
