package io.why503.paymentservice.domain.booking.model.dto;

import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 예매 상세 응답 DTO
 * - 예매 내역(영수증) 화면에서 사용되며, 금액 상세 정보를 포함합니다.
 */
@Getter
@Builder
@AllArgsConstructor
public class BookingResponse {

    // 식별자 및 상태
    private Long bookingSq;
    private Long userSq;
    private BookingStatus bookingStatus;

    // 결제 금액 상세 (영수증)
    private Integer bookingAmount;  // 티켓 총액 (정가 합계)
    private Integer totalAmount;    // 할인 적용 후 금액
    private Integer usedPoint;      // 포인트 사용액
    private Integer pgAmount;       // 실 결제 금액

    // 시간 정보
    private LocalDateTime bookingDt;

    // 상세 티켓 목록
    private List<TicketResponse> tickets;
}