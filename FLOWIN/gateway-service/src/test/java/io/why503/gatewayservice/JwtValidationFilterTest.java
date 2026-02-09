package io.why503.gatewayservice;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

@Slf4j
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"eureka.client.enabled=false"}
)
@AutoConfigureWebTestClient
public class JwtValidationFilterTest {
    @Autowired
    WebTestClient webTestClient;

    @Test
    void no_token_is_401(){
        webTestClient.get()
                .uri("/accounts/")
                .exchange()
                .expectBody()
                .consumeWith(result -> {
                    byte[] body = result.getResponseBody();
                    log.info("STATUS = {}", result.getStatus());
                    log.info("BODY = {}", new String(body, StandardCharsets.UTF_8));
                });
    }
}
