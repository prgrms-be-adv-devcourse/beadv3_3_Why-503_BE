/**
 * Concert Hall Controller
 * 공연장 등록 및 조회를 담당하는 Controller
 *
 * 사용 목적 :
 * - 공연장 등록
 * - 공연장 단건 조회
 * - 공연장 등록 시 좌석 자동 생성 분기 처리
 *
 * 설계 의도 :
 * - 좌석 생성 로직은 Service에 위임
 * - Controller에서는 요청 타입에 따라 Service 메서드만 분기
 */
package io.why503.performanceservice.domain.concerthall.Ctrl;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallReqDto;
import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallResDto;
import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallWithSeatsReq;
import io.why503.performanceservice.domain.concerthall.Sv.ConcertHallSv;
import io.why503.performanceservice.domain.seat.Model.Dto.Cmd.SeatAreaCreateCmd;

@RestController
@RequestMapping("/concert-halls")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ConcertHallCtrl {

    private final ConcertHallSv concertHallSv;

    /**
     * 공연장 등록 (좌석 생성 없음 - 기존 방식)
     *
     * POST /concert-halls
     */
    @PostMapping
    public void createConcertHall(
            @RequestBody ConcertHallReqDto reqDto
    ) {
        concertHallSv.createConcertHall(reqDto);
    }

    /**
     * 공연장 단건 조회
     *
     * GET /concert-halls/{concertHallSq}
     */
    @GetMapping("/{concertHallSq}")
    public ConcertHallResDto getConcertHall(
            @PathVariable Long concertHallSq
    ) {
        return concertHallSv.getConcertHall(concertHallSq);
    }

    /**
     * 관리자 입력 기반 좌석 생성 공연장 등록
     *
     * POST /concert-halls/custom-seats
     *
     * 요청 바디 예시:
     * {
     *   "concertHall": { ... },
     *   "seatAreas": [
     *     { "seatArea": "A", "seatCount": 20 },
     *     { "seatArea": "B", "seatCount": 40 }
     *   ]
     * }
     */
    @PostMapping("/custom-seats")
    public Long createConcertHallWithCustomSeats(
            @RequestBody ConcertHallWithSeatsReq req
    ) {
        return concertHallSv.createWithCustomSeats(
                req.getConcertHall(),
                req.getSeatAreas()
        );
    }
}
