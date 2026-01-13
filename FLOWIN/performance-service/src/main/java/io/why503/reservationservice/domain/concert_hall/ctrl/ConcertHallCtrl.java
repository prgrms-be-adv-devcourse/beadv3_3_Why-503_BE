package io.why503.performanceservice.domain.concert_hall.ctrl;

import io.why503.performanceservice.domain.concert_hall.model.dto.*;
import io.why503.performanceservice.domain.concert_hall.sv.ConcertHallSv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/performances")
public class ConcertHallCtrl {

    private final ConcertHallSv concertHallSv;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConcertHallRegisterRes register(
            @RequestBody ConcertHallRegisterReq concertHallRegisterReq
    ) {
        return concertHallSv.register(concertHallRegisterReq);
    }

    @GetMapping
    public ResponseEntity<List<ConcertHallFindRes>> findAllOrByStat(
            @RequestParam(required = false) String stat
    ) {
        List<ConcertHallFindRes> resList = (stat == null)
                ? concertHallSv.findAll()
                : concertHallSv.findByStat(stat);
        return ResponseEntity.ok(resList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConcertHallFindRes> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(concertHallSv.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConcertHallUpdateRes> update(
            @PathVariable Long id,
            @RequestBody ConcertHallUpdateReq concertHallUpdateReq
    ) {
        return ResponseEntity.ok(concertHallSv.update(id, concertHallUpdateReq));
    }
}

