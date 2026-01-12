package io.why503.theaterservice.Ctrl;

import io.why503.theaterservice.model.Ett.concert_hallEtt;
import io.why503.theaterservice.model.Dto.hallReq;
import io.why503.theaterservice.Sv.hallSv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/theaters")
public class hallCtrl {

    private final hallSv service;


//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public ResponseEntity<concert_hall> register(
//            @RequestBody hallRequest hallrequest
//    ) {
//        concert_hall register = service.register(hallrequest);
//        return ResponseEntity.ok(register);
//    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public concert_hallEtt register(
            @PathVariable Long id,
            @RequestBody hallReq hallrequest
    ) {
        return service.register(id, hallrequest);
    }

    @GetMapping
    public ResponseEntity<List<concert_hallEtt>> getAll() {
        List<concert_hallEtt> concerts = service.findAll();
        return ResponseEntity.ok(concerts);
    }
}

