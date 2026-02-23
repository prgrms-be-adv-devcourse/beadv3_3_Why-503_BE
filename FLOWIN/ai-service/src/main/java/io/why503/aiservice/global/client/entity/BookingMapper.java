package io.why503.aiservice.global.client.entity;

import io.why503.aiservice.domain.ai.model.embedding.Booking;
import io.why503.aiservice.global.client.dto.response.BookingResponse;

public interface BookingMapper {

    default Booking from(BookingResponse r){
        return new Booking(
                r.sq(),
                r.userSq(),
                r.status(),
                r.category(),
                r.genre()
        );
    };

}
