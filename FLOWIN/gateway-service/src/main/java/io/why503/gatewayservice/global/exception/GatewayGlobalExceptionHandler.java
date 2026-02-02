package io.why503.gatewayservice.global.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.why503.gatewayservice.auth.exception.JwtAuthenticationException;
import io.why503.gatewayservice.entrytoken.exception.EntryTokenRequiredException;
import io.why503.gatewayservice.entrytoken.exception.InvalidEntryRequestException;
import io.why503.gatewayservice.queue.exception.QueueWaitingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@Order(-2)
@RequiredArgsConstructor
public class GatewayGlobalExceptionHandler
        implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(
            org.springframework.web.server.ServerWebExchange exchange,
            Throwable ex
    ) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        /* =====================
           AUTH
        ====================== */
        if (ex instanceof JwtAuthenticationException e) {
            return writeErrorResponse(
                    response,
                    HttpStatus.UNAUTHORIZED,
                    Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "message", e.getMessage()
                    )
            );
        }

        /* =====================
           ENTRY TOKEN
        ====================== */
        if (ex instanceof EntryTokenRequiredException e) {
            return writeErrorResponse(
                    response,
                    HttpStatus.UNAUTHORIZED,
                    Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "message", e.getMessage()
                    )
            );
        }

        if (ex instanceof InvalidEntryRequestException e) {
            return writeErrorResponse(
                    response,
                    HttpStatus.BAD_REQUEST,
                    Map.of(
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "message", e.getMessage()
                    )
            );
        }

        /* =====================
           QUEUE
        ====================== */
        if (ex instanceof QueueWaitingException e) {

            // Retry-After 헤더
            response.getHeaders().add(
                    HttpHeaders.RETRY_AFTER,
                    String.valueOf(e.getRetryAfter())
            );

            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpStatus.ACCEPTED.value());
            body.put("message", e.getMessage());
            body.put("position", e.getPosition());
            body.put("total", e.getTotal());

            return writeErrorResponse(
                    response,
                    HttpStatus.ACCEPTED,
                    body
            );
        }

        /* =====================
           FALLBACK
        ====================== */
        log.error("Unhandled exception in gateway", ex);
        return writeErrorResponse(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR,
                Map.of(
                        "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "message", "internal server error"
                )
        );
    }

    private Mono<Void> writeErrorResponse(
            ServerHttpResponse response,
            HttpStatus status,
            Map<String, Object> body
    ) {
        response.setStatusCode(status);
        response.getHeaders().set(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE
        );

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (Exception e) {
            bytes = ("{\"message\":\"serialization error\"}")
                    .getBytes(StandardCharsets.UTF_8);
        }

        return response.writeWith(
                Mono.just(response.bufferFactory().wrap(bytes))
        );
    }
}
