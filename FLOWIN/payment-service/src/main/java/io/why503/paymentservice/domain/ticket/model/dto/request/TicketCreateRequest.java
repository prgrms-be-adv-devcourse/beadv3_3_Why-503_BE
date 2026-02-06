package io.why503.paymentservice.domain.ticket.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * 예매할 개별 좌석의 식별자와 적용할 할인 정책을 담는 요청 객체
 */
public record TicketCreateRequest(
        @NotNull(message = "회차 ID는 필수입니다.")
        Long roundSq,

        @NotEmpty(message = "생성할 좌석 ID 목록은 필수입니다.")
        List<Long> roundSeatSqs
) {

}