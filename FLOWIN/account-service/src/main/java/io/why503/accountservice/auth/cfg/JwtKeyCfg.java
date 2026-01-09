package io.why503.accountservice.auth.cfg;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Configuration
public class JwtKeyCfg {

    @Value("${custom.jwt.secret.jwt-private-path}")
    private String privatePath;
    @Value("${custom.jwt.secret.jwt-public-path}")
    private String publicPath;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    private void init(){
        privateKey = getPrivateKey();
        publicKey = getPublicKey();
    }

    @Bean
    public PrivateKey privateKey(){
        return privateKey;
    }

    @Bean
    public PublicKey publicKey(){
        return publicKey;
    }


    private PrivateKey getPrivateKey(){
        try{

            ClassPathResource privatePemFile = new ClassPathResource(privatePath); // wtf?

            String key = Files.readString(privatePemFile.getFile().toPath())
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s" ,"");
            byte[] decodedKey = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }


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
