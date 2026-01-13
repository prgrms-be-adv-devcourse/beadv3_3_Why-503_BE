package io.why503.performanceservice.domain.seat.sv;

import io.why503.performanceservice.domain.seat.model.Ett.SeatEtt;
import io.why503.performanceservice.domain.seat.model.dto.SeatFindReq;
import io.why503.performanceservice.domain.seat.model.dto.SeatFindRes;
import io.why503.performanceservice.domain.seat.repo.SeatRepo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class SeatSvImpl implements SeatSv {
    private SeatRepo seatRepo;

    @Override
    @Transactional(readOnly = true)
    public List<SeatFindRes> findSeats(SeatFindReq seatFindReq) {

        List<SeatEtt> seats;

        if (seatFindReq.seatArea() != null &&
                seatFindReq.seatNo() != null) {
            seats = seatRepo.findByConcertHallIdAndSeatAreaAndSeatNo(
                    seatFindReq.concertHallId(),
                    seatFindReq.seatArea(),
                    Math.toIntExact(seatFindReq.seatNo())
            );
        } else if (seatFindReq.seatArea() != null) {
            seats = seatRepo.findByConcertHallIdAndSeatArea(
                    seatFindReq.concertHallId(),
                    seatFindReq.seatArea()
            );
        } else {
            seats = seatRepo.findByConcertHallId(
                    seatFindReq.concertHallId()
            );
        }

        return seats.stream()
                .map(SeatFindRes::from)
                .toList();
    }


}
