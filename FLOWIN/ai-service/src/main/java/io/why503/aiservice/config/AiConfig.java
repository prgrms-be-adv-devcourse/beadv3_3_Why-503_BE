package io.why503.aiservice.config;

import lombok.Getter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Getter
@Configuration
public class AiConfig {


    //모든 서비스에서 chatClient 연결 위한 기본베이스
    @Bean
    public ChatClient chatClient(
            @Qualifier("openAiChatModel") ChatModel chatModel
            ) {
        return ChatClient.builder(chatModel).build();
    }


}