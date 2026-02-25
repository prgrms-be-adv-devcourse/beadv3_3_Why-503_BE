package io.why503.aiservice.global.client.dto.response;

import java.time.LocalDateTime;

public record PerformanceResponse(
        Long showSq,               // 공연 시퀀스
        String showName,          // 공연명
        LocalDateTime startDt,   // 공연 시작일
        LocalDateTime endDt,     // 공연 종료일
        LocalDateTime openDt,      // 티켓 오픈 일시
        String showTime,          // 러닝타임
        String viewingAge,        // 관람 등급

        String category,     // 공연 카테고리
        String genre,           //공연 장르
        String showStatus,       // 공연 상태

        Long hallSq,        // 공연장 식별자
        Long companySq             // 회사 식별자
){ }