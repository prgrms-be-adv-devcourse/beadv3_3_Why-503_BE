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
package io.why503.performanceservice.domain.concerthall.service.Impl;

import java.util.List;

import io.why503.performanceservice.domain.concerthall.service.ConcertHallService;
import io.why503.performanceservice.domain.seat.model.dto.vo.SeatAreaCreateVo;
import io.why503.performanceservice.global.validator.UserValidator;
import io.why503.performanceservice.util.mapper.ConcertHallMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.why503.performanceservice.domain.concerthall.model.dto.request.ConcertHallRequest;
import io.why503.performanceservice.domain.concerthall.model.dto.response.ConcertHallResponse;
import io.why503.performanceservice.domain.concerthall.model.dto.enums.ConcertHallStatus;
import io.why503.performanceservice.domain.concerthall.model.entity.ConcertHallEntity;
import io.why503.performanceservice.domain.concerthall.repository.ConcertHallRepository;
import io.why503.performanceservice.domain.seat.service.SeatService;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Transactional(readOnly = true)
public class ConcertHallServiceImpl implements ConcertHallService {

    private final ConcertHallRepository concertHallRepo;
    private final SeatService seatSv;
    private final ConcertHallMapper concertHallMapper;
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
    public void createConcertHall(Long userSq, ConcertHallRequest request) {
        userValidator.validateEnterprise(userSq);

        ConcertHallEntity hall = concertHallMapper.requestToEntity(request);

        hall.setConcertHallStatus(
            ConcertHallStatus.fromCode(request.concertHallStatus())
        );
        concertHallRepo.save(hall);
    }

    /**
     * 공연장 단건 조회
     * 처리 흐름 :
     * 1. 공연장 식별자 기준 조회
     * 2. Entity → Response DTO 변환
     * @param concertHallSq 공연장 식별자
     * @return 공연장 응답 DTO
     */
    @Override
    public ConcertHallResponse getConcertHall(Long concertHallSq) {

        ConcertHallEntity hall = concertHallRepo.findById(concertHallSq)
                .orElseThrow(() -> new IllegalArgumentException("concert hall not found"));

        return concertHallMapper.entityToResponse(hall);
    }
    
    /**
     * 관리자 입력 기반 좌석 생성 공연장 등록
     */
    @Override
    @Transactional
    public Long createWithCustomSeats(
            Long userSq,
            ConcertHallRequest request,
            List<SeatAreaCreateVo> areaCreateVos
    ) {
        userValidator.validateEnterprise(userSq);
        ConcertHallEntity hall = concertHallMapper.requestToEntity(request);
        concertHallRepo.save(hall);

        // 공연장 생성 후 관리자 입력 기반 좌석 생성
        seatSv.createCustomSeats(hall, areaCreateVos);

        return hall.getSq();
    }
}
