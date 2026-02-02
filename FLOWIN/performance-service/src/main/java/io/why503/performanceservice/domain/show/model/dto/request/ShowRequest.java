/**
 * Show Request DTO
 * 공연 등록 요청 시 전달되는 데이터를 담는 DTO
 * 사용 목적 :
 * - 공연 등록 API에서 클라이언트 요청 데이터 수신
 */
package io.why503.performanceservice.domain.show.model.dto.request;

import io.why503.performanceservice.domain.show.model.enums.ShowCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ShowRequest(
        @NotBlank String showName,          // 공연명
        @NotNull LocalDateTime showStartDt,   // 공연 시작일
        @NotNull LocalDateTime showEndDt,     // 공연 종료일
        @NotNull LocalDateTime showOpenDt,      // 티켓 오픈 일시
        @NotBlank String showRunningTime,          // 러닝타임
        @NotBlank String showViewingAge,        // 관람 등급
        @NotNull ShowCategory showCategory,          // 공연 카테고리
        @NotBlank String showStatus,            //공연 상태
        @NotNull Long hallSq         // 공연장 식별자
) { }
