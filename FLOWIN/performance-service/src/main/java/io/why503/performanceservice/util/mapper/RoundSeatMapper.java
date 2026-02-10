package io.why503.performanceservice.util.mapper;


import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.roundSeat.model.dto.request.RoundSeatRequest;
import io.why503.performanceservice.domain.roundSeat.model.dto.response.RoundSeatResponse;
import io.why503.performanceservice.domain.roundSeat.model.dto.response.SeatReserveResponse;
import io.why503.performanceservice.domain.roundSeat.model.entity.RoundSeatEntity;
import io.why503.performanceservice.domain.roundSeat.model.enums.RoundSeatStatus;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class RoundSeatMapper {


    //Request -> Entity
    // DB 저장을 위해 RoundEntity 객체를 받음
    public RoundSeatEntity requestToEntity(RoundSeatRequest request, RoundEntity roundEntity){
        return RoundSeatEntity.builder()
                .round(roundEntity)
                .showSeatSq(request.showSeatSq())
                .status(request.roundSeatStatus())
                .statusDt(LocalDateTime.now())
                .build();

    }

    //Entity -> Response
    public RoundSeatResponse entityToResponse(RoundSeatEntity entity){
        return RoundSeatResponse.builder()
                .roundSeatSq(entity.getSq())
                .roundSq(entity.getRound().getSq())
                .showSeatSq(entity.getShowSeatSq())
                .roundSeatStatus(entity.getStatus())
                .roundSeatStatusName(entity.getStatus().getDescription())
                .roundSeatStatusTime(entity.getStatusDt())
                .build();
    }
    //entity리스트를 dto리스트로 일괄 변환
    //전체, 예매가능 좌석조회에서 리스트로 바꾸는 코드 중복제거위해
    public List<RoundSeatResponse> dbToResponseList(List<RoundSeatEntity> entities) {

        List<RoundSeatResponse> responseList = new ArrayList<>();

        if (entities == null) return responseList;

        for (RoundSeatEntity entity : entities) {
            responseList.add(entityToResponse(entity));
        }

        return responseList;
    }

    public SeatReserveResponse entityToReserveResponse(RoundSeatEntity roundSeat, ShowSeatEntity showSeat, String concertHallName) {
        RoundEntity round = roundSeat.getRound();
        ShowEntity show = round.getShow();

        return SeatReserveResponse.builder()
                // RoundSeat 정보
                .roundSeatSq(roundSeat.getSq())
                .roundSeatStatus(roundSeat.getStatus().name())

                // ShowSeat 가격, 등급
                .price(showSeat.getPrice())
                .grade(showSeat.getGrade().name())

                // ShowSeat -> Seat 정보 (구역, 번호)
                .seatArea(showSeat.getSeat().getArea())
                .seatAreaNum(showSeat.getSeat().getNumInArea())

                // 공연 및 공연장 정보
                .showName(show.getName())
                .genre(show.getGenre().name())
                .hallName(concertHallName)
                .roundDt(round.getStartDt())
                .build();
    }

    // ShowSeat 리스트 -> RoundSeat 리스트 변환 (RoundServiceImpl에서 호출)
    public List<RoundSeatEntity> showSeatListToRoundSeatList(List<ShowSeatEntity> showSeats, RoundEntity round) {
        return showSeats.stream()
                .map(showSeat -> this.showSeatToRoundSeat(showSeat, round))
                .toList();
    }

    // ShowSeat -> RoundSeat 변환
    public RoundSeatEntity showSeatToRoundSeat(ShowSeatEntity showSeat, RoundEntity round) {
        return RoundSeatEntity.builder()
                .round(round)
                .showSeatSq(showSeat.getSq())
                .status(RoundSeatStatus.WAIT)
                .statusDt(LocalDateTime.now())
                .version(0L)
                .build();
    }

}
