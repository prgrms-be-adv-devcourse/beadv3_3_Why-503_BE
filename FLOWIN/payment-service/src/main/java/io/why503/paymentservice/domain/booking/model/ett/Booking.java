package io.why503.paymentservice.domain.booking.model.ett;

import io.why503.paymentservice.domain.booking.model.type.BookingStatus;
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
    // 1. 전체 취소
    public void cancel() {
        this.bookingStatus = BookingStatus.CANCELLED;
        // 2. 연결된 티켓들도 모두 취소 상태로 변경
        for (Ticket ticket : this.tickets) {
            ticket.cancel();
        }
    }

    // 2. ★ 부분 취소 (새로 추가)
    // 티켓 중 일부만 취소되었을 때 호출
    public void partialCancel() {
        this.bookingStatus = BookingStatus.PARTIAL_CANCEL;
    }

    // 3. 예매 확정 (결제 완료 시)
    public void confirm(String paymentKey) {
        this.bookingStatus = BookingStatus.CONFIRMED;
        this.paymentKey = paymentKey;          // PG사에서 받은 결제 키 저장
        this.approvedAt = LocalDateTime.now(); // 승인 시간 기록
        this.paymentMethod = "CARD";           // (나중에 파라미터로 확장 가능)

        // ★ 하위 티켓들도 모두 '결제됨(PAID)' 상태로 변경
        for (Ticket ticket : this.tickets) {
            ticket.paid();
        }
    }
}
