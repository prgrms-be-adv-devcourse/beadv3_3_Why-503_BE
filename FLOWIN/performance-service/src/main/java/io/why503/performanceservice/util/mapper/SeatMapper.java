package io.why503.performanceservice.util.mapper;


import io.why503.performanceservice.domain.seat.model.dto.response.SeatResponse;
import io.why503.performanceservice.domain.seat.model.entity.SeatEntity;
import org.springframework.stereotype.Component;

/**
 * Seat Mapper
 * 클래스간 상태변경
 */
@Component
public class SeatMapper {
    //response to entity
    public SeatResponse entityToResponse(SeatEntity entity){
        return new SeatResponse(
                entity.getSq(),
                entity.getNum(),
                entity.getArea(),
                entity.getNumInArea()
        );
    }
}
