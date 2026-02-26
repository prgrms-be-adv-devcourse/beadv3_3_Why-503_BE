package io.why503.aiservice.domain.ai.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ChromaConfig {

    @Value("${spring.ai.vectorstore.chroma.client.host}")
    private String host;

    @Value("${spring.ai.vectorstore.chroma.client.port}")
    private int port;

    @Bean
    public ChromaApi chromaApi(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        //baseUrl, restClientBuilder 외에 objectMapper가 추가로 필요합니다.
        String baseUrl = host + ":" + port;
        return new ChromaApi(baseUrl, restClientBuilder, objectMapper);
    }

    @Bean
    public VectorStore vectorStore(ChromaApi chromaApi, EmbeddingModel embeddingModel) {
        // Builder 패턴을 사용하여 생성하며, initializeSchema(true)를 명시합니다.
        return ChromaVectorStore.builder(chromaApi, embeddingModel)
                .collectionName("SpringAiCollection")
                .initializeSchema(true)
                .build();
    }
}