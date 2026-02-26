package io.why503.aiservice.global.client.reservation;

import io.why503.aiservice.global.client.reservation.model.dto.response.BookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient (name = "reservation-service", url = "http://localhost:8400")
public interface ReservationClient {


    @GetMapping("/bookings/ai")
    List<BookingResponse> findMyBookings(
            @RequestHeader("X-USER-SQ") Long userSq
    );
}
