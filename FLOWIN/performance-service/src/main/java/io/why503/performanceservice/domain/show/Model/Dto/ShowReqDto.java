/**
 * Show Request DTO
 * 공연 등록 요청 시 전달되는 데이터를 담는 DTO
 *
 * 사용 목적 :
 * - 공연 등록 API에서 클라이언트 요청 데이터 수신
 */
package io.why503.performanceservice.domain.show.Model.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ShowReqDto {

    private String showName;          // 공연명
    private LocalDateTime startDate;  // 공연 시작일
    private LocalDateTime endDate;    // 공연 종료일
    private LocalDateTime openDt;     // 티켓 오픈 일시
    private String showTime;          // 러닝타임
    private String viewingAge;        // 관람 등급

    // 공연 카테고리 (번호로 전달받음)
    // 예: 0 콘서트 / 1 뮤지컬 / 2 연극 / 3 클래식
    private Integer category;

    private Long concertHallSq;       // 공연장 식별자
    private Long companySq;            // 회사 식별자
}
