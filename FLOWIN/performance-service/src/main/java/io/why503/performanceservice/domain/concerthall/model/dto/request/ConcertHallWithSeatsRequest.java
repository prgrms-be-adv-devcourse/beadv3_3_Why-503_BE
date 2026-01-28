package io.why503.performanceservice.domain.concerthall.model.dto.request;

import java.util.List;

import io.why503.performanceservice.domain.seat.model.dto.vo.SeatAreaCreateVo;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

//공연장 + 좌석 구역 정보를 함께 받기 위한 요청 DTO
public record ConcertHallWithSeatsRequest(
        @NotNull ConcertHallRequest concertHall,     //공연장 등록 정보
        @NotNull
        @NotEmpty
        List<SeatAreaCreateVo> seatAreas   //좌석 구역 생성 정보
){
}
