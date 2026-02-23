package io.why503.paymentservice.domain.settlement.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 정산 처리 결과 및 상세 내역을 외부로 전달하기 위한 데이터 객체
 */
@Getter
@Builder
public class SettlementResponse {
        private Long sq;
        private Long companySq;
        private Long showSq;
        private Long totalAmount;
        private Long feeAmount;
        private Long settlementAmount;
        private String settlementStatus;
        private LocalDateTime settledDt;
        private LocalDateTime createdDt;
}