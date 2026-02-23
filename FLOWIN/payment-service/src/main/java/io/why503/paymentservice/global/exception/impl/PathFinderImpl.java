package io.why503.paymentservice.global.exception.impl;

import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.exception.PathFinder;
import io.why503.commonbase.exception.account.domain.AccountAccountException;
import io.why503.commonbase.exception.account.domain.AccountAuthException;
import io.why503.commonbase.exception.account.domain.AccountCompanyException;
import io.why503.commonbase.exception.payment.domain.PaymentPaymentException;
import io.why503.commonbase.exception.payment.domain.PaymentPointException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Component
public class PathFinderImpl implements PathFinder {
    @Override
    public CustomException findPath(HttpServletRequest request, String message, HttpStatus status) {
        String s = request.getRequestURI().split("/")[1];
        return switch (s) {
            case "accounts" -> new AccountAccountException(message, status);
            case "company" -> new AccountCompanyException(message, status);
            case "auth" -> new AccountAuthException(message, status);
            default -> null;
        };
    }
}
