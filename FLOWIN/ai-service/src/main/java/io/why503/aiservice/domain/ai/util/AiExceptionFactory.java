package io.why503.aiservice.domain.ai.util;

import io.why503.aiservice.domain.ai.util.exception.AiBadRequest;
import io.why503.aiservice.domain.ai.util.exception.AiConflict;
import io.why503.aiservice.domain.ai.util.exception.AiNotFound;
import io.why503.commonbase.exception.CustomException;

public class AiExceptionFactory {
    public static CustomException AiBadRequest(String message){
        return new AiBadRequest(message);
    }
    public static CustomException AiBadRequest(Throwable cause){
        return new AiBadRequest(cause);
    }
    public static CustomException AiNotFound(String message){
        return new AiNotFound(message);
    }
    public static CustomException AiNotFound(Throwable cause){
        return new AiNotFound(cause);
    }
    public static CustomException AiConflict(String message){
        return new AiConflict(message);
    }
    public static CustomException AiConflict(Throwable cause){
        return new AiConflict(cause);
    }
    public static CustomException AiForbidden(String message){
        return new AiConflict(message);
    }
    public static CustomException AiForbidden(Throwable cause){
        return new AiConflict(cause);
    }
}
