package io.why503.performanceservice.domain.show.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import io.why503.performanceservice.domain.show.model.dto.ShowCreateWithSeatPolicyReqDto;
import io.why503.performanceservice.domain.show.model.dto.ShowReqDto;
import io.why503.performanceservice.domain.show.model.dto.ShowResDto;
import io.why503.performanceservice.domain.show.model.entity.ShowEtt;
import io.why503.performanceservice.domain.show.model.enums.ShowCategory;
import io.why503.performanceservice.domain.show.model.enums.ShowStatus;
import io.why503.performanceservice.domain.show.repository.ShowRepo;

import io.why503.performanceservice.domain.seat.model.entity.SeatEtt;
import io.why503.performanceservice.domain.seat.repository.SeatRepo;

import io.why503.performanceservice.domain.showseat.model.dto.SeatPolicyReqDto;
import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;
import io.why503.performanceservice.domain.showseat.service.ShowSeatService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowSvImpl implements ShowSv {

    private final ShowRepo showRepo;
    private final SeatRepo seatRepo;
    private final ShowSeatService showSeatService;

    /**
     * 공연 + 좌석 정책(show_seat) 동시 생성 (seatArea 기준)
     */
    @Override
    @Transactional
    public Long createShowWithSeats(ShowCreateWithSeatPolicyReqDto req) {

        /* =======================
         * 1. 공연(Show) 생성
         * ======================= */
        ShowReqDto showReq = req.getShow();

        ShowCategory category = ShowCategory.fromCode(showReq.getCategory());

        ShowEtt show = ShowEtt.builder()
                .showName(showReq.getShowName())
                .startDate(showReq.getStartDate())
                .endDate(showReq.getEndDate())
                .openDt(showReq.getOpenDt())
                .showTime(showReq.getShowTime())
                .viewingAge(showReq.getViewingAge())
                .concertHallSq(showReq.getConcertHallSq())
                .companySq(showReq.getCompanySq())
                .build();

        show.setCategory(category);
        show.setShowStatus(ShowStatus.SCHEDULED);

        ShowEtt savedShow = showRepo.save(show);

        /* =======================
         * 2. show_seat 생성 (seatArea 기준)
         * ======================= */
        List<ShowSeatEntity> showSeats = req.getSeatPolicies().stream()
                .flatMap(policy ->
                        toShowSeatsByArea(savedShow, policy).stream()
                )
                .toList();

        showSeatService.saveAll(showSeats);

        return savedShow.getShowSq();
    }

    /**
     * 공연 단독 등록
     */
    @Override
    @Transactional
    public ShowResDto createShow(ShowReqDto reqDto) {

        ShowCategory category = ShowCategory.fromCode(reqDto.getCategory());

        ShowEtt show = ShowEtt.builder()
                .showName(reqDto.getShowName())
                .startDate(reqDto.getStartDate())
                .endDate(reqDto.getEndDate())
                .openDt(reqDto.getOpenDt())
                .showTime(reqDto.getShowTime())
                .viewingAge(reqDto.getViewingAge())
                .concertHallSq(reqDto.getConcertHallSq())
                .companySq(reqDto.getCompanySq())
                .build();

        show.setCategory(category);
        show.setShowStatus(ShowStatus.SCHEDULED);

        ShowEtt saved = showRepo.save(show);

        return ShowResDto.builder()
                .showSq(saved.getShowSq())
                .showName(saved.getShowName())
                .startDate(saved.getStartDate())
                .endDate(saved.getEndDate())
                .openDt(saved.getOpenDt())
                .showTime(saved.getShowTime())
                .viewingAge(saved.getViewingAge())
                .category(saved.getCategoryEnum())
                .showStat(saved.getShowStatus())
                .concertHallSq(saved.getConcertHallSq())
                .companySq(saved.getCompanySq())
                .build();
    }

    /**
     * 공연 단건 조회
     */
    @Override
    public ShowResDto getShow(Long showSq) {

        ShowEtt show = showRepo.findById(showSq)
                .orElseThrow(() -> new IllegalArgumentException("show not found"));

        return ShowResDto.builder()
                .showSq(show.getShowSq())
                .showName(show.getShowName())
                .startDate(show.getStartDate())
                .endDate(show.getEndDate())
                .openDt(show.getOpenDt())
                .showTime(show.getShowTime())
                .viewingAge(show.getViewingAge())
                .category(show.getCategoryEnum())
                .showStat(show.getShowStatus())
                .concertHallSq(show.getConcertHallSq())
                .companySq(show.getCompanySq())
                .build();
    }

    /**
     * seatArea 기반 show_seat 생성
     */
    private List<ShowSeatEntity> toShowSeatsByArea(
            ShowEtt show,
            SeatPolicyReqDto policy
    ) {
        List<SeatEtt> seats = seatRepo.findByConcertHall_ConcertHallSqAndSeatArea(
                show.getConcertHallSq(),
                policy.getSeatArea()
        );

        if (seats.isEmpty()) {
            throw new IllegalArgumentException(
                    "seat area not found: " + policy.getSeatArea()
            );
        }

        ShowSeatGrade grade = ShowSeatGrade.valueOf(policy.getGrade());

        return seats.stream()
                .map(seat ->
                        new ShowSeatEntity(
                                show,
                                seat,
                                grade,
                                policy.getPrice()
                        )
                )
                .toList();
    }
}
