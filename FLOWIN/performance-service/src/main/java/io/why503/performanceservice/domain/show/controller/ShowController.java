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
 * 공연(Show) 관련 API Controller
 *
 * 역할:
 * - 공연 등록
 * - 공연 단건 조회
 *
 * 특징:
 * - 비즈니스 로직은 Service 계층에 위임
 * - Controller는 요청/응답 매핑만 담당
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/shows")
public class ShowController {

    /**
     * 공연 도메인 서비스
     * - 공연 생성 및 조회 로직 처리
     */
    private final ShowService showService;

    @PostMapping
    public ResponseEntity<ShowResponse> createShow(
            @RequestBody ShowRequest req,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        requireAuthorization(authorization);
        ShowResponse res = showService.createShow(req, authorization);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/with-seats")
    public ResponseEntity<Long> createShowWithSeats(
            @RequestBody ShowCreateWithSeatPolicyRequest req,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        requireAuthorization(authorization);
        Long showSq = showService.createShowWithSeats(req, authorization);
        return ResponseEntity.status(HttpStatus.CREATED).body(showSq);
    }

    @GetMapping("/{showSq}")
    public ResponseEntity<ShowResponse> getShow(@PathVariable Long showSq) {
        ShowResponse res = showService.getShow(showSq);
        return ResponseEntity.ok(res);
    }

    private void requireAuthorization(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new UnauthorizedException("missing Authorization header");
        }
    }
}
