package io.why503.performanceservice.domain.seat.service.impl;

import io.why503.performanceservice.domain.hall.model.entity.HallEntity;
import io.why503.performanceservice.domain.seat.model.dto.response.SeatResponse;
import io.why503.performanceservice.domain.seat.model.dto.vo.SeatAreaCreateVo;
import io.why503.performanceservice.domain.seat.repository.SeatRepository;
import io.why503.performanceservice.domain.seat.model.entity.SeatEntity;
import io.why503.performanceservice.domain.seat.service.SeatService;
import io.why503.performanceservice.domain.seat.util.SeatExceptionFactory;
import io.why503.performanceservice.util.mapper.SeatMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;

    private List<SeatEntity> findByHall(Long hallSq) {
        return seatRepository.findAllByHall_SqOrderByAreaAscNumInAreaAsc(hallSq);
    }

    @Override
    public List<SeatResponse> readByHall(Long hallSq) {
        return findByHall(hallSq).stream()
                .map(i -> seatMapper.entityToResponse(i))
                .toList();
    }

    // 관리자 입력 기반 커스텀 좌석 생성
    @Override
    @Transactional
    public void createCustomSeats(
            HallEntity hall,
            List<SeatAreaCreateVo> areaCreateVos
    ) {
        List<SeatEntity> seats = new ArrayList<>();
        int globalSeatNo = 1;

        for (SeatAreaCreateVo vo : areaCreateVos) {

            // 좌석 생성 요청 검증
            validateAreaVo(vo);

            for (int num = 1; num <= vo.seatCount(); num++) {
                seats.add(SeatEntity.builder()
                        .num(globalSeatNo++)
                        .area(vo.seatArea())
                        .numInArea(num)
                        .hall(hall)
                        .build());
            }
        }

        seatRepository.saveAll(seats);
    }

    // 좌석 생성 요청 값 검증
    private void validateAreaVo(SeatAreaCreateVo vo) {

        // 구역명이 비어있음
        if (vo.seatArea() == null || vo.seatArea().isBlank()) {
            throw SeatExceptionFactory.seatBadRequest(
                        "좌석 구역 값이 비어있습니다."
            );        
        }

        // 좌석 개수가 0 이하
        if (vo.seatCount() <= 0) {
            throw SeatExceptionFactory.seatBadRequest(
                "좌석 개수(seatCount)는 1 이상이어야 합니다."
            );
        }
    }
}
