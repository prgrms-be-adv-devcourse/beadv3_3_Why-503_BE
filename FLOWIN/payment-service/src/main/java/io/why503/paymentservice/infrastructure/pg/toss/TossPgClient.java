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

/**
 * 외부 결제 대행사와의 통신을 통해 실결제 승인 및 거래 취소를 처리하는 클라이언트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TossPgClient implements PgClient {

    private final RestTemplate restTemplate;

    @Value("${toss.client.api-url:https://api.tosspayments.com/v1/payments}")
    private String apiUrl;

    @Value("${toss.client.secret-key}")
    private String secretKey;

    // 결제 식별자와 주문 정보를 대조하여 대행사에 최종 승인 요청
    @Override
    public String approvePayment(String paymentKey, String orderId, Long amount) {
        HttpHeaders headers = createHeaders();

        Map<String, Object> body = Map.of(
                "paymentKey", paymentKey,
                "orderId", orderId,
                "amount", amount
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<TossPaymentResponse> response = restTemplate.postForEntity(
                    apiUrl + "/confirm",
                    request,
                    TossPaymentResponse.class
            );

            if (response.getBody() == null || response.getBody().paymentKey() == null) {
                throw new IllegalStateException("결제 대행사 승인 응답이 비어있습니다.");
            }

            return response.getBody().paymentKey();

        } catch (Exception e) {
            log.error("결제 승인 실패: {}", e.getMessage());
            throw new IllegalArgumentException("대행사 결제 승인 실패: " + e.getMessage());
        }
    }

    // 승인된 거래 키를 기반으로 전체 또는 부분 취소 명령 전송
    @Override
    public void cancelPayment(String pgKey, String reason, Long cancelAmount) {
        HttpHeaders headers = createHeaders();

        Map<String, Object> body = new HashMap<>();
        body.put("cancelReason", reason);

        if (cancelAmount != null && cancelAmount > 0) {
            body.put("cancelAmount", cancelAmount);
        }

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(
                    apiUrl + "/" + pgKey + "/cancel",
                    request,
                    Void.class
            );

            if (cancelAmount != null) {
                log.info("부분 결제 취소 성공: {}, 금액: {}", pgKey, cancelAmount);
            } else {
                log.info("전액 결제 취소 성공: {}", pgKey);
            }

        } catch (Exception e) {
            log.error("결제 취소 실패 (식별키: {}): {}", pgKey, e.getMessage());
            throw new IllegalStateException("대행사 결제 취소 처리에 실패했습니다.");
        }
    }

    // 보안 통신을 위한 인증 정보 및 데이터 형식 설정
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String encodedKey = Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        headers.setBasicAuth(encodedKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }

    private record TossPaymentResponse(
            String paymentKey,
            String status,
            Long totalAmount) {}
}