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

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import io.why503.performanceservice.domain.show.model.enums.ShowCategory;
import io.why503.performanceservice.domain.show.model.enums.ShowStatus;

@Entity
@Table(name = "show")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_sq")
    private Long sq;               // 공연 시퀀스 (PK)

    @Column(name = "show_name", nullable = false, length = 100)
    private String name;           // 공연명

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;   // 공연 시작일

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;     // 공연 종료일

    @Column(name = "open_dt", nullable = false)
    private LocalDateTime openDate;      // 티켓 오픈 일시

    @Column(name = "show_time", nullable = false, length = 50)
    private String runningTime;           // 러닝타임

    @Column(name = "viewing_age", nullable = false, length = 20)
    private String viewingAge;         // 관람 등급

    // ===== Enum 값은 DB에 int 코드로 저장 =====

    @Column(name = "category", nullable = false)
    private int category;              // 공연 카테고리 코드

    @Column(name = "show_stat", nullable = false)
    private int status;              // 공연 상태 코드

    @Column(name = "concert_hall_sq", nullable = false)
    private Long concertHallSq;        // 공연장 식별자 (FK)

    @Column(name = "company_sq", nullable = false)
    private Long companySq;             // 회사 식별자 (FK)

    @Builder
    public ShowEntity(
            String name,
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime openDate,
            String runningTime,
            String viewingAge,
            int category,
            Long concertHallSq,
            Long companySq) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openDate = openDate;
        this.runningTime = runningTime;
        this.viewingAge = viewingAge;
        this.category = category;
        this.status = ShowStatus.SCHEDULED.getCode();
        this.concertHallSq = concertHallSq;
        this.companySq = companySq;
    }

// ===== Enum 변환 메서드 =====

    //카테고리 코드 → Enum 변환
    public ShowCategory getCategoryEnum() {
        return ShowCategory.fromCode(this.category);
    }

    //공연 상태 코드 → Enum 변환
    public ShowStatus getShowStatus() {
        return ShowStatus.fromCode(this.status);
    }

    //카테고리 Enum → 코드 값 저장
    public void setCategory(ShowCategory category) {
        this.category = category.getCode();
    }

    //공연 상태 Enum → 코드 값 저장
    public void setShowStatus(ShowStatus status) {
        this.status = status.getCode();
    }
}
