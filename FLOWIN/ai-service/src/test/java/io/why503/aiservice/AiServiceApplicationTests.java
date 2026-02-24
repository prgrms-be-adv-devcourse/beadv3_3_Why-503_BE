package io.why503.aiservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
@SpringBootTest
@EnableFeignClients
class AiServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
