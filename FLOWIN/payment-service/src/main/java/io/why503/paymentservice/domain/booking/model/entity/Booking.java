package io.why503.paymentservice.domain.booking.model.entity;

import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import io.why503.paymentservice.domain.booking.model.vo.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "booking")
@EntityListeners(AuditingEntityListener.class)
public class Booking {

    // =================================================================
    //  1. 기본 식별자 및 상태
    // =================================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_sq")
    private Long bookingSq;

    @Column(name = "user_sq", nullable = false)
    private Long userSq;

    @Column(name = "booking_status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    @Column(name = "order_id", nullable = false, unique = true)
    @Builder.Default
    private String orderId = UUID.randomUUID().toString(); // 결제용 고유 ID

    // =================================================================
    //  2. 결제 금액 및 수단
    // =================================================================
    @Column(name = "booking_amount", nullable = false)
    @Builder.Default
    private Integer bookingAmount = 0; // 순수 예매 금액

    @Column(name = "used_point", nullable = false)
    @Builder.Default
    private Integer usedPoint = 0;

    @Column(name = "pg_amount", nullable = false)
    @Builder.Default
    private Integer pgAmount = 0; // 실제 결제 금액 (총액 - 포인트)

    @Column(name = "total_amount", nullable = false)
    @Builder.Default
    private Integer totalAmount = 0;

    @Column(name = "payment_method", nullable = false)
    @Builder.Default
    private String paymentMethod = "PENDING";

    @Column(name = "payment_key")
    private String paymentKey; // PG사 승인 키

    @Column(name = "receipt_url")
    private String receiptUrl; // 영수증 URL

    @Column(name = "cancel_reason")
    private String cancelReason;

    // =================================================================
    //  3. 시간 정보 (Auditing)
    // =================================================================
    @CreationTimestamp
    @Column(name = "booking_dt", nullable = false, updatable = false)
    private LocalDateTime bookingDt; // 예매 일시

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 데이터 생성 일시

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정 일시

    @Column(name = "approved_at")
    private LocalDateTime approvedAt; // 결제 승인 일시

    // =================================================================
    //  4. 연관 관계 (중요!)
    // =================================================================
    @Builder.Default
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * [연관관계 편의 메서드]
     * 티켓을 추가할 때 부모(Booking) 정보도 자동으로 주입
     */
    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.setBooking(this);
    }

    // =================================================================
    //  5. 비즈니스 로직 (상태 변경)
    // =================================================================

    /**
     * [결제 확정] PG사 승인 완료 시 호출
     */
    public void confirm(String paymentKey, String method) {
        if (this.bookingStatus != BookingStatus.PENDING) {
            throw new IllegalStateException("결제 대기 상태에서만 승인이 가능합니다.");
        }
        this.bookingStatus = BookingStatus.CONFIRMED;
        this.paymentKey = paymentKey;
        this.paymentMethod = method;
        this.approvedAt = LocalDateTime.now();

        // 하위 티켓들도 모두 '결제됨' 처리
        for (Ticket ticket : this.tickets) {
            ticket.paid();
        }
    }

    /**
     * [전체 취소] 결제 후 환불
     */
    public void cancel(String reason) {
        this.bookingStatus = BookingStatus.CANCELLED;
        this.cancelReason = reason;

        for (Ticket t : this.tickets) {
            if (t.getTicketStatus() != TicketStatus.CANCELLED) {
                t.cancel();
            }
        }
    }

    /**
     * [부분 취소] 특정 티켓만 환불
     */
    public void cancelTicket(Long ticketSq, String reason) {
        Ticket targetTicket = findTicketOrThrow(ticketSq);

        if (targetTicket.getTicketStatus() == TicketStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 티켓입니다.");
        }

        // 티켓 취소
        targetTicket.cancel();

        // 남은 티켓이 하나도 없으면 '전체 취소'로 상태 변경
        boolean hasActive = false;
        for (Ticket t : this.tickets) {
            if (t.getTicketStatus() != TicketStatus.CANCELLED) {
                hasActive = true;
                break;
            }
        }

        if (!hasActive) {
            this.bookingStatus = BookingStatus.CANCELLED;
            this.cancelReason = reason;
        } else {
            this.bookingStatus = BookingStatus.PARTIAL_CANCEL;
        }

        // 금액 재계산이 필요하다면 여기서 호출 (PG사 부분 취소 로직에 따라 다름)
        recalculateAmounts();
    }

    /**
     * [선점 취소] 결제 전(PENDING) 상태에서 티켓 삭제 (Hard Delete)
     */
    public void deleteTicket(Long ticketSq) {
        if (this.bookingStatus != BookingStatus.PENDING) {
            throw new IllegalStateException("선점 취소는 결제 대기 상태에서만 가능합니다.");
        }

        Ticket targetTicket = findTicketOrThrow(ticketSq);

        // 리스트에서 제거 -> OrphanRemoval 동작으로 DB 삭제됨
        this.tickets.remove(targetTicket);
        targetTicket.setBooking(null);

        // 티켓이 줄었으니 금액 재계산
        recalculateAmounts();
    }

    // =================================================================
    //  6. 내부 로직 (Private Helpers)
    // =================================================================

    // 금액 재계산 (티켓 삭제/취소 시 호출)
    private void recalculateAmounts() {
        if (this.tickets.isEmpty()) {
            this.bookingAmount = 0;
            this.totalAmount = 0;
            this.pgAmount = 0;
            return;
        }

        // 부분 취소 시에도 유효한 티켓들의 가격만 합산
        int sum = 0;
        for (Ticket ticket : this.tickets) {
            if (ticket.getTicketStatus() != TicketStatus.CANCELLED) {
                int finalPrice = ticket.getFinalPrice();
                sum += finalPrice;
            }
        }

        this.bookingAmount = sum;
        this.totalAmount = sum;
        this.pgAmount = Math.max(0, sum - this.usedPoint);
    }

    // 티켓 찾기 (중복 코드 제거)
    private Ticket findTicketOrThrow(Long ticketSq) {
        return this.tickets.stream()
                .filter(t -> t.getTicketSq().equals(ticketSq))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓입니다."));
    }

    // NULL 방어 로직 (JPA 저장 전 실행)
    @PrePersist
    public void prePersist() {
        if (this.bookingAmount == null) this.bookingAmount = 0;
        if (this.totalAmount == null) this.totalAmount = 0;
        if (this.usedPoint == null) this.usedPoint = 0;
        if (this.pgAmount == null) this.pgAmount = 0;
        if (this.bookingStatus == null) this.bookingStatus = BookingStatus.PENDING;
    }
}