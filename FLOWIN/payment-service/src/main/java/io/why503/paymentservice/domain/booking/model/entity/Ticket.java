package io.why503.paymentservice.domain.booking.model.entity;

import io.why503.paymentservice.domain.booking.model.enums.DiscountPolicy;
import io.why503.paymentservice.domain.booking.model.enums.TicketStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 티켓 엔티티 (개별 상품 스냅샷)
 * - 예매(Booking)에 속한 개별 좌석 정보를 관리합니다.
 * - 공연 정보, 좌석 정보, 가격 정보는 예매 시점의 스냅샷으로 저장됩니다.
 */
@Entity
@Getter
@Table(name = "ticket")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sq")
    private Long sq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_sq", nullable = false)
    private Booking booking;

    @Column(name = "round_seat_sq", nullable = false)
    private Long roundSeatSq;

    @Column(name = "uuid", nullable = false, unique = true, length = 64)
    private String uuid;

    // [공연 스냅샷]
    @Column(name = "show_name", nullable = false, length = 100)
    private String showName;

    @Column(name = "hall_name", nullable = false, length = 100)
    private String hallName;

    @Column(name = "round_dt", nullable = false)
    private LocalDateTime roundDt;

    // [좌석 스냅샷]
    @Column(name = "seat_grade", nullable = false, length = 20)
    private String seatGrade;

    @Column(name = "seat_area", nullable = false, length = 20)
    private String seatArea;

    @Column(name = "seat_area_num", nullable = false)
    private Integer seatAreaNum;

    // [가격 스냅샷]
    @Column(name = "original_price", nullable = false)
    private Long originalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_policy", nullable = false, length = 50)
    private DiscountPolicy discountPolicy;

    @Column(name = "discount_amount", nullable = false)
    private Long discountAmount;

    @Column(name = "final_price", nullable = false)
    private Long finalPrice;

    // [상태]
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TicketStatus status;

    @Builder
    public Ticket(Booking booking, Long roundSeatSq, String showName, String hallName, LocalDateTime roundDt,
                  String seatGrade, String seatArea, Integer seatAreaNum,
                  Long originalPrice, DiscountPolicy discountPolicy, Long discountAmount) {

        // 1. 연관관계 및 필수 ID 검증
        if (booking == null) throw new IllegalArgumentException("예매 정보(booking)는 필수입니다.");
        if (roundSeatSq == null || roundSeatSq <= 0) throw new IllegalArgumentException("좌석 ID(roundSeatSq)는 필수입니다.");

        // 2. 공연 스냅샷 검증
        if (showName == null || showName.isBlank()) throw new IllegalArgumentException("공연명은 필수입니다.");
        if (hallName == null || hallName.isBlank()) throw new IllegalArgumentException("공연장명은 필수입니다.");
        if (roundDt == null) throw new IllegalArgumentException("공연 일시는 필수입니다.");

        // 3. 좌석 스냅샷 검증
        if (seatGrade == null || seatGrade.isBlank()) throw new IllegalArgumentException("좌석 등급은 필수입니다.");
        if (seatArea == null || seatArea.isBlank()) throw new IllegalArgumentException("좌석 구역은 필수입니다.");
        if (seatAreaNum == null || seatAreaNum <= 0) throw new IllegalArgumentException("좌석 번호는 양수여야 합니다.");

        // 4. 가격 검증 및 계산
        if (originalPrice == null || originalPrice < 0) throw new IllegalArgumentException("정가는 0원 이상이어야 합니다.");

        this.booking = booking;
        this.roundSeatSq = roundSeatSq;
        this.uuid = UUID.randomUUID().toString(); // UUID 자동 생성

        this.showName = showName;
        this.hallName = hallName;
        this.roundDt = roundDt;

        this.seatGrade = seatGrade;
        this.seatArea = seatArea;
        this.seatAreaNum = seatAreaNum;

        this.originalPrice = originalPrice;
        this.discountPolicy = (discountPolicy != null) ? discountPolicy : DiscountPolicy.NONE;
        this.discountAmount = (discountAmount != null) ? discountAmount : 0L;

        // 최종 가격 계산 (음수 방지)
        long calculatedPrice = this.originalPrice - this.discountAmount;
        if (calculatedPrice < 0) {
            throw new IllegalArgumentException("할인 금액이 정가보다 클 수 없습니다.");
        }
        this.finalPrice = calculatedPrice;

        this.status = TicketStatus.RESERVED; // 초기 상태는 선점
    }

    protected void setBooking(Booking booking) {
        this.booking = booking;
    }

    /**
     * 결제 완료 (예매 확정)
     */
    public void confirm() {
        if (this.status != TicketStatus.RESERVED) {
            throw new IllegalStateException("선점(RESERVED) 상태의 티켓만 결제 확정이 가능합니다. 현재: " + this.status);
        }
        this.status = TicketStatus.PAID;
    }

    /**
     * 티켓 취소
     */
    public void cancel() {
        if (this.status == TicketStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 티켓입니다.");
        }
        if (this.status == TicketStatus.USED) {
            throw new IllegalStateException("이미 사용된 티켓은 취소할 수 없습니다.");
        }
        this.status = TicketStatus.CANCELLED;
    }

    /**
     * 티켓 사용 (입장)
     */
    public void use() {
        if (this.status != TicketStatus.PAID) {
            throw new IllegalStateException("결제 완료(PAID) 된 티켓만 사용할 수 있습니다. 현재: " + this.status);
        }
        this.status = TicketStatus.USED;
    }
}