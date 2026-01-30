/**
 * 대기열 진입이 필요한 경우 전달할 응답 Body
 */
package io.why503.gatewayservice.queue.model.dto;

public record QueueRejectResponseBody(
        String message,
        Long position,
        Long totalWationg
) {
}