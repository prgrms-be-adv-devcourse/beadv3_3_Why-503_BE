package io.why503.commonbase.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

/**
 * 원래 URI로 도메인을 찾아주던 로직을 분리
 * 꼭 작성해서 @Component를 붙여줘서 주입시켜줘야 함
 */
public interface PathFinder {
    CustomException findPath(HttpServletRequest request, String message, HttpStatus status);
}
