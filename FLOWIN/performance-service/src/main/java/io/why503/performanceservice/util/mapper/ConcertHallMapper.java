package io.why503.performanceservice.util.mapper;

import io.why503.performanceservice.domain.concerthall.model.dto.request.ConcertHallRequest;
import io.why503.performanceservice.domain.concerthall.model.dto.response.ConcertHallResponse;
import io.why503.performanceservice.domain.concerthall.model.entity.ConcertHallEntity;
import org.springframework.stereotype.Component;

@Component
public class ConcertHallMapper {
    public ConcertHallEntity requestToEntity(ConcertHallRequest request){
        return ConcertHallEntity.builder()
                .concertHallName(request.concertHallName())
                .concertHallPost(request.concertHallPost())
                .concertHallBasicAddr(request.concertHallBasicAddr())
                .concertHallDetailAddr(request.concertHallDetailAddr())
                .concertHallSeatScale(request.concertHallSeatScale())
                .concertHallStructure(request.concertHallStructure())
                .concertHallLatitude(request.concertHallLatitude())
                .concertHallLongitude(request.concertHallLongitude())
                .build();
    }
    public ConcertHallResponse entityToResponse(ConcertHallEntity entity){
        return  new ConcertHallResponse(
                entity.getConcertHallSq(),
                entity.getConcertHallName(),
                entity.getConcertHallPost(),
                entity.getConcertHallBasicAddr(),
                entity.getConcertHallDetailAddr(),
                entity.getConcertHallStatus(),
                entity.getConcertHallSeatScale(),
                entity.getConcertHallStructure(),
                entity.getConcertHallLatitude(),
                entity.getConcertHallLongitude()
        );
    }
}
