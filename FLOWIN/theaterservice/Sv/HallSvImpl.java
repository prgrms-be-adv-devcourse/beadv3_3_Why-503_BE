package io.why503.theaterservice.Sv;

import io.why503.theaterservice.Repo.hallRepo;
import io.why503.theaterservice.model.Ett.concert_hallEtt;
import io.why503.theaterservice.model.Dto.hallReq;
import io.why503.theaterservice.model.Dto.hallRes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class hallSvImpl implements hallSv {
    private final hallRepo repo;

    @Override
    @Transactional
    public concert_hallEtt register(
            Long id,
            hallReq hallReq
    ) {
        concert_hallEtt concert = repo.findById(id).orElseThrow(
                () -> new IllegalArgumentException("concert hall with id " + id + " not found")
        );

        concert.update(hallReq);
        return repo.save(concert);
    }

    @Override
    @Transactional
    public List<concert_hallEtt> findAll() {
        return repo.findAll()
                .stream()
                .toList();
    }

    @Transactional
    public hallRes findById(Long id) {
        concert_hallEtt concert = concert_hallEtt.builder()
                .build();

        return repo.findById(concert);

    }

    @Transactional
    public concert_hallEtt updateConcert(Long id, hallReq hallReq) {
        concert_hallEtt concert = concert_hallEtt.builder()
                .name(hallReq.name())
                .post(hallReq.post())
                .basic_addr(hallReq.basic_addr())
                .detail_addr(hallReq.detail_addr())
                .build();

        return repo.save(concert);
    }



//    @Override
//    public hallResponse.from(hallRepository
//            .findById(id)
//            .orElseThrow(()-> new NotFoundException(
//                    "concert_hall not found"
//    ) ) );

}
