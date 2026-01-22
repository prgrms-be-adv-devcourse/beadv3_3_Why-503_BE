package io.why503.paymentservice.domain.payment.service;

import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import io.why503.paymentservice.domain.booking.repository.BookingRepository;
import io.why503.paymentservice.domain.booking.service.BookingService;
import io.why503.paymentservice.domain.payment.config.TossPaymentConfig;
import io.why503.paymentservice.domain.payment.dto.PaymentCancelRequest;
import io.why503.paymentservice.domain.payment.dto.PaymentConfirmRequest;
import io.why503.paymentservice.domain.payment.dto.TossPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 결제 서비스
 * - PG사(토스) 연동 및 결제 상태 관리를 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;
    private final TossPaymentConfig tossPaymentConfig;
    private final RestTemplate restTemplate;

    private static final String TOSS_API_URL = "https://api.tosspayments.com/v1/payments/";

    /**
     * 결제 승인 (최종)
     * - PG 승인을 먼저 수행하고, 성공 시 내부 데이터(포인트/좌석)를 처리합니다.
     * - 내부 처리가 실패하면 반드시 PG 결제를 취소(환불)해야 합니다. (보상 트랜잭션)
     */
    @Transactional
    public void confirmPayment(PaymentConfirmRequest request) {
        // 중복 승인 방지 및 금액 위변조 검증
        Booking booking = validateBooking(request);

        try {
            // 1. PG사 승인 요청 (실제 과금 발생)
            TossPaymentResponse result = requestConfirmToToss(request.paymentKey(), request.orderId(), request.amount());

            // 영수증 URL 저장 (나중에 사용자에게 보여주기 위함)
            booking.setReceiptUrl(result.receipt().url());

            try {
                // 2. 내부 비즈니스 로직 수행 (BookingService)
                // -> 여기서 포인트 차감, 좌석 확정 등이 일어납니다.
                bookingService.confirmBooking(
                        booking.getBookingSq(),
                        result.paymentKey(),
                        result.method(),
                        booking.getUserSq()
                );
            } catch (Exception bizEx) {
                // [중요] 보상 트랜잭션 (Compensating Transaction)
                // PG 승인은 났는데(돈은 나갔는데) 내부 로직(DB/포인트)이 터진 상황입니다.
                // 데이터 정합성을 위해 즉시 PG 결제를 취소(환불)해줍니다.
                log.error(">>> [Payment] 내부 로직 실패로 인한 PG 자동 취소 진행: {}", bizEx.getMessage());

                try {
                    requestCancelToToss(result.paymentKey(), "시스템 오류(내부 로직 실패)로 인한 자동 취소", null);
                } catch (Exception cancelEx) {
                    // 환불조차 실패하면 개발자가 수동으로 처리해야 하므로 CRITICAL 로그를 남깁니다.
                    log.error(">>> [CRITICAL] PG 취소 실패! 수동 환불 필요. Key={}", result.paymentKey());
                }

                // 원래 발생한 예외를 던져서, 바깥쪽 catch 블록으로 이동시킵니다.
                throw bizEx;
            }

        } catch (Exception e) {
            // 3. 최종 실패 처리
            // PG 승인 실패 or 내부 로직 실패(보상 처리 후) 모든 경우에 도달합니다.
            // 예매 상태를 '실패/취소'로 변경하여 종료합니다.
            log.error(">>> [Payment] 결제 프로세스 최종 실패: {}", e.getMessage());
            booking.cancel("결제 프로세스 실패: " + e.getMessage());
            throw e;
        }
    }

    /**
     * 결제 취소 (전체/부분)
     * - 티켓 ID 유무에 따라 부분 취소와 전체 취소를 구분합니다.
     */
    @Transactional
    public void cancelPayment(PaymentCancelRequest request) {
        Booking booking = bookingRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (booking.getPaymentKey() == null) {
            throw new IllegalStateException("결제 완료된 건만 취소할 수 있습니다.");
        }

        Integer cancelAmount = null;

        // 1. DB 및 내부 상태 취소 (먼저 수행)
        // 외부 API 호출 전에 DB 상태를 먼저 바꾸고, 실패 시 롤백되는 것이 안전합니다.
        if (request.ticketSq() != null) {
            Ticket targetTicket = booking.getTickets().stream()
                    .filter(t -> t.getTicketSq().equals(request.ticketSq()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("티켓이 존재하지 않습니다."));

            cancelAmount = targetTicket.getFinalPrice();
            bookingService.cancelTicket(booking.getBookingSq(), request.ticketSq(), booking.getUserSq());

        } else {
            bookingService.cancelBooking(booking.getBookingSq(), booking.getUserSq());
        }

        // 2. PG사 취소 요청 (실패 시 @Transactional로 인해 1번 로직도 롤백됨)
        try {
            requestCancelToToss(booking.getPaymentKey(), request.cancelReason(), cancelAmount);
        } catch (Exception e) {
            log.error("PG사 취소 요청 실패: {}", e.getMessage());
            throw new IllegalStateException("PG사 결제 취소 실패 (잠시 후 다시 시도해주세요)");
        }
    }

    /**
     * 결제 실패 처리 (단순 상태 변경)
     */
    @Transactional
    public void failPayment(String orderId, String reason) {
        Booking booking = bookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 이미 처리된 건이 아닐 때만 실패로 마킹
        if (booking.getBookingStatus() == BookingStatus.PENDING) {
            booking.cancel(reason != null ? reason : "결제 실패");
        }
    }

    // --- Private Helper Methods ---

    /**
     * 유효성 검증
     * - 이미 결제된 건인지, 프론트에서 보낸 금액이 DB와 일치하는지 확인합니다.
     */
    private Booking validateBooking(PaymentConfirmRequest request) {
        Booking booking = bookingRepository.findByOrderId(request.orderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        if (booking.getBookingStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 결제입니다.");
        }
        // 금액 위변조 방지 (DB 금액 vs 요청 금액)
        if (!booking.getPgAmount().equals(request.amount())) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
        }
        return booking;
    }

    private TossPaymentResponse requestConfirmToToss(String paymentKey, String orderId, Integer amount) {
        HttpHeaders headers = getTossHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId", orderId);
        body.put("amount", amount);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        return restTemplate.postForEntity(TOSS_API_URL + "confirm", entity, TossPaymentResponse.class).getBody();
    }

    private void requestCancelToToss(String paymentKey, String reason, Integer cancelAmount) {
        HttpHeaders headers = getTossHeaders();
        Map<String, Object> body = new HashMap<>();
        body.put("cancelReason", reason);
        // 부분 취소일 때만 금액을 전송 (null이면 전액 취소로 처리됨)
        if (cancelAmount != null) body.put("cancelAmount", cancelAmount);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(TOSS_API_URL + paymentKey + "/cancel", entity, Map.class);
    }

    /**
     * 토스 인증 헤더 생성 (Basic Auth)
     * - SecretKey 뒤에 콜론(:)을 붙여서 Base64로 인코딩해야 합니다.
     */
    private HttpHeaders getTossHeaders() {
        String authValue = Base64.getEncoder().encodeToString(
                (tossPaymentConfig.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8)
        );
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + authValue);
        headers.set("Content-Type", "application/json");
        return headers;
    }
}