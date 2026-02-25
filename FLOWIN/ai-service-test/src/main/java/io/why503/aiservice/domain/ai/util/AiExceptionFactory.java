package io.why503.aiservice.domain.ai.util;

import io.why503.aiservice.domain.ai.util.exception.AiNoContent;
import io.why503.aiservice.domain.ai.util.exception.AiNotFound;
import io.why503.commonbase.exception.CustomException;

public class AiExceptionFactory {
    public static CustomException AiNotFound(String message){
        return new AiNotFound(message);
    }
    public static CustomException AiNotFound(Throwable cause){
        return new AiNotFound(cause);
    }
    public static CustomException AiNoContent(String message){
        return new AiNoContent(message);
    }
    public static CustomException AiNoContent(Throwable cause){
        return new AiNoContent(cause);
    }
}
