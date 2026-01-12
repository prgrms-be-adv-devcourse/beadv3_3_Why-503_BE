/**
 * 인증 코드 생성 유틸 클래스
 *
 * 사용 목적 :
 * - 이메일 인증에 사용할 일회성 인증 코드 생성
 */
package io.why503.companyservice.utill;

import java.util.Random;

public class AuthCodeGenerator {

    public static String generate() {

        Random random = new Random(); // 난수 생성을 위한 Random 객체
        int code = 100000 + random.nextInt(900000); // 6자리 숫자 인증 코드 생성
        return String.valueOf(code); // 문자열 형태로 변환하여 반환
    }
}
