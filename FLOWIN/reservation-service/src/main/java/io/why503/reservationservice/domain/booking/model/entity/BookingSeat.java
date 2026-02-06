package io.why503.reservationservice.domain.booking.model.entity;

import io.why503.reservationservice.domain.booking.util.BookingExceptionFactory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 예매(Booking)와 회차 좌석(RoundSeat)을 연결하는 중간 엔티티
 * - 역할: 단순히 "어떤 예매에 어떤 좌석이 묶여있는지"만 기록
 * - 상태 관리: 없음 (RoundSeat 테이블에서 직접 관리됨)
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

    @Builder
    public BookingSeat(Long roundSeatSq) {
        if (roundSeatSq == null) {
            throw BookingExceptionFactory.bookingBadRequest("회차 좌석 ID는 필수입니다.");
        }
        this.roundSeatSq = roundSeatSq;
    }

    // 연관관계 편의 메서드용 (Booking에서 호출)
    public void setBooking(Booking booking) {
        // 무결성 보호: 한 번 설정된 Booking은 변경 불가하거나, null 체크 등 필요 시 추가
        if (this.booking != null) {
            throw BookingExceptionFactory.bookingConflict("이미 예매 정보가 설정된 좌석입니다.");
        }
        this.booking = booking;
    }
}