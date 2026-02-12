package io.why503.performanceservice.domain.roundSeat.model.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoundSeatStatus {
    WAIT("판매 대기"),
    AVAILABLE("판매 가능"),
    RESERVED("선점됨"),
    SOLD("판매완료"),
    LOCKED("판매제한");     //관리자가 막아둔 상태

    private final String description;

}
