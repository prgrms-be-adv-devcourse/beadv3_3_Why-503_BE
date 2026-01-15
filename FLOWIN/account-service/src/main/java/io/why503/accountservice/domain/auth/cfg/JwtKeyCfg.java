package io.why503.accountservice.domain.auth.cfg;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/*
jwt의 인증키를 RSA로 구성 했기에 만들어진 RSA키(.pem)을 파싱하기 위한 함수
 */
@Slf4j
@Configuration
public class JwtKeyCfg {

    @Value("${custom.jwt.secret.jwt-private-path}")
    private String privatePath; //private-key의 주소, yml에 명시

    @Value("${custom.jwt.secret.jwt-public-path}")
    private String publicPath; //public-key의 주소, yml에 명시

    private PrivateKey privateKey;
    private PublicKey publicKey;

    //bean이 생성됨과 동시에 실행
    @PostConstruct
    private void init(){
        privateKey = getPrivateKey();
        publicKey = getPublicKey();
    }
    //PrivateKey를 반환
    @Bean
    public PrivateKey privateKey(){
        return privateKey;
    }

    //PublicKey를 반환
    @Bean
    public PublicKey publicKey(){
        return publicKey;
    }

    //private-key를 해석하고 반환, 위 함수와 달리 한번만 실행한 이유는 i/o처리를 줄이기 위해서
    private PrivateKey getPrivateKey(){
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

    //위와 일부를 제외하고 완전 동일
    private PublicKey getPublicKey(){
        try{
            ClassPathResource publicPemFile = new ClassPathResource(publicPath);

            String key = Files.readString(publicPemFile.getFile().toPath())
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s" ,"");
            byte[] decodedKey = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
