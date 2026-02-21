package io.why503.reservationservice.domain.booking.model.dto.response;

// 예매 진입 응답
public record BookingEnterResponse (
    boolean canEnter,       // true면 즉시 진입 가능
    String status,          // ENTER | WAITING | CLOSE
    String entryToken,      // ENTER일때만 존재하도록  
    Long waitingPosition
){
    public static BookingEnterResponse enter(String entryToken) {
        return new BookingEnterResponse(true, "ENTER", entryToken, null);
    }   

    public static BookingEnterResponse waiting(Long position) {
        return new BookingEnterResponse(false, "WAITING", null, position);
    }

    public static BookingEnterResponse closed() {
        return new BookingEnterResponse(false, "CLOSED", null, null);
    }
}
