package io.why503.performanceservice.domain.roundSeats.controller;


import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatRequest;
import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatResponse;
import io.why503.performanceservice.domain.roundSeats.service.RoundSeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/round-seats")
public class RoundSeatController {

    private final RoundSeatService roundSeatService;

    //회차 좌석 생성
    @PostMapping
    public ResponseEntity<RoundSeatResponse> createRoundSeat(
                @Valid @RequestBody RoundSeatRequest request

    ){
        RoundSeatResponse response = roundSeatService.createRoundSeat(request);
        return ResponseEntity.ok(response);
    }


    //전체 조회
    @GetMapping
    public ResponseEntity<List<RoundSeatResponse>> getRoundSeatList(
            @RequestParam(name = "roundSq") Long roundSq
    ) {
        List<RoundSeatResponse> response = roundSeatService.getRoundSeatList(roundSq);
        return ResponseEntity.ok(response);
    }

    //예매 가능 좌석 조회
    @GetMapping("/available")
    public ResponseEntity<List<RoundSeatResponse>> getAvailableRoundSeatList(
            @RequestParam(name = "roundSq") Long roundSq
    ){
        List<RoundSeatResponse> response = roundSeatService.getAvailableRoundSeatList(roundSq);
        return ResponseEntity.ok(response);
    }

    //상태변경
    @PatchMapping("/{roundSeatSq}/status")
    public ResponseEntity<RoundSeatResponse> patchRoundSeatStatus(
            @PathVariable(name = "roundSeatSq") Long roundSeatSq,
            @RequestBody RoundSeatRequest request
    ){
        RoundSeatResponse response = roundSeatService.patchRoundSeatStatus(roundSeatSq, request.roundSeatStatus());
        return ResponseEntity.ok(response);
    }




}
