package io.why503.paymentservice.domain.point.controller;

import io.why503.paymentservice.domain.point.model.dto.request.PointRequest;
import io.why503.paymentservice.domain.point.model.dto.response.PointResponse;
import io.why503.paymentservice.domain.point.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    /**
     * 포인트 충전 요청
     * - PG 결제 전, 충전 대기 상태의 데이터를 생성합니다.
     */
    @PostMapping
    public ResponseEntity<PointResponse> createPoint(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid PointRequest request) {

        // 해피 패스 금지: 헤더 값 검증
        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }

        PointResponse response = pointService.createPointCharge(userSq, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 포인트 충전 요청 상세 조회
     * - 본인의 내역만 조회 가능
     */
    @GetMapping("/{pointSq}")
    public ResponseEntity<PointResponse> findPoint(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("pointSq") Long pointSq) {

        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }

        PointResponse response = pointService.findPoint(userSq, pointSq);
        return ResponseEntity.ok(response);
    }

    /**
     * 내 포인트 충전 이력 조회
     * - 최신순 정렬
     */
    @GetMapping
    public ResponseEntity<List<PointResponse>> findPoints(
            @RequestHeader("X-USER-SQ") Long userSq) {

        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }

        List<PointResponse> responses = pointService.findPointsByUser(userSq);
        return ResponseEntity.ok(responses);
    }

    /**
     * 포인트 충전 취소
     * - 결제 대기(READY) 상태일 때만 취소 가능
     * - 상태 변경이므로 POST 사용
     */
    @PostMapping("/{pointSq}/cancel")
    public ResponseEntity<PointResponse> cancelPoint(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("pointSq") Long pointSq) {

        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }

        PointResponse response = pointService.cancelPoint(userSq, pointSq);
        return ResponseEntity.ok(response);
    }
}