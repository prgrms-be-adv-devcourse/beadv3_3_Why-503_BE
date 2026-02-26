package io.why503.performanceservice.domain.hall.service.Impl;

import java.util.List;

import io.why503.performanceservice.domain.hall.service.HallService;
import io.why503.performanceservice.domain.hall.util.HallExceptionFactory;
import io.why503.performanceservice.domain.seat.model.dto.vo.SeatAreaCreateVo;
import io.why503.performanceservice.global.validator.UserValidator;
import io.why503.performanceservice.util.mapper.HallMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.why503.performanceservice.domain.hall.model.dto.request.HallRequest;
import io.why503.performanceservice.domain.hall.model.dto.response.HallResponse;
import io.why503.performanceservice.domain.hall.model.enums.HallStatus;
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

    // 공연장 등록
    @Override
    @Transactional
    public void createHall(Long userSq, HallRequest request) {
        userValidator.validateAdmin(userSq, HallExceptionFactory.hallForbidden("관리자만 공연장 등록이 가능합니다."));

        if (hallRepository.existsByNameAndBasicAddr(request.hallName(), request.hallBasicAddr())) {
            throw HallExceptionFactory.hallConflict("이미 해당 주소에 동일한 이름의 공연장이 존재합니다.");
        }

        HallEntity hall = hallMapper.requestToEntity(request);

        hall.setHallStatus(
            HallStatus.fromCode(request.hallStatus())
        );
        hallRepository.save(hall);
    }

    // 공연장 조회
    @Override
    public HallResponse getHall(Long hallSq) {

        HallEntity hall = hallRepository.findById(hallSq)
                .orElseThrow(() -> HallExceptionFactory.hallNotFound("존재하지 않는 공연장입니다."));

        return hallMapper.entityToResponse(hall);
    }

    // 관리자 입력 기반 좌석 생성 공연장 등록
    @Override
    @Transactional
    public Long createWithCustomSeats(
            Long userSq,
            HallRequest request,
            List<SeatAreaCreateVo> areaCreateVos
    ) {
        userValidator.validateAdmin(userSq, HallExceptionFactory.hallForbidden("관리자만 공연장 등록이 가능합니다."));

        if (hallRepository.existsByNameAndBasicAddr(request.hallName(), request.hallBasicAddr())) {
            throw HallExceptionFactory.hallConflict("이미 해당 주소에 동일한 이름의 공연장이 존재합니다.");
        }

        if (areaCreateVos == null || areaCreateVos.isEmpty()) {
            throw HallExceptionFactory.hallBadRequest("좌석 생성 정보는 필수입니다.");
        }
        HallEntity hall = hallMapper.requestToEntity(request);
        hallRepository.save(hall);

        // 공연장 생성 후 관리자 입력 기반 좌석 생성
        seatService.createCustomSeats(hall, areaCreateVos);

        return hall.getSq();
    }
}
