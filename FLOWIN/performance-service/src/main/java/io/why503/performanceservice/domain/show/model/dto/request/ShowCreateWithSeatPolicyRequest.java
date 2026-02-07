package io.why503.performanceservice.domain.show.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

import io.why503.performanceservice.domain.showseat.model.dto.request.SeatPolicyRequest;

public record ShowCreateWithSeatPolicyRequest(
        @NotNull(message = "공연 기본 정보는 필수입니다.")
        @Valid
        ShowRequest showRequest,
        @NotNull(message = "좌석 정책 목록은 필수입니다.")
        @NotEmpty(message = "좌석 정책은 최소 1개 이상 필요합니다.")
        @Valid
        List<SeatPolicyRequest> seatPolicies
) { }
