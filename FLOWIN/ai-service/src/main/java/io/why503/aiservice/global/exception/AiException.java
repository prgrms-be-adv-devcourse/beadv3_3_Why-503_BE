package io.why503.aiservice.global.exception;

import org.springframework.http.HttpStatus;

public class AiException extends CustomException {
    protected AiException(String message, String code, HttpStatus status) {
        super(message, "PE-" + code, status);
    }
    protected AiException(Throwable cause, String code, HttpStatus status) {
        super(cause, "PE-" + code, status);
    }

    public static AiException NotFound(String message) {
        return new AiException(
                "해당 공연 장르를 찾을 수 없습니다. message=" + message,
                "AI_001",
                HttpStatus.BAD_REQUEST
        );
    }

    public static AiException invalidCategory() {
        return new AiException(
                "유효하지 않은 카테고리입니다.",
                "AI_002",
                HttpStatus.BAD_REQUEST
        );
    }

    public static AiException invalidGenre() {
        return new AiException(
                "유효하지 않은 장르입니다.",
                "AI_003",
                HttpStatus.BAD_REQUEST
        );
    }

    public static AiException invalidResponse() {
        return new AiException(
                "AI 파싱 실패",
                "AI_004",
                HttpStatus.BAD_REQUEST
        );
    }

    public static AiException performanceFailed(Object doc) {
        return new AiException(
                "공연 매핑 실패: doc=" + doc,
                "AI_005",
                HttpStatus.BAD_REQUEST);
    }
//
//    public static AiException embedFailed(Throwable cause) {
//        return new AiException(
//                cause,
//                "AI_005",
//                HttpStatus.INTERNAL_SERVER_ERROR
//        );
//    }
//
//    public static AiException ResponseFailed(Throwable cause) {
//        return new AiException(
//                cause,
//                "AI_006",
//                HttpStatus.INTERNAL_SERVER_ERROR
//        );
//    }
}