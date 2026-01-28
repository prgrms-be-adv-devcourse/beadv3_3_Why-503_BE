/**
 * Show Response DTO
 * 공연 조회 및 등록 응답 데이터를 담는 DTO
 * 사용 목적 :
 * - 공연 등록 후 결과 반환
 * - 공연 단건 조회 결과 반환
 */
package io.why503.performanceservice.domain.show.model.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import io.why503.performanceservice.domain.show.model.enums.ShowCategory;
import io.why503.performanceservice.domain.show.model.enums.ShowStatus;

public record ShowResponse(
        @NotNull Long showSq,               // 공연 시퀀스
        @NotBlank String showName,          // 공연명
        @NotNull LocalDateTime startDate,   // 공연 시작일
        @NotNull LocalDateTime endDate,     // 공연 종료일
        @NotNull LocalDateTime openDt,      // 티켓 오픈 일시
        @NotBlank String showTime,          // 러닝타임
        @NotBlank String viewingAge,        // 관람 등급

        @NotNull ShowCategory category,     // 공연 카테고리
        @NotNull ShowStatus showStatus,       // 공연 상태

        @NotNull Long concertHallSq,        // 공연장 식별자
        @NotNull Long companySq             // 회사 식별자
) { }
