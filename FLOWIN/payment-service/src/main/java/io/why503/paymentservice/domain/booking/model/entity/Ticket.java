package io.why503.paymentservice.domain.booking.model.entity;

import io.why503.paymentservice.domain.booking.model.vo.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticket")
public class Ticket {

    // =================================================================
    //  1. 식별자 및 외부 참조
    // =================================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_sq")
    private Long ticketSq;

    // [수정 1] 용어 변경: showingSeatSq -> roundSeatSq
    @Column(name = "round_seat_sq", nullable = false)
    private Long roundSeatSq;

    @Column(name = "ticket_uuid", nullable = false)
    @Builder.Default
    private String ticketUuid = UUID.randomUUID().toString();

    // =================================================================
    //  2. 좌석 정보 스냅샷
    // =================================================================

    @Column(name = "show_name", nullable = false)
    private String showName;

    @Column(name = "concert_hall_name", nullable = false)
    private String concertHallName;

    @Column(name = "round_date", nullable = false)
    private LocalDateTime roundDate;

    @Column(name = "grade", nullable = false)
    private String grade;

    @Column(name = "seat_area", nullable = false)
    private String seatArea;     // 구역 (A)

    @Column(name = "area_seat_no", nullable = false)
    private Integer areaSeatNumber; // 번호 (1)

    // =================================================================
    //  3. 가격 정보 스냅샷
    // =================================================================
    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;

    @Column(name = "discount_amount", nullable = false)
    @Builder.Default
    private Integer discountAmount = 0;

    @Column(name = "final_price", nullable = false)
    private Integer finalPrice;

    // =================================================================
    //  4. 상태 정보
    // =================================================================
    @Column(name = "ticket_status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private TicketStatus ticketStatus = TicketStatus.AVAILABLE;

    // =================================================================
    //  5. 연관 관계
    // =================================================================
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_sq", nullable = false)
    private Booking booking;

    // =================================================================
    //  6. 비즈니스 로직
    // =================================================================

    // 결제 완료 처리
    public void paid() {
        this.ticketStatus = TicketStatus.PAID;
    }

    // 취소 처리
    public void cancel() {
        this.ticketStatus = TicketStatus.CANCELLED;
    }

    // 입장 처리 (QR 사용) - [추가 제안]
    public void use() {
        if (this.ticketStatus != TicketStatus.PAID) {
            throw new IllegalStateException("결제 완료된 티켓만 사용할 수 있습니다.");
        }
        this.ticketStatus = TicketStatus.USED;
    }

    // =================================================================
    //  7. 내부 로직 (Null 방어)
    // =================================================================
    @PrePersist
    public void prePersist() {
        // 기존 기본값 설정
        if (this.originalPrice == null) this.originalPrice = 0;
        if (this.discountAmount == null) this.discountAmount = 0;
        if (this.finalPrice == null) this.finalPrice = 0;
        if (this.ticketStatus == null) this.ticketStatus = TicketStatus.AVAILABLE;
        if (this.ticketUuid == null) this.ticketUuid = UUID.randomUUID().toString();

        // [수정] 변경된 스냅샷 필드명 반영 (null 방지)
        if (this.grade == null) this.grade = "S"; // SQL default가 'S'였음
        if (this.seatArea == null) this.seatArea = "Unknown";
        if (this.areaSeatNumber == null) this.areaSeatNumber = 0;

        // [NEW] 새로 추가된 공연 정보 스냅샷 (null 방지)
        if (this.showName == null) this.showName = "Unknown Show";
        if (this.concertHallName == null) this.concertHallName = "Unknown Hall";
        if (this.roundDate == null) this.roundDate = LocalDateTime.now(); // 임시 시간
    }

    // [편의 메서드] "A-15" 형태로 꺼내고 싶을 때 사용
    public String getFormattedSeatNo() {
        return this.seatArea + "-" + this.areaSeatNumber;
    }
}