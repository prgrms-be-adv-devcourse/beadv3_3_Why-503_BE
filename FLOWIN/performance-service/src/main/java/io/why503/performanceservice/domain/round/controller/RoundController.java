package io.why503.performanceservice.domain.round.controller;


import io.why503.performanceservice.domain.round.model.dto.request.RoundRequest;
import io.why503.performanceservice.domain.round.model.dto.response.RoundResponse;
import io.why503.performanceservice.domain.round.model.enums.RoundStatus;
import io.why503.performanceservice.domain.round.service.RoundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/round")
public class RoundController {

    private final RoundService roundService;

    // 회차 생성 (관리자만)
    @PostMapping
    public ResponseEntity<RoundResponse> createRound(
            @RequestHeader("X-USER-SQ") Long userSq,
            @Valid @RequestBody RoundRequest request) {
        return ResponseEntity.ok(roundService.createRound(userSq, request));
    }

    // 특정 공연의 모든 회차 조회 (관리자만)
    @GetMapping("/all/{showSq}")
    public ResponseEntity<List<RoundResponse>> getAllRounds(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long showSq) {
        return ResponseEntity.ok(roundService.getRoundListByShow(userSq, showSq));
    }

    // 일반 유저용 예매 가능 회차 조회
    @GetMapping("/available/{showSq}")
    public ResponseEntity<List<RoundResponse>> getAvailableRounds(@PathVariable Long showSq) {
        return ResponseEntity.ok(roundService.getAvailableRoundList(showSq));
    }

    //회차 단건 상세 조회
    @GetMapping("/{roundSq}")
    public ResponseEntity<RoundResponse> getRoundDetail(
            @PathVariable(name = "roundSq") Long roundSq
    ) {
        RoundResponse response = roundService.getRoundDetail(roundSq);
        return ResponseEntity.ok(response);
    }

    // 회차 상태 변경
    @PatchMapping("/{roundSq}/status")
    public ResponseEntity<RoundResponse> patchRoundStat(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable(name = "roundSq") Long roundSq,
            @RequestBody RoundRequest request // 상태값만 꺼내서 씀
    ) {
        RoundStatus status = request.roundStatus();
        // req.getRoundStatus()에 변경할 상태가 들어옴
        RoundResponse response = roundService.patchRoundStat(userSq, roundSq, status);
        return ResponseEntity.ok(response);
    }

}