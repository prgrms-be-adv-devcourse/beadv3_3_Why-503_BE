package io.why503.accountservice.domain.companies.util;

import io.why503.accountservice.domain.companies.util.exception.CompanyBadRequest;

public final class CompanyExceptionFactory {

    public static CompanyBadRequest companyBadRequest(String message){
        return new CompanyBadRequest(message);
    }
    public static CompanyBadRequest companyBadRequest(Throwable cause){
        return new CompanyBadRequest(cause);
    }

}
