package io.why503.performanceservice.domain.hall.model.dto.request;

import java.util.List;

import io.why503.performanceservice.domain.seat.model.dto.vo.SeatAreaCreateVo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record HallWithSeatsRequest(

        @NotNull(message = "공연장 정보는 필수입니다.")
        @Valid
        HallRequest hall,

        @NotNull(message = "좌석 구역 정보는 필수입니다.")
        @NotEmpty(message = "좌석 구역은 최소 1개 이상 필요합니다.")
        @Valid
        List<SeatAreaCreateVo> seatAreas

) {}
