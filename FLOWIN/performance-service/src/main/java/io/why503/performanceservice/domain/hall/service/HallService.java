/**
 * Concert Hall Service
 * 공연장 도메인의 비즈니스 로직을 정의하는 Service 인터페이스
 * 사용 목적 :
 * - 공연장 등록
 * - 공연장 단건 조회
 * - 공연장 등록 시 좌석 자동 생성
 * 설계 의도 :
 * - Controller 와 Repository 사이의 중간 계층
 * - 구현체 교체 및 테스트 용이성 확보
 */
package io.why503.performanceservice.domain.hall.service;

import java.util.List;

import io.why503.performanceservice.domain.hall.model.dto.request.HallRequest;
import io.why503.performanceservice.domain.hall.model.dto.response.HallResponse;
import io.why503.performanceservice.domain.seat.model.dto.vo.SeatAreaCreateVo;

public interface HallService {

    // 공연장 등록
    void createHall(Long userSq, HallRequest request);

    // 공연장 조회
    HallResponse getHall(Long hallSq);
    
    /** (관리자) 좌석 생성 공연장 등록
     * - 공연장 생성
     * - 관리자 입력 구역/좌석 수 기준 좌석 생성
     */
    Long createWithCustomSeats(
            Long userSq,
            HallRequest request,
            List<SeatAreaCreateVo> areaCreateVos
    );
}
