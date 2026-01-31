package io.why503.paymentservice.domain.point.service;

import io.why503.paymentservice.domain.point.model.dto.request.PointRequest;
import io.why503.paymentservice.domain.point.model.dto.response.PointResponse;
import io.why503.paymentservice.domain.point.model.entity.Point;

import java.util.List;

public interface PointService {

    /**
     * 포인트 충전 요청 생성
     * - 충전 대기(READY) 상태의 요청서를 생성합니다.
     * - 이후 PG사 결제가 완료되면 Account 서비스로 충전을 요청합니다.
     */
    PointResponse createPointCharge(Long userSq, PointRequest request);

    /**
     * 포인트 충전 요청 상세 조회
     * - 본인의 요청 내역만 조회 가능
     */
    PointResponse findPoint(Long userSq, Long pointSq);

    /**
     * 내 포인트 충전 이력 조회
     * - 최신순 정렬
     */
    List<PointResponse> findPointsByUser(Long userSq);

    /**
     * 포인트 충전 취소
     * - 대기(READY) 상태인 경우에만 취소 가능
     */
    PointResponse cancelPoint(Long userSq, Long pointSq);

    Point findByOrderId(String orderId);
}