package io.why503.performanceservice.domain.round.model.entity;


import io.why503.performanceservice.domain.round.model.enums.RoundStatus;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Table(name = "rounds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoundEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_sq")
    private Long sq;       //회차시퀀스

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_sq", nullable = false)
    private ShowEntity show;    //공연시퀀스

    @Column(name = "round_dt",nullable = false)
    private LocalDateTime dateTime; //회차 일시

    @Column(name = "round_no",nullable = false)
    private Integer num; // 회차 번호

    @Column(name = "casting",nullable = false)
    private String cast; //해당 회차 출연진

    @Enumerated(EnumType.STRING) // DB에 AVAILABLE 등으로 저장
    @Column(name = "round_stat",nullable = false)
    private RoundStatus status;

    @Builder
    public RoundEntity(
            ShowEntity show,
            LocalDateTime dateTime,
            Integer num,
            String cast,
            RoundStatus status) {
        this.show = show;
        this.dateTime = dateTime;
        this.num = num;
        this.cast = cast;
        this.status = status;
    }

    //회차 상태 변경 메서드
    public void updateStat(RoundStatus newStatus) {
        this.status = newStatus;
    }

    // 회차 번호 변경 메서드 (재정렬 로직용)
    public void updateRoundNum(Integer newRoundNum) {
        this.num = newRoundNum;
    }
}