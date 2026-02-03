package io.why503.commonbase.factory;

import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.exception.performance.domain.*;
import org.springframework.http.HttpStatus;

/**
 * 입력 값은 모두
 * String message
 * HttpStatus
 * 로 고정
 */
public final class PerformanceFactory {

    private PerformanceFactory(){}

    public static CustomException hallException(String message, HttpStatus status){
        return new PerformanceHallException(message, status);
    }
    public static CustomException roundException(String message, HttpStatus status){
        return new PerformanceRoundException(message,status);
    }
    public static CustomException showException(String message, HttpStatus status){
        return new PerformanceShowException(message, status);
    }
    public static CustomException seatException(String message, HttpStatus status){
        return new PerformanceSeatException(message, status);
    }
    public static CustomException showSeatException(String message, HttpStatus status){
        return new PerformanceShowSeatException(message, status);
    }
    public static CustomException roundSeatException(String message, HttpStatus status){
        return new PerformanceRoundSeatException(message, status);
    }
}
