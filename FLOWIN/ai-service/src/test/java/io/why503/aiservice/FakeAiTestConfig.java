package io.why503.aiservice;

import org.mockito.Mockito;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@TestConfiguration
@ActiveProfiles("test")
public class FakeAiTestConfig {

    @Bean
    public ChatClient chatClient() {
        return Mockito.mock(ChatClient.class);
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return Mockito.mock(EmbeddingModel.class);
    }

    @Bean
    public VectorStore vectorStore() {
        return Mockito.mock(VectorStore.class);
    }
}

