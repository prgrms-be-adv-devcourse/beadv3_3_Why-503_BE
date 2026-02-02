package io.why503.performanceservice.domain.showseat.service.impl;

import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;
import io.why503.performanceservice.domain.showseat.repository.ShowSeatRepository;
import io.why503.performanceservice.domain.showseat.service.ShowSeatService;
import io.why503.performanceservice.global.error.ErrorCode;
import io.why503.performanceservice.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowSeatServiceImpl implements ShowSeatService {

    private final ShowSeatRepository showSeatRepo;

    @Override
    @Transactional
    public void saveAll(List<ShowSeatEntity> showSeats) {
        showSeatRepo.saveAll(showSeats);
    }

    @Override
    public List<ShowSeatEntity> getByShow(Long showSq) {
        return showSeatRepo.findByShow_Sq(showSq);
    }

    @Override
    @Transactional
    public void changeGrade(Long showSeatSq, ShowSeatGrade grade) {
        ShowSeatEntity showSeat = showSeatRepo.findById(showSeatSq)
                .orElseThrow(() -> new BusinessException(ErrorCode.SEAT_NOT_FOUND));
        showSeat.changeGrade(grade);
    }

    @Override
    @Transactional
    public void changePrice(Long showSeatSq, int price) {
        ShowSeatEntity showSeat = showSeatRepo.findById(showSeatSq)
                .orElseThrow(() -> new BusinessException(ErrorCode.SEAT_NOT_FOUND));
        showSeat.changePrice(price);
    }
}
