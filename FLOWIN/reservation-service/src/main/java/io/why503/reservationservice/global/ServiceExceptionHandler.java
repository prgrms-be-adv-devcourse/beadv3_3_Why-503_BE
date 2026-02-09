package io.why503.reservationservice.global;

import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.exception.reservation.domain.ReservationBookingException;
import io.why503.commonbase.model.dto.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ServiceExceptionHandler {
    //커스텀 전체 핸들러, 이건 그대로 가져다 쓰면 됨.
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> customExceptionHandler(
            CustomException e
    ){
        loggingCustomException(e);
        return ResponseEntity.status(e.getStatus())
                .body(new ExceptionResponse(e));
    }


    //@Valid 예외 핸들러. 여러가지가 던져지니 그거에 맞춰서 잡을 것
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> validExceptionHandler(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) throws Exception{
        //이 오류의 도메인이 어디인지 찾기 위한 uri찾기
        String s = request.getRequestURI().split("/")[1];
        //notnull(그냥 단어)하면 여기서 취합해서 메시지로 만들어줌
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(i -> i.getDefaultMessage())
                .collect(Collectors.joining(", ")) + "이(가) 누락되거나 올바르지 않습니다";
        //makeExceptionURI 참조
        CustomException ex = makeExceptionByURI(s, message, HttpStatus.BAD_REQUEST);
        if(ex == null){
            throw new Exception(
                    "MethodArgumentNotValidException -> Exception : 없는 uri인데 MethodArgumentNotValidException"
            );
        }
        //loggingCustomException 참조, 로그 찍기 용임
        loggingCustomException(ex);
        return ResponseEntity.status(ex.getStatus())
                .body(new ExceptionResponse(ex));
    }

    /*json 파싱 실패(위 함수와 같은 부분은 주석 생략)
    간단하게 설명하면 java->json에서 발생하는 오류 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> JsonParseExceptionHandler(
            HttpMessageNotReadableException e,
            HttpServletRequest request
    ) throws Exception {
        String s = request.getRequestURI().split("/")[1];

        String message = "json 변환/해독 실패";

        CustomException ex = makeExceptionByURI(s, message, HttpStatus.BAD_REQUEST);

        if(ex == null){
            throw new Exception(
                    "HttpMessageNotReadableException -> Exception : 없는 uri인데 HttpMessageNotReadableException"
            );
        }
        loggingCustomException(ex);
        return ResponseEntity.status(ex.getStatus())
                .body(new ExceptionResponse(ex));
    }

    //db 규칙 오류(여기서는 명시적으로 잡지 못한 중복, null오류를 찾고 못 잡은 건 Exception)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> DBExceptionHandler(
            DataIntegrityViolationException e,
            HttpServletRequest request
    ) throws Exception{
        String s = request.getRequestURI().split("/")[1];
        //우리가 사용하는 jdbc가 오류 코드를 찾아줌, 그걸 꺼내기 위한 과정
        if(NestedExceptionUtils.getRootCause(e) instanceof SQLException se){
            //notnull 오류, 즉 값 누락, 혹시 우리가 못 잡은 걸 막기 위해 있음, 400
            if(se.getErrorCode() == 1048){
                CustomException exception = makeExceptionByURI(s, "값 누락", HttpStatus.BAD_REQUEST);
                if(exception == null){
                    throw new Exception("DBExceptionHandler 1048 location is unknown");
                }
                loggingCustomException(exception);
                return ResponseEntity.status(exception.getStatus())
                        .body(new ExceptionResponse(exception));
            }
            //unique 위반, 즉 중복, 409
            else if(se.getErrorCode() == 1062){
                CustomException exception = makeExceptionByURI(s, "개체 중복", HttpStatus.CONFLICT);
                if(exception == null){
                    throw new Exception("DBExceptionHandler 1062 location is unknown");
                }
                loggingCustomException(exception);
                return ResponseEntity.status(exception.getStatus())
                        .body(new ExceptionResponse(exception));
            }
            //기타 등등 자세한 건 노출하면 안되니까 위치랑 내용만 찍고 500
            else{
                CustomException exception = makeExceptionByURI(
                        s,
                        "sorry, just wait a minute :)",
                        HttpStatus.INTERNAL_SERVER_ERROR);
                if(exception == null){
                    throw new Exception("DBExceptionHandler etc location is unknown");
                }
                loggingCustomException(exception);
                return ResponseEntity.status(exception.getStatus())
                        .body(new ExceptionResponse(exception));
            }
        }
        throw new Exception("DBExceptionHandler meet unknown Exception");
    }

    //나머지 모르는 거 전체 핸들러(로그만 찍고 모르는 에러라고 메시지)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> unknownExceptionHandler(
            Exception e
    ){
        log.error("UnknownError\n", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("UnknownError");
    }

    //uri로 도메인 찾기
    private CustomException makeExceptionByURI(String s, String message, HttpStatus status){
        return switch (s) {
            case "booking" -> new ReservationBookingException(message, status);
            default -> null;
        };
    }

    //CustomException 로그 출력
    private void loggingCustomException(CustomException e){
        log.error("{}/{}/{}/{}/{}",
                e.getCause(),
                e.getCode(),
                e.getMessage(),
                e.getClass(),
                e.getUUID());
    }
}