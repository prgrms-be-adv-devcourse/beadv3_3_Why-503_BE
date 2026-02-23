package io.why503.paymentservice.domain.settlement.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

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