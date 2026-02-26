package io.why503.gatewayservice.auth.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtKeyConfig {

    @Value("${custom.jwt.secret.jwt-public-path}")
    private String publicPath;

    private PublicKey publicKey;

    //bean이 생성됨과 동시에 실행
    @PostConstruct
    private void init(){
        publicKey = getPublicKey();
    }

    //publicKey반환 해주는 빈
    @Bean
    public PublicKey publicKey(){
        return publicKey;
    }

    //publicKey 키 생성 (I/O처리기 때문에 한번만)
    private PublicKey getPublicKey(){
        try{
            // 1. yml에 "classpath:"가 적혀있다면 그 글자를 지워줍니다.
            String cleanPath = publicPath.replace("classpath:", "");
            ClassPathResource publicPemFile = new ClassPathResource(cleanPath);

            // 2. [핵심] getFile() 대신 getInputStream()을 사용해서 스트림으로 읽어야 JAR 환경에서 터지지 않습니다!
            byte[] keyBytes = publicPemFile.getInputStream().readAllBytes();
            String key = new String(keyBytes)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s" ,"");

            byte[] decodedKey = Base64.getDecoder().decode(key);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        }
        catch (Exception e){
            throw new RuntimeException("퍼블릭 키를 읽는 중 오류가 발생했습니다.", e);
        }
    }
}
