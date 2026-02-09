package io.why503.performanceservice.domain.showseat.service.impl;

import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;
import io.why503.performanceservice.domain.showseat.repository.ShowSeatRepository;
import io.why503.performanceservice.domain.showseat.service.ShowSeatService;
import io.why503.performanceservice.domain.showseat.util.ShowSeatExceptionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowSeatServiceImpl implements ShowSeatService {

    private final ShowSeatRepository showSeatRepository;

    //공연 좌석 일괄 저장
    @Override
    @Transactional
    public void saveAll(List<ShowSeatEntity> showSeats) {
        showSeatRepository.saveAll(showSeats);
    }

    //특정 공연의 좌석 조회
    @Override
    public List<ShowSeatEntity> getByShow(Long showSq) {
        return showSeatRepository.findByShow_Sq(showSq);
    }

    //좌석 등급 변경
    @Override
    @Transactional
    public void changeGrade(Long showSeatSq, ShowSeatGrade grade) {
        //좌석 등급 여부
        if (grade == null) {
            throw ShowSeatExceptionFactory.showSeatBadRequest("변경할 좌석 등급은 필수입니다.");
        }

        //좌석 조회
        ShowSeatEntity showSeat = showSeatRepository.findById(showSeatSq)
                .orElseThrow(() -> ShowSeatExceptionFactory.showSeatNotFound("좌석이 존재하지 않습니다."));
        //등급 변경
        showSeat.changeGrade(grade);
    }

    //좌석 가격 변경
    @Override
    @Transactional
    public void changePrice(Long showSeatSq, Long price) {
        //가격 유효성 검사
        if (price == null || price < 0) {
            throw ShowSeatExceptionFactory.showSeatBadRequest("좌석 가격은 0원 이상이어야 합니다.");
        }

        ShowSeatEntity showSeat = showSeatRepository.findById(showSeatSq)
                .orElseThrow(() -> ShowSeatExceptionFactory.showSeatNotFound("좌석이 존재하지 않습니다."));
        //가격 변경
        showSeat.changePrice(price);
    }

    //공연 식별자로 좌석 목록 조회, RoundService에서 회차 좌석 자동생성 시 사용
    @Override
    public List<ShowSeatEntity> getSeatsByShowSq(Long showSq) {
        return showSeatRepository.findByShow_Sq(showSq);
    }

}
