package io.why503.performanceservice.domain.hall.model.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

public record HallRequest(
        @NotBlank(message = "공연장 이름")
        String hallName,                //공연장명

        @NotBlank(message = "우편번호")
        String hallPost,                //우편번호

        @NotBlank(message = "기본 주소")
        String hallBasicAddr,           //기본 주소

        @NotBlank(message = "상세 주소")
        String hallDetailAddr,          //상세 주소

        @NotBlank(message = "공연장 상태")
        String hallStatus,              //공연장 상태

        @NotNull(message = "좌석 수")
        @Min(value = 50, message = "좌석 수(최소 50석 이상)")
        Integer hallSeatScale,          //공연장 총 좌석 수

        @NotBlank(message = "공연장 구조 정보")
        String hallStructure,           //공연장 구조 정보

        @NotNull(message = "위도")
        @DecimalMin(value = "-90.0", message = "위도(-90 이상)")
        @DecimalMax(value = "90.0", message = "위도(90 이하)")
        BigDecimal hallLatitude,        //공연장 위도

        @NotNull(message = "경도")
        @DecimalMin(value = "-180.0", message = "경도(-180 이상)")
        @DecimalMax(value = "180.0", message = "경도(180 이하)")
        BigDecimal hallLongitude        //공연장 경도
) { }
