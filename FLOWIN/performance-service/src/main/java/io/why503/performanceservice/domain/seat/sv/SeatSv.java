package io.why503.performanceservice.domain.seat.sv;

import io.why503.performanceservice.domain.seat.model.dto.SeatFindReq;
import io.why503.performanceservice.domain.seat.model.dto.SeatFindRes;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface SeatSv {
    List<SeatFindRes> findSeats(SeatFindReq req);
}
