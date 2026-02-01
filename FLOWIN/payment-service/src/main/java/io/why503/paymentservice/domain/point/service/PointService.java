package io.why503.paymentservice.domain.point.service;

import io.why503.paymentservice.domain.point.model.dto.request.PointRequest;
import io.why503.paymentservice.domain.point.model.dto.response.PointResponse;
import io.why503.paymentservice.domain.point.model.entity.Point;

import java.util.List;

/**
 * 포인트 충전 요청의 생성, 조회 및 상태 관리를 정의하는 서비스 인터페이스
 */
public interface PointService {

    // 새로운 포인트 충전 요청 생성
    PointResponse createPointCharge(Long userSq, PointRequest request);

    // 충전 요청 건에 대한 상세 정보 조회
    PointResponse findPoint(Long userSq, Long pointSq);

    // 사용자의 전체 포인트 충전 이력 목록 조회
    List<PointResponse> findPointsByUser(Long userSq);

    // 진행 중인 포인트 충전 요청 취소
    PointResponse cancelPoint(Long userSq, Long pointSq);

    // 주문 번호를 기반으로 포인트 엔티티 검색
    Point findByOrderId(String orderId);
}