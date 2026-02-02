package io.why503.gatewayservice.entrytoken.service;
/**
 * EntryToken 발급 회수
 * Queue 통과 시 issue()
 * 예매 성공 또는 TTL 만료 시 revoke()
 */
public interface EntryTokenIssuer {
    // EntryToken 발급
    void issue(String showId, String userId);

    // EntryToken 회수
    void revoke(String showId, String userId);
}
