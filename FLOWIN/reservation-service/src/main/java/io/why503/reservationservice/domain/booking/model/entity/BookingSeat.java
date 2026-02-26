package io.why503.reservationservice.domain.booking.model.entity;

import io.why503.reservationservice.domain.booking.model.enums.DiscountPolicy;
import io.why503.reservationservice.domain.booking.util.BookingExceptionFactory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 예매 정보와 선택된 좌석 간의 연관 관계를 관리하는 엔티티
 * - 특정 예매에 귀속된 좌석 식별 정보를 유지
 */
@Entity
@Getter
@Table(name = "booking_seat", uniqueConstraints = {
        @UniqueConstraint(name = "uk_booking_seat", columnNames = {"booking_sq", "round_seat_sq"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sq")
    private Long sq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_sq", nullable = false)
    private Booking booking;

    @Column(name = "round_seat_sq", nullable = false)
    private Long roundSeatSq;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_policy", length = 20)
    private DiscountPolicy discountPolicy = DiscountPolicy.NONE;

    @Builder
    public BookingSeat(Long roundSeatSq, DiscountPolicy discountPolicy) {
        if (roundSeatSq == null) {
            throw BookingExceptionFactory.bookingBadRequest("회차 좌석 ID는 필수입니다.");
        }
        this.roundSeatSq = roundSeatSq;
        this.discountPolicy = (discountPolicy != null) ? discountPolicy : DiscountPolicy.NONE;
    }

    // 예매 엔티티와의 양방향 연관 관계 설정 및 데이터 무결성 검증
    public void setBooking(Booking booking) {
        if (this.booking != null) {
            throw BookingExceptionFactory.bookingConflict("이미 예매 정보가 설정된 좌석입니다.");
        }
        this.booking = booking;
    }

    public void changeDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = (discountPolicy != null) ? discountPolicy : DiscountPolicy.NONE;
    }
}