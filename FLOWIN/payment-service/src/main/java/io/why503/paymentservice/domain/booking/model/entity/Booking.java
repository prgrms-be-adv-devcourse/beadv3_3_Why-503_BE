package io.why503.paymentservice.domain.booking.model.entity;

import io.why503.paymentservice.domain.booking.model.enums.BookingStatus;
import io.why503.paymentservice.domain.booking.model.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 예매(주문) 엔티티
 * - 예매 정보, 결제 금액, 상태, 티켓 목록을 관리하는 핵심 애그리거트 루트입니다.
 */
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "booking")
@EntityListeners(AuditingEntityListener.class)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_sq")
    private Long sq; // bookingSq -> sq

    @Column(name = "user_sq", nullable = false)
    private Long userSq;

    @Setter
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING; // bookingStatus -> status

    @Column(name = "order_id", nullable = false, unique = true)
    @Builder.Default
    private String orderId = UUID.randomUUID().toString();

    // --- 금액 정보 ---

    @Setter
    @Column(name = "original_amount", nullable = false) // DB 컬럼 유지
    @Builder.Default
    private Integer originalAmount = 0; // bookingAmount -> originalAmount (의미 명확화)

    @Column(name = "final_amount", nullable = false)
    @Builder.Default
    private Integer finalAmount = 0; // totalAmount -> finalAmount (할인 후 최종 금액)

    @Column(name = "used_point", nullable = false)
    @Builder.Default
    private Integer usedPoint = 0;

    @Column(name = "pg_amount", nullable = false)
    @Builder.Default
    private Integer pgAmount = 0;

    // --- 결제 정보 ---

    @Column(name = "payment_method", nullable = false)
    @Builder.Default
    private String paymentMethod = "PENDING";

    @Setter
    @Column(name = "payment_key")
    private String paymentKey;

    @Setter
    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(name = "cancel_reason")
    private String cancelReason;

    // --- 시간 정보 ---

    @CreationTimestamp
    @Column(name = "booking_dt", nullable = false, updatable = false)
    private LocalDateTime reservedAt; // bookingDt -> reservedAt

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Setter
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // --- 연관 관계 ---

    @Builder.Default
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.setBooking(this);
    }

    // --- 비즈니스 로직 ---

    public void setFinalAmount(Integer amount) {
        this.finalAmount = amount;
        calculatePgAmount();
    }

    public void applyPoints(int pointsToUse) {
        if (pointsToUse > this.finalAmount) {
            pointsToUse = this.finalAmount;
        }
        this.usedPoint = pointsToUse;
        calculatePgAmount();
    }

    private void calculatePgAmount() {
        this.pgAmount = Math.max(0, this.finalAmount - this.usedPoint);
    }

    public void confirm(String paymentKey, String method) {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태에서만 승인이 가능합니다.");
        }
        this.status = BookingStatus.CONFIRMED;
        this.paymentKey = paymentKey;
        this.paymentMethod = method;
        this.approvedAt = LocalDateTime.now();

        this.tickets.forEach(Ticket::paid);
    }

    public void cancel(String reason) {
        this.status = BookingStatus.CANCELLED;
        this.cancelReason = reason;
        this.tickets.stream()
                .filter(t -> t.getStatus() != TicketStatus.CANCELLED)
                .forEach(Ticket::cancel);
    }

    public void cancelTicket(Long ticketSq, String reason) {
        Ticket targetTicket = findTicketOrThrow(ticketSq);

        if (targetTicket.getStatus() == TicketStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 티켓입니다.");
        }

        targetTicket.cancel();

        boolean hasActive = this.tickets.stream()
                .anyMatch(t -> t.getStatus() != TicketStatus.CANCELLED);

        if (!hasActive) {
            this.status = BookingStatus.CANCELLED;
            this.cancelReason = reason;
        } else {
            this.status = BookingStatus.PARTIAL_CANCEL;
        }

        recalculateAmounts();
    }

    public void deleteTicket(Long ticketSq) {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("선점 취소는 결제 대기 상태에서만 가능합니다.");
        }

        Ticket targetTicket = findTicketOrThrow(ticketSq);
        this.tickets.remove(targetTicket);
        targetTicket.setBooking(null);

        recalculateAmounts();
    }

    // --- Private Helper Methods ---

    private void recalculateAmounts() {
        if (this.tickets.isEmpty()) {
            this.originalAmount = 0;
            this.finalAmount = 0;
            this.usedPoint = 0;
            this.pgAmount = 0;
            return;
        }

        int sum = this.tickets.stream()
                .filter(t -> t.getStatus() != TicketStatus.CANCELLED)
                .mapToInt(Ticket::getFinalPrice)
                .sum();

        this.originalAmount = sum;
        this.finalAmount = sum; // 기본적으로 원가 합계가 최종가 (할인 로직이 있다면 여기서 추가)

        applyPoints(this.usedPoint);
    }

    private Ticket findTicketOrThrow(Long ticketSq) {
        return this.tickets.stream()
                .filter(t -> t.getSq().equals(ticketSq))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓입니다."));
    }

    @PrePersist
    public void prePersist() {
        if (this.originalAmount == null) this.originalAmount = 0;
        if (this.finalAmount == null) this.finalAmount = 0;
        if (this.usedPoint == null) this.usedPoint = 0;
        if (this.pgAmount == null) this.pgAmount = 0;
        if (this.status == null) this.status = BookingStatus.PENDING;
    }
}