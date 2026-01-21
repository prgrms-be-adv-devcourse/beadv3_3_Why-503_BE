package io.why503.paymentservice.global.client.dto;

import lombok.*;

/**
 * 회원 정보 응답 DTO
 * - Account Service로부터 받아온 회원 상세 정보입니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {

    private String name;
    private Integer point; // 보유 포인트
}