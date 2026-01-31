package io.why503.paymentservice.infrastructure.pg.toss;

import io.why503.paymentservice.global.client.PgClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPgClient implements PgClient {

    // 기존에 등록된 RestTemplate Bean을 주입받습니다.
    private final RestTemplate restTemplate;

    // [수정] YAML 경로에 맞춰서 client.api-url로 변경
    @Value("${toss.client.api-url:https://api.tosspayments.com/v1/payments}")
    private String apiUrl;

    // [수정] YAML 경로에 맞춰서 client.secret-key로 변경
    @Value("${toss.client.secret-key}")
    private String secretKey;

    /**
     * Toss 결제 승인 요청
     */
    @Override
    public String approvePayment(String paymentKey, String orderId, Long amount) {
        // 1. 헤더 설정 (Basic Auth)
        HttpHeaders headers = createHeaders();

        // 2. 바디 설정
        Map<String, Object> body = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            // 3. API 호출
            ResponseEntity<TossPaymentResponse> response = restTemplate.postForEntity(
                    apiUrl + "/confirm",
                    request,
                    TossPaymentResponse.class
            );

            if (response.getBody() == null || response.getBody().paymentKey() == null) {
                throw new IllegalStateException("Toss 결제 승인 응답이 비어있습니다.");
            }

            return response.getBody().paymentKey();

        } catch (Exception e) {
            log.error("Toss 결제 승인 실패: {}", e.getMessage());
            throw new IllegalArgumentException("PG사 결제 승인 실패: " + e.getMessage());
        }
    }

    /**
     * Toss 결제 취소 요청
     */
    @Override
    public void cancelPayment(String pgKey, String reason, Long cancelAmount) {
        HttpHeaders headers = createHeaders();

        // 1. 요청 바디 구성
        Map<String, Object> body = new HashMap<>();
        body.put("cancelReason", reason);

        // cancelAmount가 존재할 경우에만 바디에 추가 (부분 취소 시 필수)
        if (cancelAmount != null && cancelAmount > 0) {
            body.put("cancelAmount", cancelAmount);
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            // 토스페이먼츠 취소 API 엔드포인트: /v1/payments/{paymentKey}/cancel
            restTemplate.postForEntity(
                    apiUrl + "/" + pgKey + "/cancel",
                    request,
                    Void.class
            );

            if (cancelAmount != null) {
                log.info("Toss 부분 결제 취소 성공: {}, 금액: {}", pgKey, cancelAmount);
            } else {
                log.info("Toss 전액 결제 취소 성공: {}", pgKey);
            }

        } catch (Exception e) {
            log.error("Toss 결제 취소 실패 (pgKey: {}): {}", pgKey, e.getMessage());
            throw new IllegalStateException("PG사 결제 취소 처리에 실패했습니다.");
        }
    }

    // 공통 헤더 생성 (Authorization)
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.setBasicAuth(encodedKey); // Spring 5.2+ 부터 지원
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    // 응답 매핑용 DTO
    private record TossPaymentResponse(
            String paymentKey,
            String status,
            Long totalAmount) {}
}