package io.why503.reservationservice.Domain.Concert.Model.Ett;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "`show`")
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_sq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_hall_sq", nullable = false)
    private ConcertHall concertHall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_sq", nullable = false)
    private Category category;

    @Column(name = "company_sq", nullable = false)
    private Long companySq; // 기업 서비스 ID 참조

    @Column(name = "show_name", nullable = false)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "open_dt", nullable = false)
    private LocalDateTime openDate;

    @Column(name = "show_time")
    private String runningTime;

    @Column(name = "viewing_age")
    private String viewingAge;

    @Column(name = "show_stat")
    private Integer status;

    @Column(name = "cast")
    private String cast;

    @Column(name = "director")
    private String director;
}