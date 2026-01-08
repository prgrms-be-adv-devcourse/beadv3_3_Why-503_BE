package io.why503.theaterservice.service;

import io.why503.theaterservice.Repository.hallRepository;
import io.why503.theaterservice.model.Entity.concert_hall;
import io.why503.theaterservice.model.dto.hallRequest;
import io.why503.theaterservice.model.dto.hallResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class hallServiceImpl implements hallService {
    private final hallRepository repo;

    @Override
    @Transactional
    public concert_hall register(
            Long id,
            hallRequest hallRequest
    ) {
        concert_hall concert = repo.findById(id).orElseThrow(
                () -> new IllegalArgumentException("concert hall with id " + id + " not found")
        );

        concert.update(hallRequest);
        return repo.save(concert);
    }

    @Override
    @Transactional
    public List<concert_hall> findAll() {
        return repo.findAll()
                .stream()
                .toList();
    }

    @Transactional
    public hallResponse findById(Long id) {
        concert_hall concert = concert_hall.builder()
                .build();

        return repo.findById(concert);

    }

    @Transactional
    public concert_hall updateConcert(Long id, hallRequest hallRequest) {
        concert_hall concert = concert_hall.builder()
                .name(hallRequest.name())
                .post(hallRequest.post())
                .basic_addr(hallRequest.basic_addr())
                .detail_addr(hallRequest.detail_addr())
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
