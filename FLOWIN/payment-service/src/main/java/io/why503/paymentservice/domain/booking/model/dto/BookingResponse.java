package io.why503.paymentservice.domain.booking.model.dto;

import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * [예매 상세 응답 DTO]
 * - 예매 내역 확인(영수증) 화면에서 사용됩니다.
 * - 결제 금액 상세(총액, 할인, 포인트, 실결제액) 정보를 모두 포함합니다.
 */
@Getter
@Builder
@AllArgsConstructor
public class BookingResponse {

    // 1. 식별자 및 상태
    private Long bookingSq;         // 예매 고유 ID
    private Long userSq;            // 예매자 ID
    private BookingStatus bookingStatus; // 예매 상태 (예: PENDING, CONFIRMED, CANCELLED)

    // 2. 결제 금액 상세 정보 (영수증)
    private Integer bookingAmount;  // 순수 예매 금액 (티켓값 합계)
    private Integer totalAmount;    // 결제 대상 금액 (할인 적용 후)
    private Integer usedPoint;      // 사용한 포인트
    private Integer pgAmount;       // 실제 카드 결제 금액 (Total - Point)

    // 3. 시간 정보
    private LocalDateTime bookingDt; // 예매 일시

    // 4. 티켓 목록 (상세 정보 포함)
    private List<TicketResponse> tickets;
}