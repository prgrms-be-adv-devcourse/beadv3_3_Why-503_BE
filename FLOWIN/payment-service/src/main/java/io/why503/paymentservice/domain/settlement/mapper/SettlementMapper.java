package io.why503.paymentservice.domain.settlement.mapper;

import io.why503.paymentservice.domain.settlement.model.dto.response.SettlementResponse;
import io.why503.paymentservice.domain.settlement.model.entity.Settlement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SettlementMapper {

    // 단건 엔티티 -> Record DTO 변환 (Builder 대신 생성자 직접 호출)
    public SettlementResponse entityToResponse(Settlement settlement) {
        if (settlement == null) {
            return null;
        }

        return new SettlementResponse(
                settlement.getSq(),
                settlement.getCompanySq(),
                settlement.getShowSq(),
                settlement.getTotalAmount(),
                settlement.getFeeAmount(),
                settlement.getSettlementAmount(),
                settlement.getSettlementStatus().name(), // Enum을 String으로 안전하게 변환
                settlement.getSettledDt(),
                settlement.getCreatedDt()
        );
    }

    // 리스트 변환 (Stream 및 메서드 참조 대신 명시적 for 루프 사용)
    public List<SettlementResponse> entityToResponseList(List<Settlement> settlements) {
        if (settlements == null || settlements.isEmpty()) {
            return new ArrayList<>();
        }

        List<SettlementResponse> responseList = new ArrayList<>();
        for (Settlement settlement : settlements) {
            responseList.add(entityToResponse(settlement));
        }

        return responseList;
    }

    // 정산 대기(READY) 상태의 엔티티 생성
    public Settlement responseToEntity(Long showSq, Long companySq, long totalAmount, long feeAmount, long settlementAmount) {
        return Settlement.builder()
                .showSq(showSq)
                .companySq(companySq)
                .totalAmount(totalAmount)
                .feeAmount(feeAmount)
                .settlementAmount(settlementAmount)
                .settlementStatus(io.why503.paymentservice.domain.settlement.model.enums.SettlementStatus.READY)
                .build();
    }
}