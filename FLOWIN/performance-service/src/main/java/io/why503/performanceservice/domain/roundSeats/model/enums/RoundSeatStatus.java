package io.why503.performanceservice.domain.roundSeats.model.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoundSeatStatus {
    AVAILABLE(0,"판매 가능"),
    RESERVED(1,"선점됨"),
    SOLD(2,"판매완료"),
    LOCKED(3,"판매제한");     //관리자가 막아둔 상태

    private final Integer dbCode;
    private final String description;

}
