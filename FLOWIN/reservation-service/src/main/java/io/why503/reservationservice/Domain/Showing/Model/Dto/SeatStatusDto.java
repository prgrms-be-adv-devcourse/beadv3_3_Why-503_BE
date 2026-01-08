package io.why503.reservationservice.Domain.Showing.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatStatusDto {
    private Long showingSeatSq; // 좌석 고유 번호
    private String seatArea;    // 구역 (A, B...)
    private Integer seatNo;     // 좌석 번호 (1, 2...)
    private Integer status;     // 상태 (0:빈자리, 1:선점, 2:판매완료)
}