package io.why503.reservationservice.domain.booking.model.entity;

import io.why503.reservationservice.domain.booking.model.enums.BookingStatus;
import io.why503.reservationservice.domain.booking.util.BookingExceptionFactory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 예매 및 좌석 선점 정보를 관리하는 엔티티
 * - 결제 상태에 따른 예매 생명 주기를 관리
 */
@Entity
@Getter
@Table(name = "booking")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sq")
    private Long sq;

    @Column(name = "user_sq", nullable = false)
    private Long userSq;

    @Column(name = "order_id", nullable = false, unique = true, length = 64)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> bookingSeats = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @LastModifiedDate
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    @Builder
    public Booking(Long userSq, String orderId) {
        if (userSq == null || userSq <= 0) {
            throw BookingExceptionFactory.bookingBadRequest("회원 번호는 필수이며 0보다 커야 합니다.");
        }
        if (orderId == null || orderId.isBlank()) {
            throw BookingExceptionFactory.bookingBadRequest("주문 번호(OrderId)는 필수입니다.");
        }

        this.userSq = userSq;
        this.orderId = orderId;
    }

    // 예매와 선점 좌석 간의 객체 연관관계 설정
    public void addBookingSeat(BookingSeat bookingSeat) {
        if (bookingSeat == null) {
            throw BookingExceptionFactory.bookingBadRequest("추가할 좌석 정보가 없습니다.");
        }
        this.bookingSeats.add(bookingSeat);
        bookingSeat.setBooking(this);
    }

    // 결제 승인 결과 반영 및 예매 확정
    public void paid() {
        if (this.status != BookingStatus.PENDING) {
            throw BookingExceptionFactory.bookingConflict("결제 완료 처리는 대기(PENDING) 상태에서만 가능합니다.");
        }
        this.status = BookingStatus.PAID;
    }

    // 사용자 요청 또는 결제 실패에 따른 예매 무효화
    public void cancel() {
        if (this.status == BookingStatus.CANCELLED) {
            throw BookingExceptionFactory.bookingConflict("이미 취소된 예매입니다.");
        }
        this.status = BookingStatus.CANCELLED;
    }
}