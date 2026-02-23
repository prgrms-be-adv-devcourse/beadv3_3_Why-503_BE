package io.why503.paymentservice.domain.settlement.mapper;

import io.why503.paymentservice.domain.settlement.model.dto.response.SettlementResponse;
import io.why503.paymentservice.domain.settlement.model.entity.Settlement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 정산 엔티티 객체를 외부 응답용 DTO로 변환하는 매퍼
 */
@Component
public class SettlementMapper {

    // 단일 정산 도메인 객체를 응답 규격에 맞게 변환
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

    // 다건의 정산 내역 목록을 응답 목록으로 일괄 변환
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