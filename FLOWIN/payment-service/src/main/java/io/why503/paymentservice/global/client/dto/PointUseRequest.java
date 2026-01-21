package io.why503.paymentservice.global.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 포인트 사용 요청 DTO
 * - Account Service로 포인트 차감을 요청할 때 사용합니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointUseRequest {
    private Long userSq;   // 사용자 식별자
    private Integer amount; // 사용할(차감할) 포인트 금액
}