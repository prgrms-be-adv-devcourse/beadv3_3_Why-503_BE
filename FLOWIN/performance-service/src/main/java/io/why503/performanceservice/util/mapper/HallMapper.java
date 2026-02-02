package io.why503.performanceservice.util.mapper;

import io.why503.performanceservice.domain.hall.model.dto.request.HallRequest;
import io.why503.performanceservice.domain.hall.model.dto.response.HallResponse;
import io.why503.performanceservice.domain.hall.model.entity.HallEntity;
import org.springframework.stereotype.Component;

@Component
public class HallMapper {
    public HallEntity requestToEntity(HallRequest request){
        return HallEntity.builder()
                .name(request.hallName())
                .post(request.hallPost())
                .basicAddr(request.hallBasicAddr())
                .detailAddr(request.hallDetailAddr())
                .status(request.hallStatus())
                .seatScale(request.hallSeatScale())
                .structure(request.hallStructure())
                .latitude(request.hallLatitude())
                .longitude(request.hallLongitude())
                .build();
    }
    public HallResponse entityToResponse(HallEntity entity){
        return new HallResponse(
                entity.getSq(),
                entity.getName(),
                entity.getPost(),
                entity.getBasicAddr(),
                entity.getDetailAddr(),
                entity.getHallStatus(),
                entity.getSeatScale(),
                entity.getStructure(),
                entity.getLatitude(),
                entity.getLongitude()
        );
    }
}
