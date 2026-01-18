package io.why503.performanceservice.domain.roundSeats.model.entity;


import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatStatus;
import io.why503.performanceservice.domain.roundSeats.model.dto.enumconverter.RoundSeatStatusConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name= "round_seat",
        //같은 회차에 같은 좌석 중복 등록 방지
        //테이블이 만들어질 때 기능을 넣는 것
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_round_seat_unique",
                    columnNames = {"round_sq", "show_seat_sq"}
            )}
    )
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoundSeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_seat_sq")
    private Long roundSeatSq;                                    //회차좌석시퀀스

    @Column(name = "round_seat_stat", nullable = false)
    @Convert(converter = RoundSeatStatusConverter.class)
    private RoundSeatStatus roundSeatStatus;                     //회차좌석 상태

    @Column(name = "round_seat_stat_time", nullable = false)
    private LocalDateTime roundSeatStatusTime;                   //상태변경일시

    // 변수명은 roundSq 유지, 타입은 RoundEntity로 변경하여 FK 연동
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_sq", nullable = false)
    private RoundEntity roundSq;                                        //회차시퀀스

    @Column(name = "show_seat_sq",nullable = false)
    private Long showSeatSq;                                     //공연좌석시퀀스


    //상태 변경 메서드
    public void updateStatus(RoundSeatStatus newStatus) {
        this.roundSeatStatus = newStatus;
        this.roundSeatStatusTime = LocalDateTime.now();
    }

}
