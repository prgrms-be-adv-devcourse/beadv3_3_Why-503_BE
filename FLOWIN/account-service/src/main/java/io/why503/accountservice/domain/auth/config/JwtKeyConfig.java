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
            //빌드 후 Resource에 있는 파일의 경로
            ClassPathResource privatePemFile = new ClassPathResource(privatePath);

            //전처리
            String key = Files.readString(privatePemFile.getFile().toPath())
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
            throw new RuntimeException(e);
        }
    }
}
