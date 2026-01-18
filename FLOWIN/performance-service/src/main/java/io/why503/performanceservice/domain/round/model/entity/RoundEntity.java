package io.why503.performanceservice.domain.round.model.entity;


import io.why503.performanceservice.domain.round.model.dto.RoundStatus;
import io.why503.performanceservice.domain.round.model.dto.enumconverter.RoundStatusConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "rounds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoundEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_sq")
    private Long roundSq;       //회차시퀀스

    //    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "show_sq", nullable = false)
    @Column(name = "show_sq")
    private Long showSq;    //공연시퀀스

    @Column(name = "round_dt",nullable = false)
    private LocalDateTime roundDt; //회차 일시

    @Column(name = "round_no",nullable = false)
    private Integer roundNum; // 회차 번호

    @Column(name = "casting",nullable = false)
    private String roundCast; //해당 회차 출연진

    @Column(name = "round_stat",nullable = false)
    @Convert(converter = RoundStatusConverter.class)
    private RoundStatus roundStatus; //회차 상태 enum으로 관리 0:예매 가능, 1:예매 종료, 2: 회차취소

    //회차 상태 변경 메서드
    public void updateStat(RoundStatus newStatus) {
        this.roundStatus = newStatus;
    }
}