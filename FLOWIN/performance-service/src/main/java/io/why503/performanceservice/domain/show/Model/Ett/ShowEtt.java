package io.why503.performanceservice.domain.show.Model.Ett;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import io.why503.performanceservice.domain.show.Model.Enum.ShowCategory;
import io.why503.performanceservice.domain.show.Model.Enum.ShowStatus;

@Entity
@Table(name = "`show`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ShowEtt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_sq")
    private Long showSq;

    @Column(name = "show_name", nullable = false, length = 100)
    private String showName;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "open_dt", nullable = false)
    private LocalDateTime openDt;

    @Column(name = "show_time", nullable = false, length = 50)
    private String showTime;

    @Column(name = "viewing_age", nullable = false, length = 20)
    private String viewingAge;

    // ===== DB에는 int =====
    @Column(name = "category", nullable = false)
    private int category;

    @Column(name = "show_stat", nullable = false)
    private int showStat;

    @Column(name = "concert_hall_sq", nullable = false)
    private Long concertHallSq;

    @Column(name = "company_sq", nullable = false)
    private Long companySq;

    // ===== Enum 변환 메서드 =====

    public ShowCategory getCategoryEnum() {
        return ShowCategory.fromCode(this.category);
    }

    public ShowStatus getShowStatus() {
        return ShowStatus.fromCode(this.showStat);
    }

    public void setCategory(ShowCategory category) {
        this.category = category.getCode();
    }

    public void setShowStatus(ShowStatus status) {
        this.showStat = status.getCode();
    }
}
