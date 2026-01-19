package io.why503.performanceservice.domain.roundSeats.service;


import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.round.repository.RoundRepository;
import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatRequest;
import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatResponse;
import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatStatus;
import io.why503.performanceservice.domain.roundSeats.model.entity.RoundSeatEntity;
import io.why503.performanceservice.domain.roundSeats.model.mapper.RoundSeatMapper;
import io.why503.performanceservice.domain.roundSeats.repository.RoundSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoundSeatService {

    private final RoundSeatRepository roundSeatRepository;
    private final RoundRepository roundRepository; // 추가됨
    private final RoundSeatMapper roundSeatMapper;

    //회차 좌석 생성
    @Transactional
    public RoundSeatResponse createRoundSeat(RoundSeatRequest request){
        // FK 연동을 위해 RoundEntity 조회
        RoundEntity roundEntity = roundRepository.findById(request.roundSq())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다."));

        // 조회한 roundEntity를 Mapper에 전달
        RoundSeatEntity entity = roundSeatMapper.dtoToEntity(request, roundEntity);
        RoundSeatEntity savedEntity = roundSeatRepository.save(entity);

        return roundSeatMapper.entityToDto(savedEntity);
    }


    //전체 조회
    public List<RoundSeatResponse> getRoundSeatList(Long roundSq) {
        // Repository 메서드명 변경
        // RoundSeaEntity에서 roundSq를 찾고 RoundEntity에서 roundSq와 일치하는 것을 찾아라
        List<RoundSeatEntity> entities = roundSeatRepository.findByRoundSq_RoundSq(roundSq);
        return convertToDtoList(entities);
    }


    //예매 가능 좌석 조회
    public List<RoundSeatResponse> getAvailableRoundSeatList(Long roundSq){
        // Repository 메서드명 변경
        List<RoundSeatEntity> entities = roundSeatRepository.findByRoundSq_RoundSqAndRoundSeatStatus(
                roundSq, RoundSeatStatus.AVAILABLE
        );
        return convertToDtoList(entities);
    }


    //상태 변경
    @Transactional
    public RoundSeatResponse patchRoundSeatStatus(Long roundSeatSq, RoundSeatStatus newStatus){
        RoundSeatEntity entity = roundSeatRepository.findById(roundSeatSq)
                //존재하지 않는 데이터 조회시
                .orElseThrow(()-> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));

        entity.updateStatus(newStatus);
        return roundSeatMapper.entityToDto(entity);

    }



    //Entity 리스트 -> DTO 리스트 변환기
    private List<RoundSeatResponse> convertToDtoList(List<RoundSeatEntity> entities) {
        List<RoundSeatResponse> dtoList = new ArrayList<>();

        for (RoundSeatEntity entity : entities) {
            dtoList.add(roundSeatMapper.entityToDto(entity));
        }

        return dtoList;
    }

}