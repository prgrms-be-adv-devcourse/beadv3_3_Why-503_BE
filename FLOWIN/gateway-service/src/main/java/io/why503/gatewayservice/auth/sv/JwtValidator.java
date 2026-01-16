package io.why503.gatewayservice.auth.sv;

import io.why503.gatewayservice.auth.model.dto.TokenBody;
import java.util.Map;

//JwtValidator 인터페이스, 실체화는 impl에서
public interface JwtValidator {
    public Map<String, Object> getClaims(String token);
    public TokenBody parse(String t);
    public boolean validate(String t);
}
