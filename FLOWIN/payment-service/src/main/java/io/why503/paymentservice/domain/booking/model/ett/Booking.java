package io.why503.paymentservice.domain.booking.model.ett;

import io.why503.paymentservice.domain.booking.model.type.BookingStatus;
import io.why503.paymentservice.domain.booking.model.type.TicketStatus;
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

    @CreationTimestamp // INSERT 시 자동 생성
    @Column(name = "booking_dt", nullable = false, updatable = false)
    private LocalDateTime bookingDt;

    @Column(name = "booking_amount", nullable = false)
    private Integer bookingAmount;

    // --- [결제 정보 (NOT NULL 방어용)] ---
    // SQL 제약조건(NOT NULL) 회피를 위한 초기값 설정

    @Column(name = "total_amount", nullable = false)
    @Builder.Default
    private Integer totalAmount = 0;

    @Column(name = "used_point", nullable = false)
    @Builder.Default
    private Integer usedPoint = 0;

    @Column(name = "pg_amount", nullable = false)
    @Builder.Default
    private Integer pgAmount = 0;

    @Column(name = "payment_method", nullable = false)
    @Builder.Default
    private String paymentMethod = "PENDING"; // 결제 대기중

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "order_id", nullable = false, unique = true)
    @Builder.Default
    private String orderId = UUID.randomUUID().toString(); // 필수값: UUID 자동 생성

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "receipt_url")
    private String receiptUrl;

    @Column(name = "cancel_reason")
    private String cancelReason;

    // --- [시스템 시간] ---
    @CreationTimestamp // INSERT 시 자동 생성
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // --- [연관 관계] ---
    @Builder.Default
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addTicket(Ticket ticket) {
        this.tickets.add(ticket);
        ticket.setBooking(this);
    }

    // 비즈니스 로직
    // 전체 취소
    public void cancel() {
        this.bookingStatus = BookingStatus.CANCELLED;
        // 2. 연결된 티켓들도 모두 취소 상태로 변경
        for (Ticket ticket : this.tickets) {
            ticket.cancel();
        }
    }

    // 부분 취소 (새로 추가)
    public void cancelTicket(Long ticketSq, String reason) {
        // 1. 취소할 티켓 찾기
        Ticket targetTicket = this.tickets.stream()
                .filter(t -> t.getTicketSq().equals(ticketSq))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 예매에 존재하지 않는 티켓입니다."));

        // 2. 이미 취소된 티켓인지 확인
        if (targetTicket.getTicketStatus() == TicketStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 티켓입니다.");
        }

        // 3. 해당 티켓만 취소 상태로 변경
        targetTicket.cancel();

        // 4. 예매 상태(BookingStatus) 재산정 (남은 티켓 확인)
        boolean hasActiveTicket = this.tickets.stream()
                .anyMatch(t -> t.getTicketStatus() != TicketStatus.CANCELLED);

        if (!hasActiveTicket) {
            // 살아있는 티켓이 없으면 -> 전체 취소 처리
            this.bookingStatus = BookingStatus.CANCELLED;
            this.cancelReason = reason;
        } else {
            // 아직 살아있는 티켓이 있으면 -> 부분 취소 처리
            this.bookingStatus = BookingStatus.PARTIAL_CANCEL;
        }
    }

    // 예매 확정 (결제 완료 시)
    public void confirm(String paymentKey, String method) {
        // (BookingStatus가 Enum이라고 가정할 때)
        if (this.bookingStatus != BookingStatus.PENDING) {
            throw new IllegalStateException("결제 대기 중인 예약만 승인할 수 있습니다. (현재 상태: " + this.bookingStatus + ")");
        }
        this.bookingStatus = BookingStatus.CONFIRMED;
        this.paymentKey = paymentKey;          // PG사에서 받은 결제 키 저장
        this.approvedAt = LocalDateTime.now(); // 승인 시간 기록
        this.paymentMethod= method;

        // ★ 하위 티켓들도 모두 '결제됨(PAID)' 상태로 변경
        for (Ticket ticket : this.tickets) {
            ticket.paid();
        }
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.bookingDt == null) this.bookingDt = LocalDateTime.now();
        if (this.bookingStatus == null) this.bookingStatus = BookingStatus.PENDING; // PENDING
        // 가격 정보가 null이면 0원으로 강제 세팅
        if (this.bookingAmount == null) this.bookingAmount = 0;
        if (this.totalAmount == null) this.totalAmount = 0;
        if (this.usedPoint == null) this.usedPoint = 0;
        if (this.pgAmount == null) this.pgAmount = 0;

        // 결제 수단도 없으면 기본값
        if (this.paymentMethod == null) this.paymentMethod = "PENDING";
    }

}
