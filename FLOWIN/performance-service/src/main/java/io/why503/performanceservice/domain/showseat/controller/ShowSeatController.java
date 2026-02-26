package io.why503.performanceservice.domain.showseat.controller;

import io.why503.performanceservice.domain.showseat.model.dto.response.ShowSeatBulkPriceChangeResponse;
import io.why503.performanceservice.domain.showseat.model.dto.response.ShowSeatResponse;
import io.why503.performanceservice.util.mapper.ShowSeatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import io.why503.performanceservice.domain.showseat.model.dto.request.ShowSeatGradeChangeRequest;
import io.why503.performanceservice.domain.showseat.model.dto.request.ShowSeatPriceChangeRequest;
import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;
import io.why503.performanceservice.domain.showseat.service.ShowSeatService;
import io.why503.performanceservice.global.validator.UserValidator;

@RestController
@RequiredArgsConstructor
@RequestMapping("/show-seats")
public class ShowSeatController {

    private final ShowSeatService showSeatService;
    private final ShowSeatMapper showSeatMapper;
    private final UserValidator userValidator;
    /**
     * 공연별 좌석 등급/가격 조회
     */
    @GetMapping("/shows/{showSq}")
    // [변경] 반환 타입: Entity -> Response DTO
    public ResponseEntity<List<ShowSeatResponse>> getByShow(
            @PathVariable Long showSq
    ) {
        // Service에서 Entity 리스트를 가져옴
        List<ShowSeatEntity> entities = showSeatService.getByShow(showSq);

        // [변경] Mapper를 통해 DTO로 변환하여 반환
        return ResponseEntity.ok(
                showSeatMapper.entityListToResponseList(entities)
        );
    }

    // 좌석 등급 변경
    @PatchMapping("/{showSeatSq}/grade")
    public ResponseEntity<Void> changeGrade(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long showSeatSq,
            @RequestBody ShowSeatGradeChangeRequest request
    ) {
        ShowSeatGrade grade = ShowSeatGrade.valueOf(request.grade());
        showSeatService.changeGrade(userSq, showSeatSq, grade);
        return ResponseEntity.ok().build();
    }

    // 좌석 가격 변경
    @PatchMapping("/{showSeatSq}/price")
    public ResponseEntity<Void> changePrice(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable Long showSeatSq,
            @RequestBody ShowSeatPriceChangeRequest request
    ) {
        showSeatService.changePrice(userSq, showSeatSq, request.price());
        return ResponseEntity.ok().build();
    }
    
    // 등급별 좌석 가격 일괄 변경
    @PatchMapping("/shows/{showSq}/grades/{grade}/price")
    public ResponseEntity<ShowSeatBulkPriceChangeResponse> changePriceByGrade(
            @RequestHeader("X-USER-SQ") Long userSq,
            @PathVariable("showSq") Long showSq,
            @PathVariable("grade") String grade,
            @RequestBody ShowSeatPriceChangeRequest request
    ) {
        ShowSeatGrade gradeEnum = ShowSeatGrade.valueOf(grade);
        int updatedCount = showSeatService.changePriceByShowAndGrade(userSq, showSq, gradeEnum, request.price());
        return ResponseEntity.ok(new ShowSeatBulkPriceChangeResponse(showSq, gradeEnum.name(), request.price(), updatedCount));
    }
}
