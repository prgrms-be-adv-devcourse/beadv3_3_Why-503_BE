package io.why503.performanceservice.domain.show.Ctrl;

import io.why503.performanceservice.domain.show.Model.Dto.ShowReqDto;
import io.why503.performanceservice.domain.show.Model.Dto.ShowResDto;
import io.why503.performanceservice.domain.show.Sv.ShowSv;
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
public class ShowCtrl {

    /**
     * 공연 도메인 서비스
     * - 공연 생성 및 조회 로직 처리
     */
    private final ShowSv showSv;

    /**
     * 공연 등록 API
     *
     * 요청:
     * - POST /shows
     * - RequestBody: ShowReqDto
     *
     * 처리 흐름:
     * 1. 클라이언트로부터 공연 등록 정보 수신
     * 2. Service(createShow) 호출
     * 3. 생성된 공연 정보를 Response DTO로 반환
     *
     * 응답:
     * - HTTP 201 Created
     * - ResponseBody: ShowResDto
     */
    @PostMapping
    public ResponseEntity<ShowResDto> createShow(
            @RequestBody ShowReqDto reqDto
    ) {
        ShowResDto res = showSv.createShow(reqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    /**
     * 공연 단건 조회 API
     *
     * 요청:
     * - GET /shows/{showSq}
     *
     * 처리 흐름:
     * 1. PathVariable로 공연 시퀀스(showSq) 수신
     * 2. Service(getShow) 호출
     * 3. 조회된 공연 정보를 Response DTO로 반환
     *
     * 응답:
     * - HTTP 200 OK
     * - ResponseBody: ShowResDto
     */
    @GetMapping("/{showSq}")
    public ResponseEntity<ShowResDto> getShow(
            @PathVariable Long showSq
    ) {
        ShowResDto res = showSv.getShow(showSq);
        return ResponseEntity.ok(res);
    }
}
