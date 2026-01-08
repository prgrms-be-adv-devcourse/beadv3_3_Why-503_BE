package io.why503.accountservice.Auth.Sv;

import io.why503.accountservice.Auth.Model.Dto.TokenBody;

import java.util.Map;

public interface TokenProvider {
    public String issue(Long validTime, Map<String, Object> claims);
    public Map<String, Object> getClaims(String token);
    public TokenBody parse(String t);
    public boolean validate(String t);
}

