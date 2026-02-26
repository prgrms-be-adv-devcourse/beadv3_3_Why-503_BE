package io.why503.performanceservice.domain.showseat.service.impl;

import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;
import io.why503.performanceservice.domain.showseat.repository.ShowSeatRepository;
import io.why503.performanceservice.domain.showseat.service.ShowSeatService;
import io.why503.performanceservice.domain.showseat.util.ShowSeatExceptionFactory;
import io.why503.performanceservice.global.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowSeatServiceImpl implements ShowSeatService {

    private final ShowSeatRepository showSeatRepository;
    private final UserValidator userValidator;

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
    public void changeGrade(Long userSq, Long showSeatSq, ShowSeatGrade grade) {
        userValidator.validateEnterprise(
            userSq,
            ShowSeatExceptionFactory.showSeatForbidden("기업 또는 관리자만 좌석 등급을 변경할 수 있습니다.")
        );
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
    public void changePrice(Long userSq, Long showSeatSq, Long price) {
        userValidator.validateEnterprise(
            userSq,
            ShowSeatExceptionFactory.showSeatForbidden("기업 또는 관리자만 좌석 등급을 변경할 수 있습니다.")
        );
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

    @Transactional
    public int changePriceByShowAndGrade(Long userSq, Long showSq, ShowSeatGrade grade, Long price) {
        userValidator.validateEnterprise(
            userSq,
            ShowSeatExceptionFactory.showSeatForbidden("기업 또는 관리자만 좌석 등급을 변경할 수 있습니다.")
        );
        int updated = showSeatRepository.updatePriceByShowAndGrade(showSq, grade, price);
        if (updated == 0) {
            // 해당 공연에 그 등급 좌석이 없거나 showSq가 잘못됨
            throw ShowSeatExceptionFactory.showSeatNotFound("No seats found for showSq=" + showSq + ", grade=" + grade);
        }
        return updated;
    }

}
