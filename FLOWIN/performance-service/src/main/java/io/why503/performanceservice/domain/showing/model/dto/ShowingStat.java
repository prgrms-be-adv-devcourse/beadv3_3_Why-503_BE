package io.why503.performanceservice.domain.showing.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter //dbCode를 꺼낼 수 있게 해줌, getDbCode 자동 생성
@AllArgsConstructor // 생성자를 자동으로 만들어줌
public enum ShowingStat {
    //0: 예매가능, 1: 예매종료, 2: 회차취소

    AVAILABLE(0,"예매가능"),
    CLOSED(1,"예매종료"),
    CANCELLED(2,"예매취소");

    private final Integer dbCode; //DB에 저장될 숫자(0, 1, 2)
    private final String description; // 예매가능 등의 설명

}
