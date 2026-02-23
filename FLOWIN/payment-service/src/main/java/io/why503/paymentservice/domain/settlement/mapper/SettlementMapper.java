package io.why503.paymentservice.domain.settlement.mapper;

import io.why503.paymentservice.domain.settlement.model.dto.response.SettlementResponse;
import io.why503.paymentservice.domain.settlement.model.entity.Settlement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SettlementMapper {

    public SettlementResponse toResponse(Settlement settlement) {
        if (settlement == null) {
            return null;
        }

        return SettlementResponse.builder()
                .sq(settlement.getSq())
                .companySq(settlement.getCompanySq())
                .showSq(settlement.getShowSq())
                .totalAmount(settlement.getTotalAmount())
                .feeAmount(settlement.getFeeAmount())
                .settlementAmount(settlement.getSettlementAmount())
                .settlementStatus(settlement.getSettlementStatus().name())
                .settledDt(settlement.getSettledDt())
                .createdDt(settlement.getCreatedDt())
                .build();
    }

    public List<SettlementResponse> toResponseList(List<Settlement> settlements) {
        if (settlements == null || settlements.isEmpty()) {
            return new ArrayList<>();
        }

        List<SettlementResponse> responseList = new ArrayList<>();
        for (Settlement settlement : settlements) {
            responseList.add(toResponse(settlement));
        }
        return responseList;
    }
}