package io.why503.accountservice.auth.sv;

import io.why503.accountservice.auth.model.dto.TokenBody;

import java.util.Map;

public interface TokenProvider {
    public String issue(Long validTime, Map<String, Object> claims);
    public Map<String, Object> getClaims(String token);
    public TokenBody parse(String t);
    public boolean validate(String t);
}

