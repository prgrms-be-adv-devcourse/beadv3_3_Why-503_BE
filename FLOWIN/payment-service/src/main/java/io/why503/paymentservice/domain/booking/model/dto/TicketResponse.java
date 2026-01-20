package io.why503.paymentservice.domain.booking.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * [티켓 응답 DTO]
 * - 예매 완료 후 클라이언트(프론트)에게 내려줄 개별 티켓 정보입니다.
 * - 공연장 정보가 변경되어도 예매 내역은 변하지 않도록 '스냅샷' 데이터를 포함합니다.
 */
@Getter
@Builder
@AllArgsConstructor
public class TicketResponse {

    private Long ticketSq;
    private Long roundSeatSq;
    private String ticketUuid;

    // [공연 정보]
    private String showName;
    private String concertHallName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime roundDate;

    private String grade;           // 예: VIP (seatGrade X)
    private String seatArea;        // 예: A   (zone X)
    private Integer areaSeatNumber; // 예: 15  (seatNumber X)

    // [가격 및 상태]
    private Integer price;
    private String status;
}