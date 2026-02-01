package io.why503.paymentservice;

import io.why503.paymentservice.global.config.SchedulingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // 패키지 경로 주의!

@SpringBootTest
class PaymentServiceApplicationTests {

    @MockitoBean // @MockBean 대신 사용
    private SchedulingConfig schedulingConfig;

    @Test
    void contextLoads() {
    }
}