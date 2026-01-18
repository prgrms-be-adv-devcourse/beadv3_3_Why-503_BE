package io.why503.performanceservice.domain.roundSeats.controller;


import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatRequestDto;
import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatResponseDto;
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
    public ResponseEntity<RoundSeatResponseDto> createRoundSeat(
                @Valid @RequestBody RoundSeatRequestDto request

    ){
        RoundSeatResponseDto response = roundSeatService.createRoundSeat(request);
        return ResponseEntity.ok(response);
    }


    //전체 조회
    @GetMapping
    public ResponseEntity<List<RoundSeatResponseDto>> getRoundSeatList(
            @RequestParam(name = "roundSq") Long roundSq
    ) {
        List<RoundSeatResponseDto> response = roundSeatService.getRoundSeatList(roundSq);
        return ResponseEntity.ok(response);
    }

    //예매 가능 좌석 조회
    @GetMapping("/available")
    public ResponseEntity<List<RoundSeatResponseDto>> getAvailableRoundSeatList(
            @RequestParam(name = "roundSq") Long roundSq
    ){
        List<RoundSeatResponseDto> response = roundSeatService.getAvailableRoundSeatList(roundSq);
        return ResponseEntity.ok(response);
    }

    //상태변경
    @PatchMapping("/{roundSeatSq}/status")
    public ResponseEntity<RoundSeatResponseDto> patchRoundSeatStatus(
            @PathVariable(name = "roundSeatSq") Long roundSeatSq,
            @RequestBody RoundSeatRequestDto request
    ){
        RoundSeatResponseDto response = roundSeatService.patchRoundSeatStatus(roundSeatSq, request.roundSeatStatus());
        return ResponseEntity.ok(response);
    }




}
