/**
 * Concert Hall Request DTO
 * 공연장 등록 시 클라이언트로부터 전달받는 데이터 객체
 *
 * 사용 목적 :
 * - 공연장 신규 등록 요청 데이터 전달
 * - Entity 생성 전 입력값 캡슐화
 *
 * 설계 메모 :
 * - 공연장 상태는 초기 등록 시 기본값으로 처리 가능
 * - 좌석 정보는 별도 도메인에서 관리
 */
package io.why503.performanceservice.domain.concerthall.model.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConcertHallRequest {

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
     * (Y/N)
     */
    private String concertHallStat;

    /**
     * 공연장 총 좌석 수
     */
    private Integer concertHallSeatScale;

    /**
     * 공연장 구조 정보
     */
    private String concertHallStructure;

    /**
     * 공연장 위도
     */
    private BigDecimal concertHallLatitude;

    /**
     * 공연장 경도
     */
    private BigDecimal concertHallLongitude;

}
