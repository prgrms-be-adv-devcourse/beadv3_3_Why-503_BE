package io.why503.paymentservice.domain.settlement.model.dto.response;

import java.time.LocalDateTime;

/**
 * 정산 처리 결과 및 상세 내역을 외부로 전달하기 위한 데이터 객체
 */
public record SettlementResponse(
        Long sq,
        Long companySq,
        Long showSq,
        Long totalAmount,
        Long feeAmount,
        Long settlementAmount,
        String settlementStatus,
        LocalDateTime settledDt,
        LocalDateTime createdDt
) {
}