package io.why503.performanceservice.domain.showing.ctrl;


import io.why503.performanceservice.domain.showing.model.dto.ShowingReqDto;
import io.why503.performanceservice.domain.showing.model.dto.ShowingResDto;
import io.why503.performanceservice.domain.showing.sv.ShowingSv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/showing")
public class ShowingCtrl {

    private final ShowingSv showingSv;

    //회차 생성
    @PostMapping
    public ResponseEntity<ShowingResDto> createShowing(@RequestBody ShowingReqDto req){
        ShowingResDto res = showingSv.createShowing(req);
        return ResponseEntity.ok(res);
    }

    //특정 공연의 모든 회차 조회(관리자, 기업회원)
    @GetMapping
    public ResponseEntity<List<ShowingResDto>> getShowingListByShow(
            @RequestParam(name = "showSq") Long showSq
    ){
        List<ShowingResDto> res = showingSv.getShowingListByShow(showSq);
        return ResponseEntity.ok(res);
    }

    //예매 가능한 회차 조회
    @GetMapping("/available")
    public ResponseEntity<List<ShowingResDto>> getAvailableShowingList(
            @RequestParam(name = "showSq") Long showSq
    ) {
        List<ShowingResDto> res = showingSv.getAvailableShowingList(showSq);
        return ResponseEntity.ok(res);
    }

    //회차 단건 상세 조회
    @GetMapping("/{showingSq}")
    public ResponseEntity<ShowingResDto> getShowingDetail(
            @PathVariable(name = "showingSq") Long showingSq
    ) {
        ShowingResDto res = showingSv.getShowingDetail(showingSq);
        return ResponseEntity.ok(res);
    }

    // 회차 상태 변경
    @PatchMapping("/{showingSq}/status")
    public ResponseEntity<ShowingResDto> patchShowingStat(
            @PathVariable(name = "showingSq") Long showingSq,
            @RequestBody ShowingReqDto req // 상태값만 꺼내서 씀
    ) {
        // req.getStat()에 변경할 상태가 들어옴
        ShowingResDto res = showingSv.patchShowingStat(showingSq, req.getStat());
        return ResponseEntity.ok(res);
    }

}
