package io.why503.gatewayservice.auth.sv.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.why503.gatewayservice.auth.model.dto.TokenBody;
import io.why503.gatewayservice.auth.sv.JwtValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtValidatorImpl implements JwtValidator {
    private final PublicKey publicKey;
    //payload 반환
    @Override
    public Map<String, Object> getClaims(String token) {
        Jws<Claims> parsedJwt = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token);
        return parsedJwt.getPayload();
    }
    //tokenBody를 생성
    @Override
    public TokenBody parse(String t) {
        Map<String, Object> claim = getClaims(t);
        Long sq = ((Number)(claim.get("sq"))).longValue();
        return new TokenBody(sq);
    }
    //jwt 검증
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
