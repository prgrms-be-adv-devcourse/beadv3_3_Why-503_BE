package io.why503.theaterservice.Sv;

import io.why503.theaterservice.model.Ett.concert_hallEtt;
import io.why503.theaterservice.model.Dto.hallReq;
import io.why503.theaterservice.model.Dto.hallRes;

import java.util.List;



public interface hallSv {
    concert_hallEtt register(Long id, hallReq hallReq);
    List<concert_hallEtt> findAll();
    concert_hallEtt updateConcert(Long id, hallReq hallReq);
    hallRes findById(Long id);
}

