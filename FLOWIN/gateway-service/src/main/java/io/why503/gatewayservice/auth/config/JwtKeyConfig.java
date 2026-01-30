package io.why503.gatewayservice.auth.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
