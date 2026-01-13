package io.why503.performanceservice.domain.concert_hall.sv;

import io.why503.performanceservice.domain.concert_hall.model.dto.*;
import io.why503.performanceservice.domain.concert_hall.repo.ConcertHallRepo;
import io.why503.performanceservice.domain.concert_hall.model.Ett.ConcertHallEtt;
import io.why503.performanceservice.domain.seat.model.Ett.SeatEtt;
import io.why503.performanceservice.domain.seat.repo.SeatRepo;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.AccessLevel;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ConcertHallSvImpl implements ConcertHallSv {

    private final ConcertHallRepo concertHallRepo;
    private final SeatRepo seatRepo;

    @Override
    @Transactional
    public ConcertHallRegisterRes register(
            ConcertHallRegisterReq req
    ) {


        //공연장 저장
        ConcertHallEtt concert = ConcertHallEtt.builder()
                .name(req.name())
                .post(req.post())
                .basicAddr(req.basicAddr())
                .detailAddr(req.detailAddr())
                .stat(req.stat())
                .structure(req.structure())
                .latitude(req.latitude())
                .longitude(req.longitude())
                .seatScale(1000)
                .build();

        concertHallRepo.save(concert);

        //좌석 생성 (A~J, 100석씩)
        List<SeatEtt> seats = new ArrayList<>();
        int areaCount = 10;
        int seatPerArea = 100;

        for (int i = 0; i < areaCount; i++) {
            String area = String.valueOf((char)('A' + i));
            for (int j = 1; j <= seatPerArea; j++) {
                seats.add(
                        SeatEtt.builder()
                                .seatArea(area)
                                .seatNo(j)
                                .areaSeatNo(Integer.parseInt(area + j))
                                .build()
                );
            }
        }

        //좌석 저장, 장애 방지 위한 예외처리
        try {
            seatRepo.saveAll(seats);
        } catch (DataIntegrityViolationException e) {
            log.error("좌석 생성 중 오류 발생", e);
            throw e;
        }

        return new ConcertHallRegisterRes(
                concert.getId(),
                concert.getName(),
                concert.getSeatScale()
        );
    }

    @Override
    public List<ConcertHallFindRes> findAll() {
        return concertHallRepo.findAll()
                .stream()
                .map(ConcertHallFindRes::from)
                .toList();
    }

    @Override
    public ConcertHallFindRes findById(Long id) {
        return concertHallRepo.findById(id)
                .map(ConcertHallFindRes::from)
                .orElseThrow(() -> new EntityNotFoundException("좌석과 공연장 번호가 해당 정보가 없습니다."));
    }

    @Override
    @Transactional
    public ConcertHallUpdateRes update(Long id, ConcertHallUpdateReq req) {
        ConcertHallEtt concert = concertHallRepo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("공연장을 찾을 수 없습니다"));

        concert.update(req);
        return ConcertHallUpdateRes.from(concert);
    }

    @Override
    public List<ConcertHallFindRes> findByStat(String stat) {
        return concertHallRepo.findByStat(stat)
                .stream()
                .map(ConcertHallFindRes::from)
                .toList();
    }
}
