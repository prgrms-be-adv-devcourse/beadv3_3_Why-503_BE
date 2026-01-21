package io.why503.paymentservice.domain.booking.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 티켓 응답 DTO
 * - 공연장 정보가 변경되어도 예매 내역은 유지되도록 스냅샷 데이터를 포함합니다.
 */
@Getter
@Builder
@AllArgsConstructor
public class TicketResponse {

    private Long ticketSq;
    private Long roundSeatSq;
    private String ticketUuid;

    // 공연 정보 (스냅샷)
    private String showName;
    private String concertHallName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime roundDate;

    // 좌석 정보
    private String grade;           // 등급 (VIP, R, S ...)
    private String seatArea;        // 구역 (A, B ...)
    private Integer areaSeatNumber; // 번호

    // 가격 및 상태
    private Integer price;
    private String status;
}