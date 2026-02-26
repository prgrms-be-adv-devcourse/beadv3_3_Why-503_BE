package io.why503.performanceservice.domain.show.controller;

import io.why503.performanceservice.domain.show.model.dto.request.ShowCreateWithSeatPolicyRequest;
import io.why503.performanceservice.domain.show.model.dto.request.ShowRequest;
import io.why503.performanceservice.domain.show.model.dto.response.ShowResponse;
import io.why503.performanceservice.domain.show.model.enums.ShowCategory;
import io.why503.performanceservice.domain.show.model.enums.ShowGenre;
import io.why503.performanceservice.domain.show.service.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shows")
public class ShowController {

    private final ShowService showService;

    // 공연 등록
    @PostMapping
    public ResponseEntity<ShowResponse> createShow(
            @RequestHeader("X-USER-SQ") Long userSq,
            @RequestBody @Valid ShowRequest request
    ) {
        ShowResponse response = showService.createShow(request, userSq);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 공연 모두 조회
    @GetMapping
    public ResponseEntity<List<ShowResponse>> getShowAll() {
        List<ShowResponse> response = showService.readShowAll();
        return ResponseEntity.ok(response);
    }

    // 공연 + 좌석 정책 동시 생성
    @PostMapping("/with-seats")
    public ResponseEntity<Long> createShowWithSeats(
            @RequestBody @Valid ShowCreateWithSeatPolicyRequest request,
            @RequestHeader("X-USER-SQ") Long userSq
    ) {
        Long showSq = showService.createShowWithSeats(request, userSq);
        return ResponseEntity.status(HttpStatus.CREATED).body(showSq);
    }

    // 공연 단건 조회
    @GetMapping("/{showSq}")
    public ResponseEntity<ShowResponse> getShow(
            @PathVariable("showSq") Long showSq
    ) {
        ShowResponse response = showService.readShowBySq(showSq);
        return ResponseEntity.ok(response);
    }

    // 카테고리별 조회
    // shows/category?category=CONCERT
    @GetMapping("/category")
    public ResponseEntity<List<ShowResponse>> getShowsByCategory(
            @RequestParam("category") ShowCategory category
    ) {
        return ResponseEntity.ok(showService.findShowsByCategory(category));
    }

    // 카테고리 + 장르 조회
    // shows/search?category=MUSICAL&genre=THRILLER
    @GetMapping("/search")
    public ResponseEntity<List<ShowResponse>> getShowsByCategoryAndGenre(
            @RequestParam("category") ShowCategory category,
            @RequestParam("genre") ShowGenre genre
    ) {
        return ResponseEntity.ok(showService.findShowsByCategoryAndGenre(category, genre));
    }

}
