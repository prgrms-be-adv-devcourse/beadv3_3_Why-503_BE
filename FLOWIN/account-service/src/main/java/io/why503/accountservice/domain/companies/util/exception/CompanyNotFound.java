package io.why503.accountservice.domain.companies.util.exception;

import io.why503.commonbase.exception.account.domain.AccountCompanyException;
import org.springframework.http.HttpStatus;

public class CompanyNotFound extends AccountCompanyException {
    public CompanyNotFound(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public CompanyNotFound(Throwable cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}
