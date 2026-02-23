package io.why503.paymentservice.domain.settlement.controller;

import io.why503.paymentservice.domain.settlement.model.dto.response.SettlementResponse;
import io.why503.paymentservice.domain.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 정산 내역 조회 및 정산 실행 프로세스 제어를 위한 외부 인터페이스
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    // 기획사 기준의 전체 정산 통계 및 내역 제공
    @GetMapping("/company/{companySq}")
    public ResponseEntity<List<SettlementResponse>> getSettlementsByCompanySq(
            @PathVariable(name = "companySq") Long companySq) {

        List<SettlementResponse> response = settlementService.getSettlementsByCompanySq(companySq);
        return ResponseEntity.ok(response);
    }

    // 개별 공연의 정산 진행 현황 조회
    @GetMapping("/show/{showSq}")
    public ResponseEntity<List<SettlementResponse>> getSettlementsByShowSq(
            @PathVariable(name = "showSq") Long showSq) {

        List<SettlementResponse> response = settlementService.getSettlementsByShowSq(showSq);
        return ResponseEntity.ok(response);
    }

    // 공연 판매 종료에 따른 정산 기초 데이터 생성 요청
    @PostMapping("/show/{showSq}/company/{companySq}")
    public ResponseEntity<String> createSettlement(
            @PathVariable(name = "showSq") Long showSq,
            @PathVariable(name = "companySq") Long companySq) {

        settlementService.createSettlement(showSq, companySq);
        return ResponseEntity.ok("정산 대기 데이터가 성공적으로 생성되었습니다.");
    }

    // 미결제 정산 건들에 대한 실제 송금 및 상태 업데이트 트리거
    @PostMapping("/process-pending")
    public ResponseEntity<String> processPendingSettlements() {

        settlementService.processPendingSettlements();
        return ResponseEntity.ok("대기 중인 정산 건에 대한 송금 처리가 완료되었습니다.");
    }
}