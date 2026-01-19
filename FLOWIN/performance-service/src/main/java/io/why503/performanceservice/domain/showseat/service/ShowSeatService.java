package io.why503.performanceservice.domain.showseat.service;

import java.util.List;

import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;

public interface ShowSeatService {

    void saveAll(List<ShowSeatEntity> showSeats);

    List<ShowSeatEntity> getByShow(Long showSq);

    void changeGrade(Long showSeatSq, ShowSeatGrade grade);

    void changePrice(Long showSeatSq, int price);
}
