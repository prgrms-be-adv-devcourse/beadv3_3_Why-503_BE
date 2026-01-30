package io.why503.aiservice.config;

import lombok.Getter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;


@Getter
@Configuration
public class AiConfig {


    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }


}