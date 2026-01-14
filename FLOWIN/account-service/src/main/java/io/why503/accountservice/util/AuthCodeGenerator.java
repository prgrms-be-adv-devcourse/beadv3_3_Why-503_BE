/**
 * 인증 코드 생성 유틸 클래스
 * 사용 목적 :
 * - 이메일 인증에 사용할 일회성 인증 코드 생성
 */
package io.why503.accountservice.util;

import java.security.SecureRandom;

public class AuthCodeGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generate() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
