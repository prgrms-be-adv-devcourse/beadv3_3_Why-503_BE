package io.why503.performanceservice.domain.showseat.service;

import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;

import java.util.List;

public interface ShowSeatService {
    //공연 좌석 일괄 저장
    void saveAll(List<ShowSeatEntity> showSeats);
    //특정 공연의 좌석 조회
    List<ShowSeatEntity> getByShow(Long showSq);
    //좌석 등급 변경
    void changeGrade(Long userSq, Long showSeatSq, ShowSeatGrade grade);
    //좌석 가격 변경
    void changePrice(Long userSq, Long showSeatSq, Long price);
    //공연 식별자로 좌석 조회
    List<ShowSeatEntity> getSeatsByShowSq(Long showSq);
    int changePriceByShowAndGrade(Long userSq, Long showSq, ShowSeatGrade grade, Long price);
}
