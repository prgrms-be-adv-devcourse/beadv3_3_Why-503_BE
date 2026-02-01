package io.why503.paymentservice.domain.booking.model.entity;

import io.why503.paymentservice.domain.booking.model.enums.BookingStatus;
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
 * 예매의 생명주기와 전체 결제 금액 정보를 관리하는 엔티티
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

    @Column(name = "original_amount", nullable = false)
    private Long originalAmount = 0L;

    @Column(name = "final_amount", nullable = false)
    private Long finalAmount = 0L;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_dt", nullable = false, updatable = false)
    private LocalDateTime createdDt;

    @LastModifiedDate
    @Column(name = "updated_dt")
    private LocalDateTime updatedDt;

    @Builder
    public Booking(Long userSq, String orderId) {
        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("회원 번호는 필수이며 0보다 커야 합니다.");
        }
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("주문 번호는 필수입니다.");
        }

        this.userSq = userSq;
        this.orderId = orderId;
    }

    // 예매에 티켓을 추가하고 합계 금액 갱신
    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.setBooking(this);

        this.originalAmount += ticket.getOriginalPrice();
        this.finalAmount += ticket.getFinalPrice();
    }

    // 결제 완료에 따른 예매 및 하위 티켓 확정 처리
    public void confirm() {
        /*
         * 1. 예매 상태 검증
         * 2. 예매 및 모든 소속 티켓 상태 확정
         */
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("예매 대기 상태에서만 확정이 가능합니다.");
        }
        this.status = BookingStatus.CONFIRMED;

        for (Ticket ticket : this.tickets) {
            ticket.confirm();
        }
    }

    // 예매 전체 취소 및 하위 티켓 일괄 취소
    public void cancel(String reason) {
        /*
         * 1. 취소 가능 여부 확인
         * 2. 예매 상태 변경 및 사유 기록
         * 3. 유효한 모든 하위 티켓 취소 처리
         */
        if (this.status == BookingStatus.CANCELLED) throw new IllegalStateException("이미 취소된 예매입니다.");

        this.status = BookingStatus.CANCELLED;
        this.cancelReason = reason;

        for (Ticket ticket : this.tickets) {
            if (ticket.getStatus() != io.why503.paymentservice.domain.booking.model.enums.TicketStatus.CANCELLED) {
                ticket.cancel();
            }
        }
    }

    // 환불 금액에 따른 부분 취소 또는 전체 취소 처리
    public void partialCancel(long canceledAmount) {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("이미 전체 취소된 예매입니다.");
        }

        long newFinalAmount = this.finalAmount - canceledAmount;
        if (newFinalAmount < 0) {
            throw new IllegalStateException("취소 금액이 남은 결제 금액보다 큽니다.");
        }

        this.finalAmount = newFinalAmount;

        if (this.finalAmount == 0) {
            this.status = BookingStatus.CANCELLED;
            this.cancelReason = "모든 티켓 취소로 인한 자동 전체 취소";
        } else {
            this.status = BookingStatus.PARTIAL_CANCEL;
        }
    }

    // 예매 확정 전 금액 정보 변경
    public void changeAmounts(long originalAmount, long finalAmount) {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("금액 변경은 예매 대기 상태에서만 가능합니다.");
        }
        if (originalAmount < 0 || finalAmount < 0) {
            throw new IllegalArgumentException("금액은 0원 이상이어야 합니다.");
        }

        this.originalAmount = originalAmount;
        this.finalAmount = finalAmount;
    }
}