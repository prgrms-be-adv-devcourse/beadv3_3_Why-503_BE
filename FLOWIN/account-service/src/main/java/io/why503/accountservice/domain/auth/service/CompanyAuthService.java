package io.why503.accountservice.domain.auth.service;

public interface CompanyAuthService {
    void sendAuthCode(String email);
    boolean verify(String email, String inputCode);
}
