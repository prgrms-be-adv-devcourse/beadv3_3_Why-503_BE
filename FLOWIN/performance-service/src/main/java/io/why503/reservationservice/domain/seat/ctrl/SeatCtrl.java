package io.why503.performanceservice.domain.seat.ctrl;

import io.why503.performanceservice.domain.concert_hall.model.Ett.ConcertHallEtt;
import io.why503.performanceservice.domain.concert_hall.repo.ConcertHallRepo;
import io.why503.performanceservice.domain.seat.sv.SeatSv;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seats")
public class SeatCtrl {

    private final SeatSv seatSv;
    private final ConcertHallRepo concertHallRepo;


    @PostMapping("/concert-hall/{hallId}/create")
    public ResponseEntity<String> create(@PathVariable Long hallId) {
        ConcertHallEtt concert = concertHallRepo.findById(hallId)
                .orElseThrow(() -> new EntityNotFoundException("공연장이 없습니다"));

        seatSv.create(concert);
        return ResponseEntity.status(HttpStatus.CREATED).body("좌석 생성 완료");
    }


//    //좌석 증감
//    @PutMapping("/concert-hall/{hallId}/adjust")
//    public ResponseEntity<String> adjust(
//            @PathVariable Long hallId,
//            @RequestParam int newSeatScale
//    ) {
//        ConcertHallEtt concert = concertHallRepo.findById(hallId)
//                .orElseThrow(() -> new EntityNotFoundException("공연장이 없습니다"));
//
//        seatSv.adjust(concert, newSeatScale);
//        return ResponseEntity.ok("좌석 조정 완료");
//    }
//
//
//     //특정 좌석 존재 여부 확인
//    @GetMapping("/concert-hall/{hallId}/exists")
//    public ResponseEntity<Boolean> Exists(
//            @PathVariable Long hallId,
//            @RequestParam String seatArea,
//            @RequestParam int seatNo
//    ) {
//        ConcertHallEtt hall = concertHallRepo.findById(hallId)
//                .orElseThrow(() -> new EntityNotFoundException("공연장이 없습니다"));
//
//        boolean exists = seatSv.exists(hall, seatArea, seatNo);
//        return ResponseEntity.ok(exists);
//    }
}
