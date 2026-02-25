package io.why503.aiservice.domain.ai.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.why503.aiservice.domain.ai.model.vo.AiResponse;
import io.why503.aiservice.domain.ai.service.AiChat;
import io.why503.aiservice.domain.ai.service.AiService;
import io.why503.aiservice.global.exception.AiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class AiChatImpl implements AiChat {
    private final ChatClient chatClient;
    private final ObjectMapper mapper;

    //ai 프롬프트 보냄(AI서버) -> 문자열 응답식으로 받음
    public CompletableFuture<String> ask(String prompt) {
        try {
            return CompletableFuture.supplyAsync(() -> chatClient.prompt(prompt).call().content());

        } catch (Exception e) {
            log.error("ai 호출 실패", e);
            return CompletableFuture.failedFuture(e);
        }
    }
    //포스트맨에서 json 형식으로 받기 위한 파싱
    public AiResponse parse(String content) {
        try { return mapper.readValue(cleanJson(content), AiResponse.class);
        } catch (Exception e) { log.error("AI 응답 파싱 실패. content={}", content, e);
//            throw AiException.invalidResponse();
        }
        return null;
    }
    //출력에 빈 리스트를 받지 않기 위한 json에 불필요한 정보 지우기
    public String cleanJson(String content) {
        if (content == null) return "{}";
        int start = content.indexOf("{");
        int end = content.lastIndexOf("}");
        return (start >= 0 && end > start) ? content.substring(start, end + 1) : "{}";
    }
}
