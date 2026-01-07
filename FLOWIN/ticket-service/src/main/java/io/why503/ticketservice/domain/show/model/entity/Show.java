package io.why503.ticketservice.domain.show.model.entity;

import io.why503.ticketservice.domain.concerts.model.entity.ConcertHall;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "`show`") // MySQL 예약어 회피
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_sq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_hall_sq")
    private ConcertHall concertHall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_sq")
    private Category category;

    @Column(name = "show_name", nullable = false)
    private String title;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}