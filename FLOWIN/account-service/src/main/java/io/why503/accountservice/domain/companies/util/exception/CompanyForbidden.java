package io.why503.accountservice.domain.companies.util.exception;

import io.why503.commonbase.exception.account.domain.AccountCompanyException;
import org.springframework.http.HttpStatus;

public class CompanyForbidden extends AccountCompanyException {
    public CompanyForbidden(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }

    public CompanyForbidden(Throwable cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}
