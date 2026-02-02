package io.why503.gatewayservice.auth.model.dto;

//오류반환에 쓰이는 메시지 body
public record JwtAuthResponseBody(
        String message
) {
}
