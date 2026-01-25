package io.why503.gatewayservice;

import io.why503.gatewayservice.config.TestKeyConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.security.PublicKey;

@ActiveProfiles("test")
@SpringBootTest
class GatewayServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
