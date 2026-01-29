package io.why503.performanceservice.domain.roundSeats.model.entity;


import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.roundSeats.model.enums.RoundSeatStatus;
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
    private Long sq;                                    //회차좌석시퀀스

    @Column(name = "round_seat_stat", nullable = false)
    @Convert(converter = RoundSeatStatusConverter.class)
    private RoundSeatStatus status;                     //회차좌석 상태

    @Column(name = "round_seat_stat_time", nullable = false)
    private LocalDateTime statusTime;                   //상태변경일시

    // 변수명은 roundSq 유지, 타입은 RoundEntity로 변경하여 FK 연동
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_sq", nullable = false)
    private RoundEntity round;                                        //회차시퀀스

    @Column(name = "show_seat_sq",nullable = false)
    private Long showSeatSq;                                     //공연좌석시퀀스

    @Version
    private Long version;                                        //낙관적 락을 위한 버전 관리

    //상태 변경 메서드
    public void updateStatus(RoundSeatStatus newStatus) {
        this.status = newStatus;
        this.statusTime = LocalDateTime.now();
    }

    //좌석 선점
    public void reserve() {
        if (this.status != RoundSeatStatus.AVAILABLE) {
            throw new IllegalStateException("이미 판매되었거나 선점된 좌석입니다.");
        }
        this.status = RoundSeatStatus.RESERVED;
        this.statusTime = LocalDateTime.now();
    }

    //선점 해제 (취소/실패 시)
    public void release() {
        if (this.status == RoundSeatStatus.SOLD) {
            throw new IllegalStateException("이미 결제 완료된 좌석 입니다.");
        }
        this.status = RoundSeatStatus.AVAILABLE;
        this.statusTime = LocalDateTime.now();
    }

    // 판매 확정
    public void confirm() {
        if (this.status != RoundSeatStatus.RESERVED) {
            throw new IllegalStateException("선점된 좌석만 판매 확정할 수 있습니다.");
        }
        this.status = RoundSeatStatus.SOLD; // 상태를 판매완료로 변경
        this.statusTime = LocalDateTime.now();
    }
}
