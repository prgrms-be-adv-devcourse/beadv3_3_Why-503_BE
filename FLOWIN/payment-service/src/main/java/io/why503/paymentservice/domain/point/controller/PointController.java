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

/**
 * 포인트 충전 요청 및 이력 조회를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    // 포인트 충전 데이터 생성 요청 처리
    @PostMapping
    public ResponseEntity<PointResponse> createPoint(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid PointRequest request) {

        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }

        PointResponse response = pointService.createPointCharge(userSq, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 특정 충전 요청 상세 조회
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

    // 사용자의 전체 충전 이력 조회
    @GetMapping
    public ResponseEntity<List<PointResponse>> findPoints(
            @RequestHeader("X-USER-SQ") Long userSq) {

        if (userSq == null || userSq <= 0) {
            throw new IllegalArgumentException("유효하지 않은 사용자 헤더(X-USER-SQ)입니다.");
        }

        List<PointResponse> responses = pointService.findPointsByUser(userSq);
        return ResponseEntity.ok(responses);
    }

    // 결제 전 단계의 충전 요청 취소
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