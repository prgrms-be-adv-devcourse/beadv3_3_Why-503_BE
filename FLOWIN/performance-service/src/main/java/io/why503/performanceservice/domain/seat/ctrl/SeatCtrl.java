package io.why503.performanceservice.domain.seat.ctrl;


import io.why503.performanceservice.domain.seat.model.dto.SeatFindReq;
import io.why503.performanceservice.domain.seat.model.dto.SeatFindRes;
import io.why503.performanceservice.domain.seat.sv.SeatSv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seats")
public class SeatCtrl {

    private final SeatSv seatSv;

    @GetMapping
    public ResponseEntity<List<SeatFindRes>> findSeats(
            @RequestParam Long concertHallId,
            @RequestParam(required = false) String seatArea,
            @RequestParam(required = false) Long seatNo
    ) {
        SeatFindReq req = new SeatFindReq(
                concertHallId,
                seatArea,
                seatNo
        );

        return ResponseEntity.ok(seatSv.findSeats(req));
    }

}
