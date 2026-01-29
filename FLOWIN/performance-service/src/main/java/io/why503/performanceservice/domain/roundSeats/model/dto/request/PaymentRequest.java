package io.why503.performanceservice.domain.roundSeats.model.dto.request;

import java.util.List;

public record PaymentRequest(
        Long userSq,                //유저 식별자
        List<Long> roundSeatSqs     //회차 좌석 시퀀스 리스트
) {
}
