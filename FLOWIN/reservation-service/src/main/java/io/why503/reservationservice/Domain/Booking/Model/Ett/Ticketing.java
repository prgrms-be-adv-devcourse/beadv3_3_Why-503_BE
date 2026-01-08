package io.why503.reservationservice.Domain.Booking.Model.Ett;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "ticketing")
public class Ticketing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticketing_sq")
    private Long ticketingSq;

    @Column(name = "user_sq")
    private Long userSq;

    @Column(name = "ticketing_dt")
    @Builder.Default
    private LocalDateTime ticketingDt = LocalDateTime.now();

    @Column(name = "ticketing_pay")
    private Integer ticketingPay; // Service의 .ticketingPay()와 매칭

    @Column(name = "ticketing_stat")
    private Integer ticketingStat; // 0: 대기, 1: 완료

    @OneToMany(mappedBy = "ticketing", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    // Ticketing 클래스 내부에 추가
    public void updateTotalPay(Integer finalPay) {
        this.ticketingPay = finalPay;
    }

    // 상태 변경 메서드
    public void changeStatus(Integer status) {
        this.ticketingStat = status;
    }
}