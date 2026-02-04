package io.why503.aiservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;



@Import(FakeAiTestConfig.class)
@SpringBootTest
@ActiveProfiles("test")
class AiServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
