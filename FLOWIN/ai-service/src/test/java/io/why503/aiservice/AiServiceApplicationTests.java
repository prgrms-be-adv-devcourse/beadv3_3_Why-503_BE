package io.why503.aiservice;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@ActiveProfiles("test")
@SpringBootTest
@Testcontainers
class AiServiceApplicationTests {

    // Testcontainers가 관리하는 컨테이너
    @Container
    private static final GenericContainer<?> vectorStoreContainer =
            new GenericContainer<>("your/vectorstore-image:latest") // CI용 벡터스토어 이미지
                    .withExposedPorts(8080);

    // 컨테이너 URL 가져오기
    private static String vectorStoreUrl;

    @BeforeAll
    static void setUp() {
        // 컨테이너가 이미 시작된 상태이므로 포트 매핑 정보 가져오기
        String host = vectorStoreContainer.getHost();
        Integer port = vectorStoreContainer.getMappedPort(8080);
        vectorStoreUrl = "http://" + host + ":" + port;
        System.out.println("VectorStore URL: " + vectorStoreUrl);
    }



}
