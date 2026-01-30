package io.why503.performanceservice.util.mapper;

import io.why503.performanceservice.domain.concerthall.model.dto.request.ConcertHallRequest;
import io.why503.performanceservice.domain.concerthall.model.dto.response.ConcertHallResponse;
import io.why503.performanceservice.domain.concerthall.model.entity.ConcertHallEntity;
import org.springframework.stereotype.Component;

@Component
public class ConcertHallMapper {
    public ConcertHallEntity requestToEntity(ConcertHallRequest request){
        return ConcertHallEntity.builder()
                .name(request.concertHallName())
                .post(request.concertHallPost())
                .basicAddr(request.concertHallBasicAddr())
                .detailAddr(request.concertHallDetailAddr())
                .status(request.concertHallStatus())
                .seatScale(request.concertHallSeatScale())
                .structure(request.concertHallStructure())
                .latitude(request.concertHallLatitude())
                .longitude(request.concertHallLongitude())
                .build();
    }
    public ConcertHallResponse entityToResponse(ConcertHallEntity entity){
        return new ConcertHallResponse(
                entity.getSq(),
                entity.getName(),
                entity.getPost(),
                entity.getBasicAddr(),
                entity.getDetailAddr(),
                entity.getConcertHallStatus(),
                entity.getSeatScale(),
                entity.getStructure(),
                entity.getLatitude(),
                entity.getLongitude()
        );
    }
}
