/**
 * Concert Hall Service Implementation
 * 공연장 관련 비즈니스 로직 구현체
 * 처리 내용 :
 * - 공연장 등록
 * - 공연장 조회
 * - 공연장 등록 시 좌석 자동 생성
 * 주의 사항 :
 * - 좌석은 공영장에 종속된 고정 자원
 * - 좌석 생성 실패 시 공연장 생성도 롤백 되어야 함
 */
package io.why503.performanceservice.domain.hall.service.Impl;

import java.util.List;

import io.why503.performanceservice.domain.hall.service.HallService;
import io.why503.performanceservice.domain.seat.model.dto.vo.SeatAreaCreateVo;
import io.why503.performanceservice.global.error.ErrorCode;
import io.why503.performanceservice.global.error.exception.BusinessException;
import io.why503.performanceservice.global.validator.UserValidator;
import io.why503.performanceservice.util.mapper.HallMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.why503.performanceservice.domain.hall.model.dto.request.HallRequest;
import io.why503.performanceservice.domain.hall.model.dto.response.HallResponse;
import io.why503.performanceservice.domain.hall.model.dto.enums.HallStatus;
import io.why503.performanceservice.domain.hall.model.entity.HallEntity;
import io.why503.performanceservice.domain.hall.repository.HallRepository;
import io.why503.performanceservice.domain.seat.service.SeatService;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Transactional(readOnly = true)
public class HallServiceImpl implements HallService {

    private final HallRepository hallRepository;
    private final SeatService seatService;
    private final HallMapper hallMapper;
    private final UserValidator userValidator;

    /**
     * 공연장 등록
     * 처리 흐름 :
     * 1. 요청 DTO → Entity 변환
     * 2. 공연장 Entity 저장
     * @param request 공연장 등록 요청 DTO
     */
    @Override
    @Transactional
    public void createHall(Long userSq, HallRequest request) {
        userValidator.validateEnterprise(userSq);

        HallEntity hall = hallMapper.requestToEntity(request);

        hall.setHallStatus(
            HallStatus.fromCode(request.hallStatus())
        );
        hallRepository.save(hall);
    }

    /**
     * 공연장 단건 조회
     * 처리 흐름 :
     * 1. 공연장 식별자 기준 조회
     * 2. Entity → Response DTO 변환
     * @param hallSq 공연장 식별자
     * @return 공연장 응답 DTO
     */
    @Override
    public HallResponse getHall(Long hallSq) {

        HallEntity hall = hallRepository.findById(hallSq)
                .orElseThrow(() ->  new BusinessException(ErrorCode.HALL_NOT_FOUND));

        return hallMapper.entityToResponse(hall);
    }
    
    /**
     * 관리자 입력 기반 좌석 생성 공연장 등록
     */
    @Override
    @Transactional
    public Long createWithCustomSeats(
            Long userSq,
            HallRequest request,
            List<SeatAreaCreateVo> areaCreateVos
    ) {
        userValidator.validateEnterprise(userSq);
        HallEntity hall = hallMapper.requestToEntity(request);
        hallRepository.save(hall);

        // 공연장 생성 후 관리자 입력 기반 좌석 생성
        seatService.createCustomSeats(hall, areaCreateVos);

        return hall.getSq();
    }
}
