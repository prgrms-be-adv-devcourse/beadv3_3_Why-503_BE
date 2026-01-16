package io.why503.accountservice.domain.auth.service.impl;

import io.jsonwebtoken.*;
import io.why503.accountservice.domain.auth.service.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
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
}
