/**
 * Concert Hall Service Implementation
 * 공연장 관련 비즈니스 로직 구현체
 *
 * 처리 내용 :
 * - 공연장 등록
 * - 공연장 조회
 * - 공연장 등록 시 좌석 자동 생성
 *
 * 주의 사항 :
 * - 좌석은 공연장에 종속된 고정 자원
 * - 좌석 생성 실패 시 공연장 생성도 롤백되어야 함
 */
package io.why503.performanceservice.domain.concerthall.Sv;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallReqDto;
import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallResDto;
import io.why503.performanceservice.domain.concerthall.Model.Ett.ConcertHallEtt;
import io.why503.performanceservice.domain.concerthall.Repo.ConcertHallRepo;
import io.why503.performanceservice.domain.seat.Model.Dto.Cmd.SeatAreaCreateCmd;
import io.why503.performanceservice.domain.seat.Sv.SeatSv;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Transactional(readOnly = true)
public class ConcertHallSvImpl implements ConcertHallSv {

    private final ConcertHallRepo concertHallRepo;
    private final SeatSv seatSv;

    /**
     * 공연장 등록 (좌석 생성 없음)
     */
    @Override
    @Transactional
    public void createConcertHall(ConcertHallReqDto reqDto) {

        ConcertHallEtt hall = ConcertHallEtt.builder()
                .concertHallName(reqDto.getConcertHallName())
                .concertHallPost(reqDto.getConcertHallPost())
                .concertHallBasicAddr(reqDto.getConcertHallBasicAddr())
                .concertHallDetailAddr(reqDto.getConcertHallDetailAddr())
                .concertHallStat(reqDto.getConcertHallStat())
                .concertHallSeatScale(reqDto.getConcertHallSeatScale())
                .concertHallStructure(reqDto.getConcertHallStructure())
                .concertHallLatitude(reqDto.getConcertHallLatitude())
                .concertHallLongitude(reqDto.getConcertHallLongitude())
                .build();

        concertHallRepo.save(hall);
    }

    /**
     * 공연장 단건 조회
     */
    @Override
    public ConcertHallResDto getConcertHall(Long concertHallSq) {

        ConcertHallEtt hall = concertHallRepo.findById(concertHallSq)
                .orElseThrow(() -> new IllegalArgumentException("concert hall not found"));

        return ConcertHallResDto.builder()
                .concertHallSq(hall.getConcertHallSq())
                .concertHallName(hall.getConcertHallName())
                .concertHallPost(hall.getConcertHallPost())
                .concertHallBasicAddr(hall.getConcertHallBasicAddr())
                .concertHallDetailAddr(hall.getConcertHallDetailAddr())
                .concertHallStat(hall.getConcertHallStat())
                .concertHallSeatScale(hall.getConcertHallSeatScale())
                .concertHallStructure(hall.getConcertHallStructure())
                .concertHallLatitude(hall.getConcertHallLatitude())
                .concertHallLongitude(hall.getConcertHallLongitude())
                .build();
    }

    /**
     * 관리자 입력 기반 좌석 생성 공연장 등록
     */
    @Override
    @Transactional
    public Long createWithCustomSeats(
            ConcertHallReqDto reqDto,
            List<SeatAreaCreateCmd> seatAreaCmds
    ) {

        ConcertHallEtt hall = concertHallRepo.save(
                ConcertHallEtt.builder()
                        .concertHallName(reqDto.getConcertHallName())
                        .concertHallPost(reqDto.getConcertHallPost())
                        .concertHallBasicAddr(reqDto.getConcertHallBasicAddr())
                        .concertHallDetailAddr(reqDto.getConcertHallDetailAddr())
                        .concertHallStat(reqDto.getConcertHallStat())
                        .concertHallSeatScale(reqDto.getConcertHallSeatScale())
                        .concertHallStructure(reqDto.getConcertHallStructure())
                        .concertHallLatitude(reqDto.getConcertHallLatitude())
                        .concertHallLongitude(reqDto.getConcertHallLongitude())
                        .build()
        );

        // 공연장 생성 후 관리자 입력 기반 좌석 생성
        seatSv.createCustomSeats(hall, seatAreaCmds);

        return hall.getConcertHallSq();
    }
}
