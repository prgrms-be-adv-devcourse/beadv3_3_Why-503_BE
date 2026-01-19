package io.why503.performanceservice.domain.seat.service;

import io.why503.performanceservice.domain.concerthall.model.entity.ConcertHallEtt;
import io.why503.performanceservice.domain.seat.repository.SeatRepo;
import io.why503.performanceservice.domain.seat.model.dto.cmd.SeatAreaCreateCmd;
import io.why503.performanceservice.domain.seat.model.entity.SeatEtt;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class SeatSvImpl implements SeatSv {

    private final SeatRepo seatRepo;

    @Override
    public List<SeatEtt> findByConcertHall(Long concertHallSq) {
        return seatRepo
                .findAllByConcertHall_ConcertHallSqOrderBySeatAreaAscAreaSeatNoAsc(
                        concertHallSq
                );
    }

    /**
     * 관리자 입력 기반 커스텀 좌석 생성
     */
    @Override
    @Transactional
    public void createCustomSeats(
            ConcertHallEtt concertHall,
            List<SeatAreaCreateCmd> areaCreateCmds
    ) {
        List<SeatEtt> seats = new ArrayList<>();
        int globalSeatNo = 1;

        for (SeatAreaCreateCmd cmd : areaCreateCmds) {

            validateAreaCmd(cmd);

            for (int num = 1; num <= cmd.getSeatCount(); num++) {
                seats.add(new SeatEtt(
                        concertHall,
                        cmd.getSeatArea(),
                        num,
                        globalSeatNo++
                ));
            }
        }

        try {
            seatRepo.saveAll(seats);
        } catch (DataIntegrityViolationException e) {
            log.error("커스텀 좌석 생성 중 중복 오류 발생", e);
            throw e;
        }
    }

    /* =======================
       private validation
       ======================= */

    private void validateAreaCmd(SeatAreaCreateCmd cmd) {
        if (cmd.getSeatArea() == null || cmd.getSeatArea().isBlank()) {
            throw new IllegalArgumentException("seatArea is required");
        }

        if (cmd.getSeatCount() <= 0) {
            throw new IllegalArgumentException("seatCount must be greater than 0");
        }
    }
}
