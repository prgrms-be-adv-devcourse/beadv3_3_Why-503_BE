package io.why503.paymentservice.domain.booking.model.entity;

import io.why503.paymentservice.domain.booking.model.enums.DiscountPolicy;
import io.why503.paymentservice.domain.booking.model.enums.TicketStatus;
import io.why503.paymentservice.domain.booking.util.BookingExceptionFactory;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 예매에 속한 개별 좌석의 정보와 결제 시점의 가격 스냅샷을 관리하는 엔티티
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

    @Column(name = "show_name", nullable = false, length = 100)
    private String showName;

    @Column(name = "hall_name", nullable = false, length = 100)
    private String hallName;

    @Column(name = "round_dt", nullable = false)
    private LocalDateTime roundDt;

    @Column(name = "seat_grade", nullable = false, length = 20)
    private String seatGrade;

    @Column(name = "seat_area", nullable = false, length = 20)
    private String seatArea;

    @Column(name = "seat_area_num", nullable = false)
    private Integer seatAreaNum;

    @Column(name = "original_price", nullable = false)
    private Long originalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_policy", nullable = false, length = 50)
    private DiscountPolicy discountPolicy;

    @Column(name = "discount_amount", nullable = false)
    private Long discountAmount;

    @Column(name = "final_price", nullable = false)
    private Long finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private TicketStatus status;

    @Builder
    public Ticket(Booking booking, Long roundSeatSq, String showName, String hallName, LocalDateTime roundDt,
                  String seatGrade, String seatArea, Integer seatAreaNum,
                  Long originalPrice, DiscountPolicy discountPolicy, Long discountAmount) {

        /*
         * 1. 필수 연관관계 및 공연/좌석 정보 검증
         * 2. 가격 유효성 검사 및 최종 금액 계산
         * 3. 티켓 상태 초기화
         */
        if (booking == null) throw BookingExceptionFactory.bookingBadRequest("예매 정보는 필수입니다.");
        if (roundSeatSq == null || roundSeatSq <= 0) throw BookingExceptionFactory.bookingBadRequest("좌석 ID는 필수입니다.");
        if (showName == null || showName.isBlank()) throw BookingExceptionFactory.bookingBadRequest("공연명은 필수입니다.");
        if (hallName == null || hallName.isBlank()) throw BookingExceptionFactory.bookingBadRequest("공연장명은 필수입니다.");
        if (roundDt == null) throw BookingExceptionFactory.bookingBadRequest("공연 일시는 필수입니다.");
        if (seatGrade == null || seatGrade.isBlank()) throw BookingExceptionFactory.bookingBadRequest("좌석 등급은 필수입니다.");
        if (seatArea == null || seatArea.isBlank()) throw BookingExceptionFactory.bookingBadRequest("좌석 구역은 필수입니다.");
        if (seatAreaNum == null || seatAreaNum <= 0) throw BookingExceptionFactory.bookingBadRequest("좌석 번호는 양수여야 합니다.");
        if (originalPrice == null || originalPrice < 0) throw BookingExceptionFactory.bookingBadRequest("정가는 0원 이상이어야 합니다.");

        this.booking = booking;
        this.roundSeatSq = roundSeatSq;
        this.uuid = UUID.randomUUID().toString();

        this.showName = showName;
        this.hallName = hallName;
        this.roundDt = roundDt;

        this.seatGrade = seatGrade;
        this.seatArea = seatArea;
        this.seatAreaNum = seatAreaNum;

        this.originalPrice = originalPrice;
        this.discountPolicy = (discountPolicy != null) ? discountPolicy : DiscountPolicy.NONE;
        this.discountAmount = (discountAmount != null) ? discountAmount : 0L;

        long calculatedPrice = this.originalPrice - this.discountAmount;
        if (calculatedPrice < 0) {
            throw BookingExceptionFactory.bookingBadRequest("할인 금액이 정가보다 클 수 없습니다.");
        }
        this.finalPrice = calculatedPrice;
        this.status = TicketStatus.RESERVED;
    }

    protected void setBooking(Booking booking) {
        this.booking = booking;
    }

    // 티켓 상태를 결제 완료로 변경
    public void confirm() {
        if (this.status != TicketStatus.RESERVED) {
            throw BookingExceptionFactory.bookingConflict("선점 상태의 티켓만 결제 확정이 가능합니다. 현재: " + this.status);
        }
        this.status = TicketStatus.PAID;
    }

    // 티켓 취소 처리
    public void cancel() {
        if (this.status == TicketStatus.CANCELLED) {
            throw BookingExceptionFactory.bookingConflict("이미 취소된 티켓입니다.");
        }
        if (this.status == TicketStatus.USED) {
            throw BookingExceptionFactory.bookingConflict("이미 사용된 티켓은 취소할 수 없습니다.");
        }
        this.status = TicketStatus.CANCELLED;
    }

    // 티켓 사용 완료 처리
    public void use() {
        if (this.status != TicketStatus.PAID) {
            throw BookingExceptionFactory.bookingConflict("결제 완료 된 티켓만 사용할 수 있습니다. 현재: " + this.status);
        }
        this.status = TicketStatus.USED;
    }
}