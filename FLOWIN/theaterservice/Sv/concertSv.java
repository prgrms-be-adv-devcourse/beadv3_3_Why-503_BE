package io.why503.theaterservice.Sv;

import io.why503.theaterservice.Repo.concertRepo;
import io.why503.theaterservice.Repo.hallRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class concertSv {
    private final hallRepo hallRepo;
    private final concertRepo concertRepo;
}
