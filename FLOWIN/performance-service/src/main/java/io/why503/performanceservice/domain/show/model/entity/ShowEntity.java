/**
 * Show Entity
 * 공연 정보를 저장하는 JPA 엔티티
 * 사용 목적 :
 * - 공연 기본 정보 DB 저장
 * - 공연 등록 및 조회 시 기준 엔티티
 * 설계 특징 :
 * - Enum 값은 DB에 int 코드로 저장
 * - 비즈니스 계층에서는 Enum으로 변환하여 사용
 */
package io.why503.performanceservice.domain.show.model.entity;

import io.why503.performanceservice.domain.hall.model.entity.HallEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import io.why503.performanceservice.domain.show.model.enums.ShowCategory;
import io.why503.performanceservice.domain.show.model.enums.ShowStatus;

@Entity
@Table(name = "`show`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sq")
    private Long sq;               // 공연 시퀀스 (PK)

    @Column(name = "name", nullable = false, length = 100)
    private String name;           // 공연명

    @Column(name = "start_dt", nullable = false)
    private LocalDateTime startDt;   // 공연 시작일

    @Column(name = "end_dt", nullable = false)
    private LocalDateTime endDt;     // 공연 종료일

    @Column(name = "open_dt", nullable = false)
    private LocalDateTime openDt;      // 티켓 오픈 일시

    @Column(name = "running_time", nullable = false, length = 50)
    private String runningTime;           // 러닝타임

    @Column(name = "viewing_age", nullable = false, length = 20)
    private String viewingAge;         // 관람 등급

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ShowCategory category;              // 공연 카테고리 코드

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ShowStatus status;              // 공연 상태 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_sq", nullable = false)
    private HallEntity hall;        // 공연장 식별자 (FK)

    @Column(name = "company_sq", nullable = false)
    private Long companySq;             // 회사 식별자 (FK)

    @Builder
    public ShowEntity(
            String name,
            LocalDateTime startDt,
            LocalDateTime endDt,
            LocalDateTime openDt,
            String runningTime,
            String viewingAge,
            ShowCategory category, // int category -> ShowCategory category
            HallEntity hall,
            Long companySq) {
        this.name = name;
        this.startDt = startDt;
        this.endDt = endDt;
        this.openDt = openDt;
        this.runningTime = runningTime;
        this.viewingAge = viewingAge;
        this.category = category;
        this.status = ShowStatus.SCHEDULED; // 기본값 설정 (Enum 직접 할당)
        this.hall = hall;
        this.companySq = companySq;
    }

    // JPA가 알아서 변환해주고, Lombok @Getter가 값을 꺼내주므로
    // getCategoryEnum(), getShowStatus(), setCategory() 등은 이제 필요 없음

    public void changeStatus(ShowStatus newStatus) {
        this.status = newStatus;
    }
}
