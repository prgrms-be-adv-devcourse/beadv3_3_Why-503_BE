package io.why503.gatewayservice.entrytoken.service;

public interface EntryTokenValidator {
    // EntryToken 검증
    boolean isValid(String showId, String userId);
}