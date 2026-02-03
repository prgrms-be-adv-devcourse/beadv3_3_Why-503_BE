package io.why503.accountservice.domain.companies.util.exception;

import io.why503.commonbase.exception.account.domain.AccountCompanyException;
import org.springframework.http.HttpStatus;

public class CompanyBadRequest extends AccountCompanyException {
    public CompanyBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public CompanyBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}
