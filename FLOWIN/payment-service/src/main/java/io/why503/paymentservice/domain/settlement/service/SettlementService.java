package io.why503.paymentservice.domain.settlement.service;

import io.why503.paymentservice.domain.settlement.model.dto.response.SettlementResponse;

import java.util.List;

public interface SettlementService {

    /**
     * 특정 기획사의 모든 정산 내역을 조회합니다.
     * * @param companySq 기획사 식별자
     * @return 정산 내역 응답 DTO 리스트
     */
    List<SettlementResponse> getSettlementsByCompanySq(Long companySq);

    /**
     * 특정 공연의 정산 내역을 조회합니다.
     * * @param showSq 공연 식별자
     * @return 정산 내역 응답 DTO 리스트
     */
    List<SettlementResponse> getSettlementsByShowSq(Long showSq);

    /**
     * 특정 공연이 종료된 후, 해당 공연의 판매 수익(티켓)을 집계하여 정산 대기(READY) 데이터를 생성합니다.
     * * @param showSq 공연 식별자
     * @param companySq 기획사 식별자
     */
    void createSettlement(Long showSq, Long companySq);

    /**
     * 정산 대기(READY) 상태인 정산 건들을 찾아 실제 기획사로 송금 처리를 진행하고 정산 완료(COMPLETED) 상태로 변경합니다.
     * 주로 배치 스케줄러에 의해 주기적으로 실행됩니다.
     */
    void processPendingSettlements();
}