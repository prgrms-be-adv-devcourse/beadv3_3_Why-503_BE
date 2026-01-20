package io.why503.accountservice.domain.auth.service;

public interface CompanyAuthService {
    public void sendAuthCode(String email);
    public boolean verify(String email, String inputCode);
}
