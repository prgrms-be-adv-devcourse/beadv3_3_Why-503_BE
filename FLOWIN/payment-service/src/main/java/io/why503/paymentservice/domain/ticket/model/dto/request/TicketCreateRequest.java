package io.why503.paymentservice.domain.ticket.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * 공연 회차에 따른 티켓 슬롯 일괄 생성을 위한 요청 객체
 * - 공연 서비스의 좌석 정보와 티켓 데이터를 동기화하기 위해 사용
 */
public record TicketCreateRequest(
        @NotNull(message = "회차 ID는 필수입니다.")
        Long roundSq,

        @NotEmpty(message = "생성할 좌석 ID 목록은 필수입니다.")
        List<Long> roundSeatSqs
) {

}