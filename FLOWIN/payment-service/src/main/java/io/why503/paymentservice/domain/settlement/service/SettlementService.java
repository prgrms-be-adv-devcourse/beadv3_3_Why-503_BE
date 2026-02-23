package io.why503.paymentservice.domain.settlement.service;

import io.why503.paymentservice.domain.settlement.model.dto.response.SettlementResponse;

import java.util.List;

/**
 * 공연 수익 정산 프로세스의 핵심 비즈니스 로직을 정의하는 서비스
 */
public interface SettlementService {

    // 기획사별 누적 정산 내역 확인
    List<SettlementResponse> getSettlementsByCompanySq(Long companySq);

    // 개별 공연 단위의 정산 결과 조회
    List<SettlementResponse> getSettlementsByShowSq(Long showSq);

    // 공연 종료에 따른 정산 데이터 기초 자료 생성
    void createSettlement(Long showSq, Long companySq);

    // 대기 중인 정산 건들에 대한 실제 지급 처리 및 상태 업데이트
    void processPendingSettlements();
}