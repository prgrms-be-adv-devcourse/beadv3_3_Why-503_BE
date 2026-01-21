package io.why503.paymentservice.domain.booking.model.entity;

import io.why503.paymentservice.domain.booking.model.vo.TicketStatus;
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

    // --- 1. 식별자 및 외부 참조 ---

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_sq")
    private Long ticketSq;

    @Column(name = "round_seat_sq", nullable = false)
    private Long roundSeatSq;

    @Column(name = "ticket_uuid", nullable = false)
    @Builder.Default
    private String ticketUuid = UUID.randomUUID().toString();

    // --- 2. 좌석 정보 스냅샷 ---

    @Column(name = "show_name", nullable = false)
    private String showName;

    @Column(name = "concert_hall_name", nullable = false)
    private String concertHallName;

    @Column(name = "round_date", nullable = false)
    private LocalDateTime roundDate;

    @Column(name = "grade", nullable = false)
    private String grade;

    @Column(name = "seat_area", nullable = false)
    private String seatArea;

    @Column(name = "area_seat_no", nullable = false)
    private Integer areaSeatNumber;

    // --- 3. 가격 정보 스냅샷 ---

    @Column(name = "original_price", nullable = false)
    private Integer originalPrice;

    @Column(name = "discount_amount", nullable = false)
    @Builder.Default
    private Integer discountAmount = 0;

    @Column(name = "final_price", nullable = false)
    private Integer finalPrice;

    // --- 4. 상태 정보 ---

    @Column(name = "ticket_status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private TicketStatus ticketStatus = TicketStatus.AVAILABLE;

    // --- 5. 연관 관계 ---

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_sq", nullable = false)
    private Booking booking;

    // --- 6. 비즈니스 로직 ---

    /**
     * 결제 완료 처리
     */
    public void paid() {
        this.ticketStatus = TicketStatus.PAID;
    }

    /**
     * 취소 처리
     */
    public void cancel() {
        this.ticketStatus = TicketStatus.CANCELLED;
    }

    /**
     * 입장 처리 (QR 사용)
     */
    public void use() {
        if (this.ticketStatus != TicketStatus.PAID) {
            throw new IllegalStateException("결제 완료된 티켓만 사용할 수 있습니다.");
        }
        this.ticketStatus = TicketStatus.USED;
    }

//    /**
//     * 좌석 번호 포맷팅 (예: A-15)
//     */
//    public String getFormattedSeatNo() {
//        return this.seatArea + "-" + this.areaSeatNumber;
//    }

    // --- 7. 내부 로직 ---

    @PrePersist
    public void prePersist() {
        // 필수 필드 기본값 방어 로직
        if (this.originalPrice == null) this.originalPrice = 0;
        if (this.discountAmount == null) this.discountAmount = 0;
        if (this.finalPrice == null) this.finalPrice = 0;
        if (this.ticketStatus == null) this.ticketStatus = TicketStatus.AVAILABLE;
        if (this.ticketUuid == null) this.ticketUuid = UUID.randomUUID().toString();

        if (this.grade == null) this.grade = "S";
        if (this.seatArea == null) this.seatArea = "Unknown";
        if (this.areaSeatNumber == null) this.areaSeatNumber = 0;

        if (this.showName == null) this.showName = "Unknown Show";
        if (this.concertHallName == null) this.concertHallName = "Unknown Hall";
        if (this.roundDate == null) this.roundDate = LocalDateTime.now();
    }
}