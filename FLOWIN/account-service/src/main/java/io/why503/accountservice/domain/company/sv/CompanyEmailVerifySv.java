package io.why503.accountservice.domain.company.sv;

public interface CompanyEmailVerifySv {
    public boolean verify(String email, String inputCode);
}
