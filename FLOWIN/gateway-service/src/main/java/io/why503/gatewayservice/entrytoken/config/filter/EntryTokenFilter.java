package io.why503.gatewayservice.entrytoken.config.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.why503.gatewayservice.queue.model.dto.QueueRejectResponseBody;
import io.why503.gatewayservice.entrytoken.service.EntryTokenValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * Queue 통과 후 발급받은 EntryToken이 없으면 예매 API 접근 차단
 * - JwtValidationFilter가 먼저 실행되어 X-USER-SQ 헤더가 존재
 * - QueueCheckFilter가 통과 시 EntryTokenIssuer.issue() 수행
 */
@Slf4j
@Component
public class EntryTokenFilter
        extends AbstractGatewayFilterFactory<EntryTokenFilter.Config> {

    private final EntryTokenValidator entryTokenValidator;
    private final ObjectMapper om = new ObjectMapper();

    public static class Config {
        // 앞쪽은 다음에 탐색하고 오자!
    }

    public EntryTokenFilter(EntryTokenValidator entryTokenValidator) {
        super(Config.class);
        this.entryTokenValidator = entryTokenValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            ServerHttpResponse response = exchange.getResponse();
            ServerHttpRequest request = exchange.getRequest();

            String userId = request.getHeaders().getFirst("X-USER-SQ");
        
            // URL path에서 공연 식별자 추출
            String showId = extractShowId(request);

            // 필수 정보 누락시 잘못된 요청 때리기
            if (userId == null || showId == null) {
                return response.writeWith(
                        Flux.just(writeResponse(
                                response,
                                HttpStatus.BAD_REQUEST,
                                "invalid request"
                        ))
                );
            }

            // EntryToken 유효성 검증
            boolean valid = entryTokenValidator.isValid(showId, userId);
            if (!valid) {
                // EntryToken 없으면 입장 불가
                return response.writeWith(
                        Flux.just(writeResponse(
                                response,
                                HttpStatus.UNAUTHORIZED,
                                "entry token required"
                        ))
                );
            }
            // 실제 서비스로 전달
            return chain.filter(exchange);
        };
    }

    // showId 추출
    private String extractShowId(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        String[] parts = path.split("/");

        // ex: /performances/{showId}/entry
        if (parts.length >= 4 && "performance".equals(parts[1])) {
            return parts[2];
        }
        return null;
    }

    // 응답 생성
    private DataBuffer writeResponse(
            ServerHttpResponse response,
            HttpStatus status,
            String message
    ) {
        response.setStatusCode(status);
        response.getHeaders().add(
            HttpHeaders.CONTENT_TYPE, 
            "application/json");

        // Queue와 Entry 공통 응답 포맷
        QueueRejectResponseBody body =
            new QueueRejectResponseBody(
                    message,
                    null,
                    null
            );

        return response.bufferFactory().wrap(toBytes(body));
    }

    // Json byte[]로 직렬화
    private byte[] toBytes(QueueRejectResponseBody body) {
        try {
            return om.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            // log.error("Failed to serialize response body", e);
            throw new RuntimeException(e);
        }
    }
}
