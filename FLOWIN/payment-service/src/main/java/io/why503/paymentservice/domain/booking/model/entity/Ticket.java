package io.why503.paymentservice.domain.booking.model.entity;

import io.why503.paymentservice.domain.booking.model.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 티켓 엔티티
 * - 개별 좌석에 대한 예매 상세 정보를 담고 있습니다.
 * - 공연 정보(이름, 날짜, 가격)의 스냅샷을 저장하여 원본 데이터 변경에 영향을 받지 않습니다.
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticket")
public class Ticket {

    // --- 식별자 및 외부 참조 ---

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_sq")
    private Long sq;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_sq", nullable = false)
    private Booking booking;

    @Column(name = "round_seat_sq", nullable = false)
    private Long roundSeatSq;

    @Column(name = "uuid", nullable = false)
    @Builder.Default
    private String uuid = UUID.randomUUID().toString();

    // --- 좌석 정보 스냅샷 ---

    @Column(name = "show_name", nullable = false)
    private String showName;

    @Column(name = "concert_hall_name", nullable = false)
    private String concertHallName;

    @Column(name = "round_date_time", nullable = false)
    private LocalDateTime roundDateTime;

    @Column(name = "grade", nullable = false)
    @Builder.Default
    private String grade = "S";

    @Column(name = "seat_area", nullable = false)
    @Builder.Default
    private String seatArea = "Unknown";

    @Column(name = "area_seat_num", nullable = false)
    @Builder.Default
    private Integer areaSeatNum = 0; // Number -> Num 규칙 적용

    // --- 가격 정보 스냅샷 ---

    @Column(name = "original_price", nullable = false)
    @Builder.Default
    private Integer originalPrice = 0;

    @Column(name = "discount_amount", nullable = false)
    @Builder.Default
    private Integer discountAmount = 0;

    @Column(name = "final_price", nullable = false)
    @Builder.Default
    private Integer finalPrice = 0;

    // --- 상태 정보 ---

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private TicketStatus status = TicketStatus.AVAILABLE;

    // --- 비즈니스 로직 ---

    public void paid() {
        this.status = TicketStatus.PAID;
    }

    public void cancel() {
        this.status = TicketStatus.CANCELLED;
    }

    public void use() {
        if (this.status != TicketStatus.PAID) {
            throw new IllegalStateException("결제 완료된 티켓만 사용할 수 있습니다.");
        }
        this.status = TicketStatus.USED;
    }

    // --- 내부 로직 ---

    @PrePersist
    public void prePersist() {
        // 필수 필드 기본값 방어 로직
        if (this.originalPrice == null) this.originalPrice = 0;
        if (this.discountAmount == null) this.discountAmount = 0;
        if (this.finalPrice == null) this.finalPrice = 0;
        if (this.status == null) this.status = TicketStatus.AVAILABLE;
        if (this.uuid == null) this.uuid = UUID.randomUUID().toString();

        if (this.grade == null) this.grade = "S";
        if (this.seatArea == null) this.seatArea = "Unknown";
        if (this.areaSeatNum == null) this.areaSeatNum = 0;

        if (this.showName == null) this.showName = "Unknown Show";
        if (this.concertHallName == null) this.concertHallName = "Unknown Hall";
        if (this.roundDateTime == null) this.roundDateTime = LocalDateTime.now();
    }
}