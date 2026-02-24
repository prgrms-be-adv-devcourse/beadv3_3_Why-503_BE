package io.why503.aiservice.global.client.entity.mapper;

import io.why503.aiservice.domain.ai.model.embedding.Booking;
import io.why503.aiservice.global.client.dto.response.BookingResponse;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public Booking BookingResponseToBooking(BookingResponse r) {
        return new Booking(
                r.sq(),
                r.userSq(),
                r.status(),
                r.category(),
                r.genre()
        );
    }
}