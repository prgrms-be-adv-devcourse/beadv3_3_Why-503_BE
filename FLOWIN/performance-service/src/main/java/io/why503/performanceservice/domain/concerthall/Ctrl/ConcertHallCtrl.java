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
package io.why503.performanceservice.domain.concerthall.Ctrl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallReqDto;
import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallResDto;
import io.why503.performanceservice.domain.concerthall.Sv.ConcertHallSv;

@RestController
@RequiredArgsConstructor // Service 의존성 생성자 주입
@RequestMapping("/concert-halls")
public class ConcertHallCtrl {

    private final ConcertHallSv concertHallSv;

    /**
     * 공연장 등록
     *
     * 처리 내용 :
     * - 공연장 기본 정보 DB 저장
     * - 공연 등록(Show) 시 참조되는 기준 데이터 생성
     *
     * @param reqDto 공연장 등록 요청 DTO
     * @return 200 OK
     */
    @PostMapping
    public ResponseEntity<Void> createConcertHall(
            @RequestBody ConcertHallReqDto reqDto
    ) {
        concertHallSv.createConcertHall(reqDto);
        return ResponseEntity.ok().build();
    }

    /**
     * 공연장 단건 조회
     *
     * 처리 내용 :
     * - 공연장 식별자 기준 조회
     * - 존재하지 않을 경우 예외 발생
     *
     * @param concertHallSq 공연장 식별자
     * @return 공연장 응답 DTO
     */
    @GetMapping("/{concertHallSq}")
    public ResponseEntity<ConcertHallResDto> getConcertHall(
            @PathVariable Long concertHallSq
    ) {
        ConcertHallResDto res = concertHallSv.getConcertHall(concertHallSq);
        return ResponseEntity.ok(res);
    }
}
