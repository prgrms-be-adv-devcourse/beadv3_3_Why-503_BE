package io.why503.accountservice.Sv.Impl;

import io.jsonwebtoken.*;
import io.why503.accountservice.Model.Dto.TokenBody;
import io.why503.accountservice.Sv.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtProviderImpl implements TokenProvider {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    @Override
    public String issue(Long validTime, Map<String, Object> claims) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .issuedAt(now)
                .expiration(new Date(now.getTime() + validTime))
                .signWith(privateKey, Jwts.SIG.RS256);

        for(Map.Entry<String, Object> i : claims.entrySet()){
            builder.claim(i.getKey(), i.getValue());
        }
        return builder.compact();
    }


    @Override
    public Map<String, Object> getClaims(String token) {
        Jws<Claims> parsedJwt =Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token);
        return parsedJwt.getPayload();
    }

    @Override
    public TokenBody parse(String t) {
        return new TokenBody(
                getClaims(t).get("Sq").toString()
        );
    }

    @Override
    public boolean validate(String t) {
        try {
            getClaims(t);
            return true;
        } catch ( JwtException e ) {
            log.info("Invalid JWT Token was detected: {}  msg : {}", t ,e.getMessage());
        } catch ( IllegalStateException e ) {
            log.info("JWT claims String is empty: {}  msg : {}", t ,e.getMessage());
        } catch ( Exception e ) {
            log.error("an error raised from validating token : {}  msg : {}", t ,e.getMessage());
        }

        return false;
    }
}
