package io.why503.commonbase.exception;

import org.springframework.http.HttpStatus;

/**
 * 커스텀 예외, 추상클래스
 * 이걸 상속받아서 각 도메인의 예외 구현
 * md 파일
 */
public abstract class CustomException extends RuntimeException{
    protected String code;
    protected HttpStatus status;

    /**
     * 생성자
     * @param message 늘 쓰는 그 메시지
     * @param status httpStatus 입력
     * @param port 포트번호
     */
    protected CustomException(String message, HttpStatus status, int port){
        super(message);
        this.status = status;
        code = Integer.toString(port) + "-" + Integer.toString(status.value());
    }
    //httpstatus 반환
    public HttpStatus getHttpStatus(){
        return status;
    }
    //이건 코드 반환 현재 {포트번호}-{http코드} 형태
    public String getCode(){
        return code;
    }
}
