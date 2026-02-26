package io.why503.reservationservice.global.client.dto.request;
import java.util.List;
public record SeatReserveRequest(
        List<Long> roundSeatSqs
) {
    
}
