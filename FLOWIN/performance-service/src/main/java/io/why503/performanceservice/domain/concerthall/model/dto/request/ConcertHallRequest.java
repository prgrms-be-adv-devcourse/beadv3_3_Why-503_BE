/**
 * Concert Hall Request DTO
 * 공연장 등록 시 클라이언트로부터 전달받는 데이터 객체
 * 사용 목적 :
 * - 공연장 신규 등록 요청 데이터 전달
 * - Entity 생성 전 입력값 캡슐화
 * 설계 메모 :
 * - 공연장 상태는 초기 등록 시 기본값으로 처리 가능
 * - 좌석 정보는 별도 도메인에서 관리
 */
package io.why503.performanceservice.domain.concerthall.model.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

public record ConcertHallRequest(
        @NotBlank(message = "공연장 이름은 필수입니다.")
        String concertHallName,           //공연장명

        @NotBlank(message = "우편번호는 필수입니다.")
        String concertHallPost,           //우편번호

        @NotBlank(message = "기본 주소는 필수입니다.")
        String concertHallBasicAddr,      //기본 주소

        @NotBlank(message = "상세 주소는 필수입니다.")
        String concertHallDetailAddr,     //상세 주소

        @NotBlank(message = "공연장 상태는 필수입니다.")
        String concertHallStatus,           //공연장 상태

        @NotNull(message = "좌석 수는 필수입니다.")
        @Min(value = 50, message = "좌석 수는 최소 50석 이상이어야 합니다.")
        Integer concertHallSeatScale,     //공연장 총 좌석 수


        @NotBlank(message = "공연장 구조 정보는 필수입니다.")
        String concertHallStructure,       //공연장 구조 정보

        @NotNull(message = "위도는 필수입니다.")
        @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90 이하이어야 합니다.")
        BigDecimal concertHallLatitude,    //공연장 위도

        @NotNull(message = "경도는 필수입니다.")
        @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180 이하이어야 합니다.")
        BigDecimal concertHallLongitude    //공연장 경도


) { }
