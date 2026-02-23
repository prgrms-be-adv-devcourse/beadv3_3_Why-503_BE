package io.why503.paymentservice.domain.settlement.controller;

import io.why503.paymentservice.domain.settlement.model.dto.response.SettlementResponse;
import io.why503.paymentservice.domain.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    // 1. 특정 기획사의 모든 정산 내역 조회 (기획사 마이페이지용)
    @GetMapping("/company/{companySq}")
    public ResponseEntity<List<SettlementResponse>> getSettlementsByCompanySq(
            @PathVariable(name = "companySq") Long companySq) {

        List<SettlementResponse> response = settlementService.getSettlementsByCompanySq(companySq);
        return ResponseEntity.ok(response);
    }

    // 2. 특정 공연의 정산 내역 조회 (공연별 정산 상태 확인용)
    @GetMapping("/show/{showSq}")
    public ResponseEntity<List<SettlementResponse>> getSettlementsByShowSq(
            @PathVariable(name = "showSq") Long showSq) {

        List<SettlementResponse> response = settlementService.getSettlementsByShowSq(showSq);
        return ResponseEntity.ok(response);
    }

    // 3. 특정 공연 종료 후 수동으로 정산 대기 데이터 생성 (관리자용)
    @PostMapping("/show/{showSq}/company/{companySq}")
    public ResponseEntity<String> createSettlement(
            @PathVariable(name = "showSq") Long showSq,
            @PathVariable(name = "companySq") Long companySq) {

        settlementService.createSettlement(showSq, companySq);
        return ResponseEntity.ok("정산 대기 데이터가 성공적으로 생성되었습니다.");
    }

    // 4. 정산 대기 건 일괄 송금 처리 수동 트리거 (관리자용)
    @PostMapping("/process-pending")
    public ResponseEntity<String> processPendingSettlements() {

        settlementService.processPendingSettlements();
        return ResponseEntity.ok("대기 중인 정산 건에 대한 송금 처리가 완료되었습니다.");
    }
}