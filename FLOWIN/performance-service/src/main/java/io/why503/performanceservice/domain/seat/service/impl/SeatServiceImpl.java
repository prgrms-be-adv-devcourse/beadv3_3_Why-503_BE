package io.why503.performanceservice.domain.seat.service.impl;

import io.why503.performanceservice.domain.hall.model.entity.HallEntity;
import io.why503.performanceservice.domain.seat.model.dto.response.SeatResponse;
import io.why503.performanceservice.domain.seat.model.dto.vo.SeatAreaCreateVo;
import io.why503.performanceservice.domain.seat.repository.SeatRepository;
import io.why503.performanceservice.domain.seat.model.entity.SeatEntity;
import io.why503.performanceservice.domain.seat.service.SeatService;
import io.why503.performanceservice.global.error.ErrorCode;
import io.why503.performanceservice.global.error.exception.BusinessException;
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
        return seatRepository.findAllByHallSqOrderByAreaAscNumInAreaAsc(hallSq);
    }

    @Override
    public List<SeatResponse> readByHall(Long hallSq) {
        return findByHall(hallSq).stream()
                .map(i -> seatMapper.entityToResponse(i))
                .toList();
    }

    /**
     * 관리자 입력 기반 커스텀 좌석 생성
     */
    @Override
    @Transactional
    public void createCustomSeats(
            HallEntity hall,
            List<SeatAreaCreateVo> areaCreateVos
    ) {
        List<SeatEntity> seats = new ArrayList<>();
        int globalSeatNo = 1;

        for (SeatAreaCreateVo vo : areaCreateVos) {

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

    /* =======================
       private validation
       ======================= */

    private void validateAreaVo(SeatAreaCreateVo vo) {
        if (vo.seatArea() == null || vo.seatArea().isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);        }

        if (vo.seatCount() <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);        }
    }
}
