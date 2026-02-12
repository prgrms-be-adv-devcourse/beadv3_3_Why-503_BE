package io.why503.reservationservice.domain.entry.service;

// 예매 진입 토큰 발급 서비스
public interface EntryTokenService {

    // entryToken 발급
    String issue(Long userSq, Long roundSq);
}
