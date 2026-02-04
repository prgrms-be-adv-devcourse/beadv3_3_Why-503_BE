package io.why503.accountservice.domain.companies.util;

import io.why503.accountservice.domain.companies.util.exception.CompanyBadRequest;
import io.why503.accountservice.domain.companies.util.exception.CompanyNotFound;
import io.why503.commonbase.exception.CustomException;

public final class CompanyExceptionFactory {

    public static CustomException companyBadRequest(String message){
        return new CompanyBadRequest(message);
    }
    public static CustomException companyBadRequest(Throwable cause){
        return new CompanyBadRequest(cause);
    }
    public static CustomException companyNotFound(String message){
        return new CompanyNotFound(message);
    }
    public static CustomException companyNotFound(Throwable cause){
        return new CompanyNotFound(cause);
    }

}
