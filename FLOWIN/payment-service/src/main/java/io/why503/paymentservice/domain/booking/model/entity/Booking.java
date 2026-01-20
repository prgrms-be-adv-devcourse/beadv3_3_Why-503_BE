package io.why503.paymentservice.domain.booking.model.entity;

import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import io.why503.paymentservice.domain.booking.model.vo.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "booking")
@EntityListeners(AuditingEntityListener.class)
public class Booking {

    // =================================================================
    //  1. 식별자 및 기본 정보
    // =================================================================
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_sq")
    private Long bookingSq;

    @Setter
    @Column(name = "user_sq", nullable = false)
    private Long userSq;

    @Column(name = "booking_status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @Builder.Default
    private BookingStatus bookingStatus = BookingStatus.PENDING;

    @Column(name = "order_id", nullable = false, unique = true)
    @Builder.Default
    private String orderId = UUID.randomUUID().toString(); // PG사 결제용 고유 ID

    // =================================================================
    //  2. 금액 정보 (가장 중요!)
    //  - bookingAmount: 티켓 가격의 합 (순수 상품 금액)
    //  - totalAmount: 할인/부가세 적용 후 결제 대상 금액 (포인트 적용 전)
    //  - usedPoint: 사용한 포인트
    //  - pgAmount: 실제 PG사 결제 금액 (totalAmount - usedPoint)
    // =================================================================
    @Setter
    @Column(name = "booking_amount", nullable = false)
    @Builder.Default
    private Integer bookingAmount = 0;

    @Column(name = "total_amount", nullable = false)
    @Builder.Default
    private Integer totalAmount = 0;

    @Column(name = "used_point", nullable = false)
    @Builder.Default
    private Integer usedPoint = 0;

    @Column(name = "pg_amount", nullable = false)
    @Builder.Default
    private Integer pgAmount = 0;

    // =================================================================
    //  3. 결제 수단 및 취소 정보
    // =================================================================
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
    //  4. 시간 정보 (Auditing)
    // =================================================================
    @CreationTimestamp
    @Column(name = "booking_dt", nullable = false, updatable = false)
    private LocalDateTime bookingDt; // 예매 시도 일시

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt; // 결제 승인 일시

    // =================================================================
    //  5. 연관 관계
    // =================================================================
    @Builder.Default
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * [연관관계 편의 메서드]
     * 티켓 추가 시 부모(Booking) 정보도 자동으로 주입
     */
    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.setBooking(this);
    }

    // =================================================================
    //  6. 핵심 비즈니스 로직 (금액 계산)
    // =================================================================

    /**
     * 최종 결제 대상 금액 설정
     * - 할인이나 부가세가 적용된 후의 금액입니다.
     * - 설정 즉시 PG 결제액(pgAmount)을 재계산합니다.
     */
    public void setTotalAmount(Integer amount) {
        this.totalAmount = amount;
        calculatePgAmount(); // 자동 재계산
    }


    /**
     * 포인트 적용
     * - 사용할 포인트를 설정하고 PG 결제액을 재계산합니다.
     */
    public void applyPoints(int pointsToUse) {
        // 결제 총액보다 많은 포인트를 사용할 수는 없음
        if (pointsToUse > this.totalAmount) {
            pointsToUse = this.totalAmount;
        }
        this.usedPoint = pointsToUse;
        calculatePgAmount(); // 자동 재계산

        log.info(">>> [Entity] 포인트 적용 완료 | 총액: {}, 포인트: {}, 최종결제액: {}",
                this.totalAmount, this.usedPoint, this.pgAmount);
    }

    /**
     * [내부 로직] PG 결제 금액 계산기
     * - 공식: TotalAmount(총액) - UsedPoint(포인트) = PgAmount(실결제액)
     * - 마이너스 금액이 나오지 않도록 방어합니다.
     */
    private void calculatePgAmount() {
        this.pgAmount = Math.max(0, this.totalAmount - this.usedPoint);
    }

    // =================================================================
    //  7. 상태 변경 로직 (결제/취소)
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
     * [전체 취소]
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

        // 티켓 취소 처리
        targetTicket.cancel();

        // 남은 티켓이 없으면 '전체 취소'로 상태 변경
        boolean hasActive = this.tickets.stream()
                .anyMatch(t -> t.getTicketStatus() != TicketStatus.CANCELLED);

        if (!hasActive) {
            this.bookingStatus = BookingStatus.CANCELLED;
            this.cancelReason = reason;
        } else {
            this.bookingStatus = BookingStatus.PARTIAL_CANCEL;
        }

        // 취소 후 금액 재계산
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

        // 리스트에서 제거 -> OrphanRemoval로 인해 DB 삭제
        this.tickets.remove(targetTicket);
        targetTicket.setBooking(null);

        // 금액 재계산
        recalculateAmounts();
    }

    // =================================================================
    //  6. 내부 로직 (Private Helpers)
    // =================================================================

    /**
     * 금액 재계산 (티켓 삭제/취소 시 호출)
     */
    private void recalculateAmounts() {
        if (this.tickets.isEmpty()) {
            this.bookingAmount = 0;
            this.totalAmount = 0;
            this.usedPoint = 0; // 티켓이 없으면 포인트 사용도 취소
            this.pgAmount = 0;
            return;
        }

        // 유효한 티켓들의 가격 합산
        int sum = this.tickets.stream()
                .filter(t -> t.getTicketStatus() != TicketStatus.CANCELLED)
                .mapToInt(Ticket::getFinalPrice)
                .sum();

        this.bookingAmount = sum;
        this.totalAmount = sum; // 현재는 추가 할인 로직이 없으므로 동일하게 설정

        // 포인트가 총액보다 많아졌을 경우를 대비해 다시 적용
        applyPoints(this.usedPoint);
    }

    // 티켓 찾기 (중복 코드 제거)
    private Ticket findTicketOrThrow(Long ticketSq) {
        return this.tickets.stream()
                .filter(t -> t.getTicketSq().equals(ticketSq))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓입니다."));
    }

    @PrePersist
    public void prePersist() {
        if (this.bookingAmount == null) this.bookingAmount = 0;
        if (this.totalAmount == null) this.totalAmount = 0;
        if (this.usedPoint == null) this.usedPoint = 0;
        if (this.pgAmount == null) this.pgAmount = 0;
        if (this.bookingStatus == null) this.bookingStatus = BookingStatus.PENDING;
    }
}