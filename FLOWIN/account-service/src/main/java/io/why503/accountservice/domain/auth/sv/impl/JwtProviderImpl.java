package io.why503.accountservice.domain.auth.sv.impl;

import io.jsonwebtoken.*;
import io.why503.accountservice.domain.account.model.dto.UserRole;
import io.why503.accountservice.domain.auth.model.dto.TokenBody;
import io.why503.accountservice.domain.auth.sv.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;
/*
토큰 발행 클래스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider {
    private final PrivateKey privateKey; //주입 받은 PrivateKey
    private final PublicKey publicKey; //주입 받은 PublicKey

    @Override
    public String issue(Long validTime, Map<String, Object> claims) {
        Date now = new Date();
        JwtBuilder builder = Jwts.builder()
                .issuedAt(now) //생성 시간
                .expiration(new Date(now.getTime() + validTime)) //만료시간
                .signWith(privateKey, Jwts.SIG.RS256); //서명키, 서명알고리즘

        //jwt에 payload주입
        for(Map.Entry<String, Object> i : claims.entrySet()){
            builder.claim(i.getKey(), i.getValue());
        }
        return builder.compact();
    }

    //payload 반환
    @Override
    public Map<String, Object> getClaims(String token) {
        Jws<Claims> parsedJwt =Jwts.parser()
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
