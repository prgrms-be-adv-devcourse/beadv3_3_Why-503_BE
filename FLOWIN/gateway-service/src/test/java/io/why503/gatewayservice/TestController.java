package io.why503.gatewayservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/__test/ok")
    public ResponseEntity<String> ok() {
        return ResponseEntity.ok("OK");
    }
}
