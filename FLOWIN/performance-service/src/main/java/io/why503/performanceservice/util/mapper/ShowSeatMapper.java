package io.why503.performanceservice.util.mapper;

import io.why503.performanceservice.domain.showseat.model.dto.response.ShowSeatResponse;
import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShowSeatMapper {

    private final SeatMapper seatMapper; // Seat 변환은 seatMapper로

    // 단건 변환
    public ShowSeatResponse entityToResponse(ShowSeatEntity entity) {
        if (entity == null) return null;

        return new ShowSeatResponse(
                entity.getSq(),
                entity.getShow().getSq(),
                entity.getGrade(),
                entity.getPrice(),
                seatMapper.entityToResponse(entity.getSeat()) // SeatEntity -> SeatResponse 변환
        );
    }

    // 리스트 변환
    public List<ShowSeatResponse> entityListToResponseList(List<ShowSeatEntity> entities) {
        if (entities == null) return List.of();

        List<ShowSeatResponse> list = new ArrayList<>();
        for (ShowSeatEntity entity : entities) {
            ShowSeatResponse showSeatResponse = entityToResponse(entity);
            list.add(showSeatResponse);
        }
        return list;
    }
}