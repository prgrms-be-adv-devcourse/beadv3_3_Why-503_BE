package io.why503.performanceservice.domain.roundSeats.service;


import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatRequestDto;
import io.why503.performanceservice.domain.roundSeats.model.dto.RoundSeatResponseDto;
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
    private final RoundSeatMapper roundSeatMapper;

    //회차 좌석 생성
    @Transactional
    public RoundSeatResponseDto createRoundSeat(RoundSeatRequestDto request){
        RoundSeatEntity entity = roundSeatMapper.dtoToEntity(request);
        RoundSeatEntity savedEntity = roundSeatRepository.save(entity);

        return roundSeatMapper.entityToDto(savedEntity);
    }


    //전체 조회
    public List<RoundSeatResponseDto> getRoundSeatList(Long roundSq) {
        List<RoundSeatEntity> entities = roundSeatRepository.findByRoundSq(roundSq);
        return convertToDtoList(entities);
    }


    //예매 가능 좌석 조회
    public List<RoundSeatResponseDto> getAvailableRoundSeatList(Long roundSq){
        List<RoundSeatEntity> entities = roundSeatRepository.findByRoundSqAndRoundSeatStatus(
                roundSq, RoundSeatStatus.AVAILABLE
        );
        return convertToDtoList(entities);
    }


    //상태 변경
    @Transactional
    public RoundSeatResponseDto patchRoundSeatStatus(Long roundSeatSq, RoundSeatStatus newStatus){
        RoundSeatEntity entity = roundSeatRepository.findById(roundSeatSq)
                //존재하지 않는 데이터 조회시
                .orElseThrow(()-> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));

        entity.updateStatus(newStatus);
        return roundSeatMapper.entityToDto(entity);

    }



    //Entity 리스트 -> DTO 리스트 변환기
    private List<RoundSeatResponseDto> convertToDtoList(List<RoundSeatEntity> entities) {
        List<RoundSeatResponseDto> dtoList = new ArrayList<>();

        for (RoundSeatEntity entity : entities) {
            dtoList.add(roundSeatMapper.entityToDto(entity));
        }

        return dtoList;
    }

}
