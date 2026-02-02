/**
 * Seat Controller
 * 공연장 기준 좌석 조회를 담당하는 Controller
 * 사용 목적 :
 * - 특정 공연장에 소속된 좌석 목록 조회
 * 설계 의도 :
 * - Seat 도메인은 공연장 기준 고정 자원이므로
 *   공연장 식별자를 기준으로 좌석을 조회한다.
 * - 좌석의 상태/가격/판매 여부는 다루지 않는다. (show_seat 책임)
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

    /**
     * 공연장 기준 좌석 목록 조회
     * 요청 예시 :
     * GET /concert-halls/{concertHallSq}/seats
     * 처리 흐름 :
     * 1. 공연장 식별자 기준 Seat 조회
     * 2. Seat Entity → Response DTO 변환
     * @param concertHallSq 공연장 식별자
     * @return 좌석 목록
     */
    @GetMapping("/{concertHallSq}/seats")
    public List<SeatResponse> getSeatsByConcertHall(
            @PathVariable Long concertHallSq
    ) {
        return seatService.readByHall(concertHallSq);
    }
}
