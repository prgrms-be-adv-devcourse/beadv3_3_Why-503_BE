package io.why503.theaterservice.Controller;

import io.why503.theaterservice.model.Entity.concert_hall;
import io.why503.theaterservice.model.dto.hallRequest;
import io.why503.theaterservice.service.hallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/theaters")
public class hallController {

    private final hallService service;


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
    public concert_hall register(
            @PathVariable Long id,
            @RequestBody hallRequest hallrequest
    ) {
        return service.register(id, hallrequest);
    }

    @GetMapping
    public ResponseEntity<List<concert_hall>> getAll() {
        List<concert_hall> concerts = service.findAll();
        return ResponseEntity.ok(concerts);
    }
}

