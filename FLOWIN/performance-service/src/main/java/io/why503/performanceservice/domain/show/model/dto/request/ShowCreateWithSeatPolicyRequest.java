package io.why503.performanceservice.domain.show.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import io.why503.performanceservice.domain.showseat.model.dto.SeatPolicyRequest;

/**
 * Show Create With Seat Policy Request DTO
 * 사용 목적 :
 * - 공연 등록 시
 *   공연 기본 정보 + 좌석 판매 정책을 함께 전달받기 위한 Wrapper DTO
 * 구성 :
 * - showRequest         : 기존 ShowReqDto (공연 기본 정보)
 * - seatPolicies : 좌석 판매 정책 목록
 */
public record ShowCreateWithSeatPolicyRequest(
        @NotNull
        ShowRequest showRequest,                       //공연 기본 정보
        @NotNull
        @NotEmpty
        List<SeatPolicyRequest> seatPolicies    //좌석 판매 정책 목록
) { }
