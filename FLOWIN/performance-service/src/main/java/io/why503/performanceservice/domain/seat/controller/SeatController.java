/*
 * - 특정 공연장에 소속된 좌석 목록 조회
 * - Seat 도메인은 공연장 기준 고정 자원이므로
 *   공연장 식별자를 기준으로 좌석을 조회
 */
package io.why503.performanceservice.domain.seat.controller;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.why503.performanceservice.domain.seat.model.dto.response.SeatResponse;
import io.why503.performanceservice.domain.seat.service.SeatService;

@RestController
@RequestMapping("/hall")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class SeatController {

    private final SeatService seatService;

    // 공연장 기준 좌석 목록 조회
    @GetMapping("/{hallSq}/seats")
    public List<SeatResponse> getSeatsByHall(
            @PathVariable("hallSq") Long hallSq
    ) {
        return seatService.readByHall(hallSq);
    }
}
