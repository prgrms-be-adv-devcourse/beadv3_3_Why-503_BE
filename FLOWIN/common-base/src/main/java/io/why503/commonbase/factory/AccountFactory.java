package io.why503.commonbase.factory;

import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.exception.account.domain.AccountAccountException;
import io.why503.commonbase.exception.account.domain.AccountAuthException;
import io.why503.commonbase.exception.account.domain.AccountCompanyException;
import org.springframework.http.HttpStatus;

/**
 * 입력 값은 모두
 * String message
 * HttpStatus
 * 로 고정
 */
public final class AccountFactory {

    private AccountFactory(){}

    public static CustomException accountException(String message, HttpStatus status){
        return new AccountAccountException(message, status);
    }
    public static CustomException companyException(String message, HttpStatus status){
        return new AccountCompanyException(message, status);
    }
    public static CustomException authException(String message, HttpStatus status){
        return new AccountAuthException(message, status);
    }
}
