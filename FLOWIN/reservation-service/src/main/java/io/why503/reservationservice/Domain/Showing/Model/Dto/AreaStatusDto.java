package io.why503.reservationservice.Domain.Showing.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AreaStatusDto {
    private String area;      // 구역 (A, B, C...)
    private String grade;     // 등급 (SS, S, R)
    private Integer price;    // 가격
    private Long remainCount; // 남은 좌석 수
}