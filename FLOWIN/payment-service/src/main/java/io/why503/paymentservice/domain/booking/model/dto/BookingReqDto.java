package io.why503.paymentservice.domain.booking.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class BookingReqDto {
    private Long userSq;
    private Integer totalAmount;

    // 내부 클래스가 아니라, TicketDto 파일을 참조합니다.
    private List<TicketDto> tickets;
}