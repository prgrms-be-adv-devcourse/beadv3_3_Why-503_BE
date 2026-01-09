package io.why503.accountservice.domain.auth.sv;

import io.why503.accountservice.domain.auth.model.dto.TokenBody;

import java.util.Map;

//jwt 발행자 인터페이스, 실체화는 impl파일에 존재
public interface JwtProvider {
    public String issue(Long validTime, Map<String, Object> claims);
    public Map<String, Object> getClaims(String token);
    public TokenBody parse(String t);
    public boolean validate(String t);
}

