package io.why503.performanceservice.domain.round.model.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter //dbCode를 꺼낼 수 있게 해줌, getDbCode 자동 생성
@AllArgsConstructor // 생성자를 자동으로 만들어줌
public enum RoundStatus {

    AVAILABLE("예매가능"),
    CLOSED("예매종료"),
    CANCELLED("예매취소"),
    WAIT("예매대기");

    private final String description; // 예매가능 등의 설명

}