package io.why503.performanceservice.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth
    // HTTP 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    // HTTP 403
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // User / External
    // HTTP 502
    USER_SERVICE_UNAVAILABLE(HttpStatus.BAD_GATEWAY, "유저 서비스 연결 실패"),

    // Show
    // HTTP 403
    PERFORMANCE_CREATE_FORBIDDEN(HttpStatus.FORBIDDEN, "공연 생성 권한이 없습니다."),
    // HTTP 404
    SHOW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 공연입니다."),

    // ConcertHall
    // HTTP 404
    CONCERT_HALL_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 공연장입니다."),

    // Round
    //HTTP 400
    ROUND_BAD_REQUEST(HttpStatus.BAD_REQUEST, "회차 상태가 올바르지 않습니다."),
    // HTTP 404
    ROUND_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회차입니다."),
    // HTTP 409
    ROUND_CONFLICT(HttpStatus.CONFLICT, "이미 해당 시간에 등록된 회차가 존재합니다."),

    // RoundSeat / Seat
    // HTTP 404
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 좌석입니다."),
    // HTTP 409
    SEAT_ALREADY_RESERVED(HttpStatus.CONFLICT, "이미 선택되었거나 예매가 불가능한 좌석입니다."),
    // HTTP 409
    SEAT_ALREADY_SELECTED(HttpStatus.CONFLICT, "다른 사용자가 이미 선점한 좌석입니다."),
    // HTTP 403
    NOT_MY_SEAT(HttpStatus.FORBIDDEN, "본인이 선점한 좌석만 결제할 수 있습니다."),
    // HTTP 400
    RESERVATION_EXPIRED(HttpStatus.BAD_REQUEST, "선점 시간이 만료되었습니다. 다시 예매해주세요."),

    // Server
    // HTTP 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    //입력값 오류
    // HTTP 400
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다.");


    private final HttpStatus httpStatus;
    private final String message;
}