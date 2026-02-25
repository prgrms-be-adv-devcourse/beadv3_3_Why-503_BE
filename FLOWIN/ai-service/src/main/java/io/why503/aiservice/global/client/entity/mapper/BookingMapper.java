package io.why503.aiservice.global.client.entity.mapper;

import io.why503.aiservice.global.client.dto.response.Booking;
import io.why503.aiservice.global.client.dto.response.BookingResponse;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public Booking responseToBooking(BookingResponse r) {
        return new Booking(
                r.category(),
                r.genre()
        );
    }
}