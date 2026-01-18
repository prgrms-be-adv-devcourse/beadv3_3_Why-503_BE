package io.why503.accountservice.domain.companies.sv;

public interface CompanyEmailVerifySv {
    public boolean verify(String email, String inputCode);
}
