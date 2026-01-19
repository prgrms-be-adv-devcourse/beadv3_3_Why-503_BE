package io.why503.accountservice.domain.companies.service;

public interface CompanyVerifyService {
    public boolean verify(String email, String inputCode);
}
