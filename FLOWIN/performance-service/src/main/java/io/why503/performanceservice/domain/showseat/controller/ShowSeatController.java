/**
 * ShowSeat Controller
 * 공연 좌석 정책(show_seat) 관리 API
 *
 * 역할:
 * - 공연별 좌석 정책 조회
 * - 좌석 등급 변경
 * - 좌석 가격 변경
 */
package io.why503.performanceservice.domain.showseat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.why503.performanceservice.domain.showseat.model.dto.ShowSeatGradeChangeReqDto;
import io.why503.performanceservice.domain.showseat.model.dto.ShowSeatPriceChangeReqDto;
import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;
import io.why503.performanceservice.domain.showseat.service.ShowSeatService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/show-seats")
public class ShowSeatController {

    private final ShowSeatService showSeatService;

    /**
     * 공연별 좌석 정책 조회
     */
    @GetMapping("/shows/{showSq}")
    public ResponseEntity<List<ShowSeatEntity>> getByShow(
            @PathVariable Long showSq
    ) {
        return ResponseEntity.ok(
                showSeatService.getByShow(showSq)
        );
    }

    /**
     * 좌석 등급 변경
     */
    @PatchMapping("/{showSeatSq}/grade")
    public ResponseEntity<Void> changeGrade(
            @PathVariable Long showSeatSq,
            @RequestBody ShowSeatGradeChangeReqDto req
    ) {
        ShowSeatGrade grade = ShowSeatGrade.valueOf(req.getGrade());
        showSeatService.changeGrade(showSeatSq, grade);
        return ResponseEntity.ok().build();
    }

    /**
     * 좌석 가격 변경
     */
    @PatchMapping("/{showSeatSq}/price")
    public ResponseEntity<Void> changePrice(
            @PathVariable Long showSeatSq,
            @RequestBody ShowSeatPriceChangeReqDto req
    ) {
        showSeatService.changePrice(showSeatSq, req.getPrice());
        return ResponseEntity.ok().build();
    }
}
