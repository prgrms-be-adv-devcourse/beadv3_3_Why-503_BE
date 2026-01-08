package io.why503.theaterservice.service;

import io.why503.theaterservice.Repository.concertRepository;
import io.why503.theaterservice.Repository.hallRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class concertService {
    private final hallRepository hallRepository;
    private final concertRepository concertRepository;
}
