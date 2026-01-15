package io.why503.performanceservice.domain.showing.model.ett;


import io.why503.performanceservice.domain.showing.model.dto.ShowingStat;
import io.why503.performanceservice.domain.showing.model.dto.enumconverter.ShowingStatConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "showing")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShowingEtt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "showing_sq")
    private Long sq;       //회차시퀀스

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "show_sq", nullable = false)
    @Column(name = "show_sq")
    private Long show;    //공연시퀀스

    @Column(name = "showing_dt",nullable = false)
    private LocalDateTime dt; //회차 일시

    @Column(name = "showing_no",nullable = false)
    private Integer no; // 회차 번호

    @Column(name = "`cast`",nullable = false)
    private String cast; //해당 회차 출연진

    @Column(name = "showing_stat",nullable = false)
    @Convert(converter = ShowingStatConverter.class)
    private ShowingStat stat; //회차 상태 enum으로 관리 0:예매 가능, 1:예매 종료, 2: 회차취소

    //회차 상태 변경 메서드
    public void updateStat(ShowingStat newStat) {
        this.stat = newStat;
    }
}
