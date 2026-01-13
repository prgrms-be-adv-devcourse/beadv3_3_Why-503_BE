package io.why503.performanceservice.domain.seat.sv;

import io.why503.performanceservice.domain.concert_hall.model.Ett.ConcertHallEtt;
import io.why503.performanceservice.domain.seat.model.Ett.SeatEtt;
import io.why503.performanceservice.domain.seat.repo.SeatRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class SeatSvImpl implements SeatSv {
    private SeatRepo seatRepo;

    @Override
    @Transactional
    public Optional create(ConcertHallEtt concertHall) {
        List<SeatEtt> seats = new ArrayList<>();
        int areaCount = 10;       // A~J
        int seatPerArea = 100;    // 구역당 100석

        for (int i = 0; i < areaCount; i++) {
            String area = String.valueOf((char)('A' + i));
            for (int j = 1; j <= seatPerArea; j++) {
                if (!exists(concertHall, area, j)) {
                    seats.add(
                            SeatEtt.builder()
                                    .seatArea(area)
                                    .seatNo(j)
                                    .areaSeatNo(j)
                                    .concertHall(concertHall)
                                    .build()
                    );
                }
            }
        }

        try {
            seatRepo.saveAll(seats);
        } catch (DataIntegrityViolationException e) {
            log.error("좌석 생성 중 중복 또는 DB 오류", e);
            throw new RuntimeException("좌석 생성 실패", e);
        }
    }

    @Override
    @Transactional
    public Optional adjust(ConcertHallEtt concertHall, int newSeatScale) {
        int currentSeatCount = seatRepo.count(concertHall);
        int diff = newSeatScale - currentSeatCount;

        // 좌석 증가
        if (diff > 0) {
            int lastSeatNo = seatRepo.MaxSeatNo(concertHall);
            List<SeatEtt> newSeats = new ArrayList<>();
            for (int i = 1; i <= diff; i++) {
                newSeats.add(
                        SeatEtt.builder()
                                .seatArea("EXTRA")
                                .seatNo(lastSeatNo + i)
                                .areaSeatNo(lastSeatNo + i)
                                .concertHall(concertHall)
                                .build()
                );
            }
            seatRepo.saveAll(newSeats);
        }

        // 좌석 감소 (예약 좌석 제외)
        if (diff < 0) {
            List<SeatEtt> deletableSeats =
                    seatRepo.findSeats(concertHall, Math.abs(diff));
            seatRepo.deleteAll(deletableSeats);
        }
    }

    @Override
    public boolean exists(ConcertHallEtt concertHall, String seatArea, int seatNo) {
        return seatRepo.exists(concertHall, seatArea, seatNo);
    }
}
