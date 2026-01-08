package io.why503.reservationservice.Domain.Showing.Sv;

import io.why503.reservationservice.Domain.Showing.Model.Dto.AreaStatusDto;
import io.why503.reservationservice.Domain.Showing.Model.Dto.SeatStatusDto;
import io.why503.reservationservice.Domain.Showing.Repo.ShowingSeatRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowingSv {

    private final ShowingSeatRepo showingSeatRepo;

    /**
     * [좌석 영역 목록 표시]
     * 특정 회차(showingSq)의 구역별 등급, 가격, 잔여 좌석 수를 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<AreaStatusDto> getAreaStatus(Long showingSq) {
        // Repo에 아까 만든 findAreaStatus 메서드를 호출합니다.
        List<AreaStatusDto> statusList = showingSeatRepo.findAreaStatus(showingSq);

        if (statusList.isEmpty()) {
            throw new IllegalArgumentException("해당 회차의 좌석 정보가 존재하지 않습니다.");
        }

        return statusList;
    }

    @Transactional(readOnly = true)
    public List<SeatStatusDto> getAllSeatStatus(Long showingSq) {
        return showingSeatRepo.findAllSeatStatusDtoByShowingSq(showingSq);
    }

    // ShowingSv.java 에 추가
    @Transactional(readOnly = true)
    public List<SeatStatusDto> getSeatStatusByArea(Long showingSq, String area) {
        return showingSeatRepo.findSeatStatusByArea(showingSq, area);
    }
}