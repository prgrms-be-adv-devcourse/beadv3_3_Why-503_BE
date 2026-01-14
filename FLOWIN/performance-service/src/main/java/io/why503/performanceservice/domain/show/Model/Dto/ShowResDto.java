/**
 * Show Response DTO
 * 공연 조회 및 등록 응답 데이터를 담는 DTO
 *
 * 사용 목적 :
 * - 공연 등록 후 결과 반환
 * - 공연 단건 조회 결과 반환
 */
package io.why503.performanceservice.domain.show.Model.Dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import io.why503.performanceservice.domain.show.Model.Enum.ShowCategory;
import io.why503.performanceservice.domain.show.Model.Enum.ShowStatus;

@Getter
@Builder
public class ShowResDto {

    private Long showSq;              // 공연 시퀀스
    private String showName;           // 공연명
    private LocalDateTime startDate;   // 공연 시작일
    private LocalDateTime endDate;     // 공연 종료일
    private LocalDateTime openDt;      // 티켓 오픈 일시
    private String showTime;           // 러닝타임
    private String viewingAge;         // 관람 등급

    private ShowCategory category;     // 공연 카테고리
    private ShowStatus showStat;       // 공연 상태

    private Long concertHallSq;        // 공연장 식별자
    private Long companySq;            // 회사 식별자
}
