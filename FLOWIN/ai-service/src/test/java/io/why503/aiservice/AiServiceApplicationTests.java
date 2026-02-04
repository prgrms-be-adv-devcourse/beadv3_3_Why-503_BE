package io.why503.aiservice;

import io.why503.aiservice.model.vo.*;
import io.why503.aiservice.service.AiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
class AiServiceApplicationTests {


    @Autowired
    private AiService aiService;

    @Test
    void contextLoads() {
        ResultRequest request = new ResultRequest(
                List.of(Category.MUSICAL),
                List.of(Category.CONCERT),
                List.of(MoodCategory.COMEDY)
        );

        ResultResponse response = aiService.getRecommendations(request);

        assertThat(response).isNotNull();
        assertThat(response.recommendations()).isNotEmpty();
        assertThat(response.summary()).isNotBlank();
    }

}
