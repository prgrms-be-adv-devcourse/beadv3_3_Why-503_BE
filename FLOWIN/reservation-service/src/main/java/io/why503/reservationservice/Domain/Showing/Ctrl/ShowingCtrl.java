package io.why503.reservationservice.Domain.Showing.Ctrl;

import io.why503.reservationservice.Domain.Showing.Model.Dto.AreaStatusDto;
import io.why503.reservationservice.Domain.Showing.Model.Dto.SeatStatusDto;
import io.why503.reservationservice.Domain.Showing.Sv.ShowingSv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/showings")
@RequiredArgsConstructor
public class ShowingCtrl {

    private final ShowingSv showingSv;

    /**
     * 특정 회차의 구역별 좌석 현황 조회
     * GET /showings/1/areas
     */
    @GetMapping("/{showingSq}/areas")
    public ResponseEntity<List<AreaStatusDto>> getAreaStatus(@PathVariable Long showingSq) {
        return ResponseEntity.ok(showingSv.getAreaStatus(showingSq));
    }

    @GetMapping("/{showingSq}/seats")
    public ResponseEntity<List<SeatStatusDto>> getAllSeats(@PathVariable Long showingSq) {
        return ResponseEntity.ok(showingSv.getAllSeatStatus(showingSq));
    }

    /**
     * 특정 회차의 특정 구역 좌석 상세 조회
     * GET /showings/1/areas/A/seats
     */
    @GetMapping("/{showingSq}/areas/{area}/seats")
    public ResponseEntity<List<SeatStatusDto>> getSeatsByArea(
            @PathVariable Long showingSq,
            @PathVariable String area
    ) {
        return ResponseEntity.ok(showingSv.getSeatStatusByArea(showingSq, area));
    }
}