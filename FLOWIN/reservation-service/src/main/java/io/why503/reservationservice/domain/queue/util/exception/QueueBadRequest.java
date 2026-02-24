package io.why503.reservationservice.domain.queue.util.exception;
// 이거 0.0.5 바뀌면 Wait-> Queue로 이름 변경해야함
import io.why503.commonbase.exception.reservation.domain.ReservationWaitException;
import org.springframework.http.HttpStatus;

public class QueueBadRequest extends ReservationWaitException {

    public QueueBadRequest(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }

    public QueueBadRequest(Throwable cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}