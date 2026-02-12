package io.why503.reservationservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class ReservationServiceApplicationTests {

    /**
     * Redis TestContainer
     * - redis 7 사용
     * - TTL 만료 이벤트 활성화 (Ex)
     */
    @Container
    static final GenericContainer<?> redis =
            new GenericContainer<>("redis:7.2")
                    .withExposedPorts(6379)
                    .withCommand("redis-server --notify-keyspace-events Ex");

    /**
     * Spring Redis 설정을
     * TestContainer Redis 정보로 override
     */
    @DynamicPropertySource
    static void overrideRedisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Test
    void contextLoads() {
    }
}
