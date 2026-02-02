package io.why503.gatewayservice.queue.service;

/**
 * 사용자가 지금 바로 입장 가능한지 판단
 * 입장이 불가능하면 대기열에 진입
 */

public interface QueueService {

    // 지금 바로 통과 가능한지 판단
    boolean canEnter(String showId, String userId);

    // 대기열에 사용자를 추가
    void enqueue(String showId, String userId);

    // 이미 대기열에 들어가 있는지 여부 
    boolean isAlreadyQueued(String showId, String userId);

    // 대기열 상태 파악
    Long getQueuePosition(String showId, String userId);
    Long getQueueSize(String showId);

    
}
