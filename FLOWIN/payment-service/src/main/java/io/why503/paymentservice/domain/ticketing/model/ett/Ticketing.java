package io.why503.paymentservice.domain.ticketing.model.ett;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticketing")
@EntityListeners(AuditingEntityListener.class)
public class Ticketing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticketing_sq")
    private Long ticketingSq;

    @CreatedDate
    @Column(name = "ticketing_dt", nullable = false, updatable = false)
    private LocalDateTime ticketingDt;

    @Column(name = "ticketing_pay", nullable = false)
    private Integer ticketingPay;

    // 0:대기, 1:완료, 2:취소, 3:부분취소
    @Column(name = "ticketing_stat", nullable = false)
    private Integer ticketingStat;

    @Column(name = "user_sq", nullable = false)
    private Long userSq;

    @Builder.Default
    @OneToMany(mappedBy = "ticketing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    // --- [연관관계 편의 메서드] ---
    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.setTicketing(this);
    }

    // --- [비즈니스 로직] ---

    // 1. 결제 완료 (대기 -> 완료)
    public void confirm() {
        this.ticketingStat = 1;
    }

    // 2. 전체 취소 (완료 -> 취소)
    public void cancel() {
        this.ticketingStat = 2;
        // 하위 티켓들도 모두 취소 상태로 변경
        for (Ticket ticket : tickets) {
            ticket.cancel();
        }
    }

    // 3. 부분 취소 (특정 티켓만 취소) - ★ 추가된 핵심 로직
    public void cancelSpecificTicket(Long ticketSq) {
        // 1) 해당 티켓을 찾아서 취소 처리
        this.tickets.stream()
                .filter(t -> t.getTicketSq().equals(ticketSq))
                .findFirst()
                .ifPresent(Ticket::cancel);

        // 2) 남아있는 유효한 티켓이 있는지 확인
        boolean hasValidTicket = this.tickets.stream()
                .anyMatch(t -> t.getTicketStat() == 0); // 0:유효

        // 3-A) 유효한 게 하나라도 있으면 -> 부분 취소 상태(3)
        if (hasValidTicket) {
            this.ticketingStat = 3;
            // 필요하다면 여기서 ticketingPay(총액)를 재계산하거나, 환불 금액 로직 추가
        }
        // 3-B) 유효한 게 하나도 없으면 -> 전체 취소 상태(2)
        else {
            this.ticketingStat = 2;
        }
    }
}