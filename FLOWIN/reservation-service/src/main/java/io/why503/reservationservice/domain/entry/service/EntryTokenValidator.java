package io.why503.reservationservice.domain.entry.service;

public interface EntryTokenValidator {

    void validate(Long roundSq, Long userSq);

}