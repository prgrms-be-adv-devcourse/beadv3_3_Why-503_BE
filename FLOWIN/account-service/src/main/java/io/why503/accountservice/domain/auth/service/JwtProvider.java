package io.why503.accountservice.domain.auth.service;

import java.util.Map;

//jwt 발행자 인터페이스, 실체화는 impl파일에 존재
public interface JwtProvider {
    String issue(Long validTime, Map<String, Object> claims);
}

