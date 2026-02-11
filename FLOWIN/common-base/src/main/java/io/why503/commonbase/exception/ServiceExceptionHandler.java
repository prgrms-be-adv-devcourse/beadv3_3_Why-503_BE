package io.why503.commonbase.exception;

import io.why503.commonbase.model.dto.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * 이걸 상속받아서 @RestControllerAdvice를 달아줘야 함
 * Uri 오류, redis 연결 오류는 md 파일에서 확인 후에 붙이면 됨
 */
public abstract class ServiceExceptionHandler {

    protected final PathFinder pathFinder;
    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected ServiceExceptionHandler(PathFinder pathFinder){
        this.pathFinder = pathFinder;
    }

    //커스텀 전체 핸들러, 이건 그대로 가져다 쓰면 됨.
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> customExceptionHandler(
            CustomException e
    ) {
        return loggingCustomException(e);
    }

    //@Valid 예외 핸들러. 여러가지가 던져지니 그거에 맞춰서 잡을 것
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> validExceptionHandler(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        //@NotNull(그냥 단어)하면 여기서 취합해서 메시지로 만들어줌
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(i -> i.getDefaultMessage())
                .collect(Collectors.joining(", ")) + "이(가) 누락되거나 옳바르지 않습니다";
        //pathFinder, readme 참조
        CustomException ex = pathFinder.findPath(request, message, HttpStatus.BAD_REQUEST);
        if(ex == null){
            return unknownExceptionHandler(new Exception("unknown MethodArgumentNotValidException"));
        }
        //loggingCustomException 참조, 로그 찍고 반환
        return loggingCustomException(ex);
    }

    /*JPA의 json 매칭 실패(위 함수와 같은 부분은 주석 생략)
    간단하게 설명하면 java->json에서 필요한 조건이 안맞으면 발생하는 오류 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> jsonMatchExceptionHandler(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ) {
        CustomException ex = pathFinder.findPath(request, "형식 문제", HttpStatus.BAD_REQUEST);

        if(ex == null){
            return unknownExceptionHandler(new Exception("unknown HttpMessageNotReadableException"));
        }
        return loggingCustomException(ex);
    }

    //db 규칙 오류(여기서는 명시적으로 잡지 못한 중복, null오류를 찾고 못 잡은 건 Exception)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> dbExceptionHandler(
            DataIntegrityViolationException e,
            HttpServletRequest request
    ) {
        //우리가 사용하는 jdbc가 오류 코드를 찾아줌, 그걸 꺼내기 위한 과정
        if(NestedExceptionUtils.getRootCause(e) instanceof SQLException se){
            //notnull 오류, 즉 값 누락, 혹시 우리가 못 잡은 걸 막기 위해 있음, 400
            if(se.getErrorCode() == 1048){
                return checkDbException(request, "값 누락", HttpStatus.BAD_REQUEST,
                        "unknown DataIntegrityViolationException 1048");
            }
            //unique 위반, 즉 중복, 409
            else if(se.getErrorCode() == 1062){
                return checkDbException(request, "개체 중복", HttpStatus.CONFLICT,
                        "unknown DataIntegrityViolationException 1062");
            }
            //기타 등등 자세한 건 노출하면 안되니까 위치랑 내용만 찍고 500
            else{
                return checkDbException(request, "sorry, just wait a minute :)", HttpStatus.INTERNAL_SERVER_ERROR,
                        "unknown DataIntegrityViolationException");
            }
        }
        return unknownExceptionHandler(new Exception("DBExceptionHandler meet unknown Exception"));
    }

    //나머지 모르는 거 전체 핸들러(로그만 찍고 모르는 에러라고 메시지)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> unknownExceptionHandler(
            Exception e
    ){
        log.error("unknown", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ExceptionResponse(e));
    }
    /*
    CustomException 로그 출력 후, ResponseEntity<ExceptionResponse>반환
    MDC란 slf4j에서 사용되는 map형태 저장소
    ->대충 로그 찍을 때 dto를 om으로 변환 안하게 해도 되는 친구 = 내가 생각한거 먼저 구현한 사람이 있던 거임
    clear을 안하면 전의 로그값이 남아있을 수 있으니까 꼭 해야 함
    */
    protected ResponseEntity<ExceptionResponse> loggingCustomException(CustomException e) {
        try {
            //내가 원하는 값 채워넣기
            MDC.put("code", e.getCode());
            MDC.put("message", e.getMessage());
            MDC.put("id", e.getId());
            //없다면 알아서 null있다면 넣음
            if (e.getCause() != null) {
                MDC.put("cause", e.getCause().toString());
            }
            //로그 던지기
            log.error("CustomException", e);
        } finally {
            MDC.clear();
        }
        return ResponseEntity.status(e.getStatus())
                .body(new ExceptionResponse(e));
    }
    //db체크 메서드 일부분 함수화
    protected ResponseEntity<ExceptionResponse> checkDbException(
            HttpServletRequest request,
            String message,
            HttpStatus status,
            String messageException
    ){
        CustomException exception = pathFinder.findPath(request, message, status);
        if(exception == null){
            return unknownExceptionHandler(new Exception(messageException));
        }
        return loggingCustomException(exception);
    }
}
