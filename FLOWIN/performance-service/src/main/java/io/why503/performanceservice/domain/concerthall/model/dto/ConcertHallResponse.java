/**
 * Concert Hall Response DTO
 * 공연장 조회 및 등록 결과 반환용 데이터 객체
 *
 * 사용 목적 :
 * - 공연장 등록 결과 반환
 * - 공연장 단건 조회 응답
 *
 * 설계 메모 :
 * - Entity 직접 노출 방지
 * - API 응답 구조 통일 목적
 */
package io.why503.performanceservice.domain.concerthall.model.dto;

import java.math.BigDecimal;

import io.why503.performanceservice.domain.concerthall.model.dto.enums.ConcertHallStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConcertHallResponse {

    /**
     * 공연장 식별자
     */
    private Long concertHallSq;

    /**
     * 공연장명
     */
    private String concertHallName;

    /**
     * 우편번호
     */
    private String concertHallPost;

    /**
     * 기본 주소
     */
    private String concertHallBasicAddr;

    /**
     * 상세 주소
     */
    private String concertHallDetailAddr;

    /**
     * 공연장 상태
     */
    private ConcertHallStatus concertHallStatus;

    /**
     * 총 좌석 수
     */
    private Integer concertHallSeatScale;

    /**
     * 공연장 구조
     */
    private String concertHallStructure;

    /**
     * 위도
     */
    private BigDecimal concertHallLatitude;

    /**
     * 경도
     */
    private BigDecimal concertHallLongitude;
}
