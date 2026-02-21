package io.why503.accountservice.domain.auth.config;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/*
jwt의 인증키를 RSA로 구성 했기에 만들어진 RSA키(.pem)을 파싱하기 위한 함수
 */
@Configuration
public class JwtKeyConfig {

    @Value("${custom.jwt.secret.jwt-private-path}")
    private String privatePath; //private-key의 주소, yml에 명시

    private PrivateKey privateKey;

    //bean이 생성됨과 동시에 실행
    @PostConstruct
    private void init(){
        privateKey = getPrivateKey();
    }
    //PrivateKey를 반환
    @Bean
    public PrivateKey privateKey(){
        return privateKey;
    }

    //private-key를 해석하고 반환, i/o처리를 줄이기 위해서 시스템 시작 시 한번만 실행
    private PrivateKey getPrivateKey() {
        try{
            // [수정 1] yml 설정에 "classpath:"가 섞여 있을 수 있으니 안전하게 제거
            String cleanPath = privatePath.replace("classpath:", "");
            ClassPathResource privatePemFile = new ClassPathResource(cleanPath);

            // [수정 2] getFile() 대신 getInputStream()을 사용해 바이트로 읽기!
            byte[] keyBytes = privatePemFile.getInputStream().readAllBytes();

            // [수정 3] 바이트를 문자열로 변환 후 전처리
            String key = new String(keyBytes)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s" ,"");

            //Base64디코딩
            byte[] decodedKey = Base64.getDecoder().decode(key);

            //RSA PrivateKey규격을 의미하는 클래스
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);

            //완성된 PKCS8EncodedKeySpec을 받아서 key를 생성
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
        catch (Exception e){
            // 에러 메시지를 조금 더 명확하게 남겨두면 나중에 찾기 편합니다.
            throw new RuntimeException("프라이빗 키를 읽는 중 오류가 발생했습니다. 경로: " + privatePath, e);
        }
    }
}
