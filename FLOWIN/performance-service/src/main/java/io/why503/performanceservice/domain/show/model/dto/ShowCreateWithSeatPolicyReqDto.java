package io.why503.performanceservice.domain.show.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import io.why503.performanceservice.domain.showseat.model.dto.SeatPolicyReqDto;

/**
 * Show Create With Seat Policy Request DTO
 *
 * 사용 목적 :
 * - 공연 등록 시
 *   공연 기본 정보 + 좌석 판매 정책을 함께 전달받기 위한 Wrapper DTO
 *
 * 구성 :
 * - show         : 기존 ShowReqDto (공연 기본 정보)
 * - seatPolicies : 좌석 판매 정책 목록
 */
@Getter
@NoArgsConstructor
public class ShowCreateWithSeatPolicyReqDto {

    /**
     * 공연 기본 정보
     */
    private ShowReqDto show;

    /**
     * 좌석 판매 정책 목록
     */
    private List<SeatPolicyReqDto> seatPolicies;
}
