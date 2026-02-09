package io.why503.performanceservice.domain.hall.model.dto.response;

import java.math.BigDecimal;

import io.why503.performanceservice.domain.hall.model.dto.enums.HallStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HallResponse(
        @NotNull Long hallSq,                    //공연장 식별자
        @NotBlank String hallName,               //공연장명
        @NotBlank String hallPost,               //우편번호
        @NotBlank String hallBasicAddr,          //기본 주소
        @NotBlank String hallDetailAddr,         //상세 주소
        @NotBlank HallStatus hallStatus,         //공연장 상태
        @NotNull Integer hallSeatScale,          //총 좌석 수
        @NotBlank String hallStructure,          //공연장 구조
        @NotNull BigDecimal hallLatitude,        //위도
        @NotNull BigDecimal hallLongitude        //경도
) { }
