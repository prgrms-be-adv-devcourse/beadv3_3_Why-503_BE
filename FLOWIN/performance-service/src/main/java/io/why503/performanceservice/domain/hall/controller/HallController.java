package io.why503.performanceservice.domain.hall.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.why503.performanceservice.domain.hall.model.dto.request.HallRequest;
import io.why503.performanceservice.domain.hall.model.dto.response.HallResponse;
import io.why503.performanceservice.domain.hall.model.dto.request.HallWithSeatsRequest;
import io.why503.performanceservice.domain.hall.service.HallService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/hall")
public class HallController {

    private final HallService hallService;

    // 공연장 등록
    @PostMapping
    public ResponseEntity<Void> createHall(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid HallRequest request
    ) {
        hallService.createHall(userSq, request);
        return ResponseEntity.ok().build();
    }

    // 공연장 조회
    @GetMapping("/{hallSq}")
    public ResponseEntity<HallResponse> getHall(
            @PathVariable("hallSq") Long hallSq
    ) {
        HallResponse response = hallService.getHall(hallSq);
        return ResponseEntity.ok(response);
    }

    // 공연장 등록 + 좌석 생성
    @PostMapping("/custom-seats")
    public Long createHallWithCustomSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid HallWithSeatsRequest request
    ) {
        return hallService.createWithCustomSeats(
                userSq,
                request.hall(),
                request.seatAreas()
        );
    }
}
