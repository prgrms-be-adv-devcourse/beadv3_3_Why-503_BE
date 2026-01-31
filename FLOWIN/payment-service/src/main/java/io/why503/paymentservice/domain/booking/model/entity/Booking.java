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
 * 예매 엔티티
 * - 예매의 생명주기(생성, 확정, 취소)를 관리합니다.
 * - 금액 정보는 Ticket들의 합계이며, 결제 전(PENDING)에만 변경 가능합니다.
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

    // [상태 및 금액]
    // 디폴트 값은 필드에서 관리
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "original_amount", nullable = false)
    private Long originalAmount = 0L;

    @Column(name = "final_amount", nullable = false)
    private Long finalAmount = 0L;

    // [메타 정보]
    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    // [추가됨] Ticket과의 1:N 관계 매핑
    // CascadeType.ALL: Booking 저장/삭제 시 Ticket도 같이 처리
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
        // 해피 패스 금지: 필수 값 검증
        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("회원 번호(userSq)는 필수이며 0보다 커야 합니다.");
        }
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("주문 번호(orderId)는 필수입니다.");
        }

        this.userSq = userSq;
        this.orderId = orderId;
        // 상태와 금액은 필드 초기값(PENDING, 0L)을 따름
    }

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.setBooking(this); // Ticket 엔티티에 setBooking 메서드 필요 (혹은 생성자 주입)

        // 티켓 추가 시 금액 자동 합산
        this.originalAmount += ticket.getOriginalPrice();
        this.finalAmount += ticket.getFinalPrice();
    }

    /**
     * 예매 확정
     * - 결제가 완료되어 예매를 확정합니다.
     * - PENDING 상태에서만 가능합니다.
     */
    public void confirm() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("예매 대기 상태에서만 확정이 가능합니다.");
        }
        this.status = BookingStatus.CONFIRMED;

        // [중요] Booking이 확정되면 산하 Ticket들도 모두 확정 처리
        for (Ticket ticket : this.tickets) {
            ticket.confirm();
        }
    }

    /**
     * 예매 전체 취소
     * - 사유가 반드시 필요합니다.
     * - 이미 취소된 건은 다시 취소할 수 없습니다.
     */
    public void cancel(String reason) {
        if (this.status == BookingStatus.CANCELLED) throw new IllegalStateException("이미 취소된 예매입니다.");

        this.status = BookingStatus.CANCELLED;
        this.cancelReason = reason;

        // [중요] 하위 티켓들도 모두 취소 처리
        for (Ticket ticket : this.tickets) {
            // 이미 취소된 티켓이 아닐 경우에만 취소
            if (ticket.getStatus() != io.why503.paymentservice.domain.booking.model.enums.TicketStatus.CANCELLED) {
                ticket.cancel();
            }
        }
    }

    public void partialCancel(long canceledAmount) {
        if (this.status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("이미 전체 취소된 예매입니다.");
        }

        long newFinalAmount = this.finalAmount - canceledAmount;
        if (newFinalAmount < 0) {
            throw new IllegalStateException("취소 금액이 남은 결제 금액보다 큽니다.");
        }

        this.finalAmount = newFinalAmount;

        // 남은 금액이 0원이면 '전체 취소'로 상태 변경, 아니면 '부분 취소'
        if (this.finalAmount == 0) {
            this.status = BookingStatus.CANCELLED;
            this.cancelReason = "모든 티켓 취소로 인한 자동 전체 취소";
        } else {
            this.status = BookingStatus.PARTIAL_CANCEL;
        }
    }

    /**
     * 금액 변경
     * - 티켓 선택 변경 등으로 인한 금액 업데이트
     * - 결제 전(PENDING) 상태에서만 가능합니다.
     */
    public void changeAmounts(long originalAmount, long finalAmount) {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("금액 변경은 예매 대기(PENDING) 상태에서만 가능합니다.");
        }
        if (originalAmount < 0 || finalAmount < 0) {
            throw new IllegalArgumentException("금액은 0원 이상이어야 합니다.");
        }

        this.originalAmount = originalAmount;
        this.finalAmount = finalAmount;
    }
}