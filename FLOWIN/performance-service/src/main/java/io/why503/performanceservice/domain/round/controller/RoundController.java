package io.why503.performanceservice.domain.round.controller;


import io.why503.performanceservice.domain.round.model.dto.RoundRequest;
import io.why503.performanceservice.domain.round.model.dto.RoundResponse;
import io.why503.performanceservice.domain.round.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/round")
public class RoundController {

    private final RoundService roundService;

    //회차 생성
    @PostMapping
    public ResponseEntity<RoundResponse> createRound(@RequestBody RoundRequest request){
        RoundResponse response = roundService.createRound(request);
        return ResponseEntity.ok(response);
    }

    //특정 공연의 모든 회차 조회(관리자, 기업회원)
    @GetMapping
    public ResponseEntity<List<RoundResponse>> getRoundListByShow(
            @RequestParam(name = "showSq") Long showSq
    ){
        List<RoundResponse> response = roundService.getRoundListByShow(showSq);
        return ResponseEntity.ok(response);
    }

    //예매 가능한 회차 조회
    @GetMapping("/available")
    public ResponseEntity<List<RoundResponse>> getAvailableRoundList(
            @RequestParam(name = "showSq") Long showSq
    ) {
        List<RoundResponse> response = roundService.getAvailableRoundList(showSq);
        return ResponseEntity.ok(response);
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
            @PathVariable(name = "roundSq") Long roundSq,
            @RequestBody RoundRequest request // 상태값만 꺼내서 씀
    ) {
        // req.getRoundStatus()에 변경할 상태가 들어옴
        RoundResponse response = roundService.patchRoundStat(roundSq, request.getRoundStatus());
        return ResponseEntity.ok(response);
    }

}