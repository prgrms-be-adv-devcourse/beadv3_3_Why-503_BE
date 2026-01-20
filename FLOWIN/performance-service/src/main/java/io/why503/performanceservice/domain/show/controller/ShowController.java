package io.why503.performanceservice.domain.show.controller;

import io.why503.performanceservice.domain.show.model.dto.ShowCreateWithSeatPolicyRequest;
import io.why503.performanceservice.domain.show.model.dto.ShowRequest;
import io.why503.performanceservice.domain.show.model.dto.ShowResponse;
import io.why503.performanceservice.domain.show.service.ShowService;
import io.why503.performanceservice.global.error.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Show Controller
 *
 * 역할:
 * - 공연 등록
 * - 공연 + 좌석 정책 등록
 * - 공연 단건 조회
 *
 * 책임:
 * - 요청/응답 매핑
 * - Authorization 헤더 존재 여부 검증
 *
 * 비즈니스 로직은 Service 계층에 위임
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shows")
public class ShowController {

    private final ShowService showService;

    /**
     * 공연 단독 생성
     * (COMPANY 권한 필수)
     */
    @PostMapping
    public ResponseEntity<ShowResponse> createShow(
            @RequestBody ShowRequest req,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        requireAuthorization(authorization);
        ShowResponse res = showService.createShow(req, authorization);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    /**
     * 공연 + 좌석 정책 동시 생성
     * (COMPANY 권한 필수)
     */
    @PostMapping("/with-seats")
    public ResponseEntity<Long> createShowWithSeats(
            @RequestBody ShowCreateWithSeatPolicyRequest req,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        requireAuthorization(authorization);
        Long showSq = showService.createShowWithSeats(req, authorization);
        return ResponseEntity.status(HttpStatus.CREATED).body(showSq);
    }

    /**
     * 공연 단건 조회
     */
    @GetMapping("/{showSq}")
    public ResponseEntity<ShowResponse> getShow(
            @PathVariable Long showSq
    ) {
        ShowResponse res = showService.getShow(showSq);
        return ResponseEntity.ok(res);
    }

    /**
     * Authorization 헤더 필수 검증
     */
    private void requireAuthorization(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new UnauthorizedException("missing Authorization header");
        }
    }
}
