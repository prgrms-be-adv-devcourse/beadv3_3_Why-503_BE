package io.why503.theaterservice.service;

import io.why503.theaterservice.model.Entity.concert_hall;
import io.why503.theaterservice.model.dto.hallRequest;
import io.why503.theaterservice.model.dto.hallResponse;

import java.util.List;



public interface hallService {
    concert_hall register(Long id, hallRequest hallRequest);
    List<concert_hall> findAll();
    concert_hall updateConcert(Long id,hallRequest hallRequest);
    hallResponse findById(Long id);
}

