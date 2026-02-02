/**
 * Concert Hall Controller
 * 공연장 등록 및 조회 요청을 처리하는 API 컨트롤러
 *
 * 사용 목적 :
 * - 관리자 또는 내부 시스템에서 공연장 정보 등록
 * - 공연장 식별자 기준 단건 조회
 *
 * 설계 메모 :
 * - 공연장 도메인은 다른 도메인(Show, Seat)의 기준 데이터 역할
 * - FK 참조 대상이므로 삭제/수정 시 주의 필요
 */
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
@RequiredArgsConstructor // Service 의존성 생성자 주입
@RequestMapping("/hall")
public class HallController {

    private final HallService hallService;
    /**
     * 공연장 등록
     * 처리 내용 :
     * - 공연장 기본 정보 DB 저장
     * - 공연 등록(Show) 시 참조되는 기준 데이터 생성
     * @param request 공연장 등록 요청 DTO
     * @return 200 OK
     */
    @PostMapping
    public ResponseEntity<Void> createHall(
            @RequestHeader("X-USER-SQ") Long userSq,
            @Valid @RequestBody HallRequest request
    ) {
        hallService.createHall(userSq, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 공연장 단건 조회
     * 처리 내용 :
     * - 공연장 식별자 기준 조회
     * - 존재하지 않을 경우 예외 발생
     * @param hallSq 공연장 식별자
     * @return 공연장 응답 DTO
     */
    @GetMapping("/{hallSq}")
    public ResponseEntity<HallResponse> getConcertHall(
            @PathVariable Long hallSq
    ) {
        HallResponse response = hallService.getHall(hallSq);
        return ResponseEntity.ok(response);
    }

    /**
     * 관리자 입력 기반 좌석 생성 공연장 등록
     * POST /concert-halls/custom-seats
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
    public Long createHallWithCustomSeats(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody HallWithSeatsRequest request
    ) {
        return hallService.createWithCustomSeats(
                userSq,
                request.hall(),
                request.seatAreas()
        );
    }
}
