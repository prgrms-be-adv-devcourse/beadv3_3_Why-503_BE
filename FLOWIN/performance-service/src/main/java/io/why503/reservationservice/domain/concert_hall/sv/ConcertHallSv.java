package io.why503.performanceservice.domain.concert_hall.sv;

import io.why503.performanceservice.domain.concert_hall.model.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ConcertHallSv {

    ConcertHallRegisterRes register(
            ConcertHallRegisterReq concertHallRegisterReq
    );

    List<ConcertHallFindRes> findAll();

    ConcertHallFindRes findById(Long id);

    ConcertHallUpdateRes update(
            Long id,
            ConcertHallUpdateReq concertHallUpdateReq
    );

    List<ConcertHallFindRes> findByStat(String stat);
}

