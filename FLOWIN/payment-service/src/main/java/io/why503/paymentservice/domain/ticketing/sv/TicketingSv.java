package io.why503.paymentservice.domain.ticketing.sv;

import io.why503.paymentservice.domain.ticketing.mapper.TicketingMapper;
import io.why503.paymentservice.domain.ticketing.model.dto.TicketingReqDto;
import io.why503.paymentservice.domain.ticketing.model.dto.TicketingResDto;
import io.why503.paymentservice.domain.ticketing.model.ett.Ticketing;
import io.why503.paymentservice.domain.ticketing.repo.TicketingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketingSv {

    private final TicketingRepo ticketingRepo;
    private final TicketingMapper ticketingMapper;

    /**
     * 예매 생성
     */
    @Transactional
    public TicketingResDto createTicketing(TicketingReqDto req) {
        // 1. DTO -> Entity 변환
        Ticketing ticketing = ticketingMapper.toEntity(req);

        // 2. 저장 (Cascade로 인해 Ticket도 자동 저장됨)
        Ticketing saved = ticketingRepo.save(ticketing);

        // 3. 결과 반환
        return TicketingResDto.from(saved);
    }

    /**
     * 예매 상세 조회
     */
    public TicketingResDto getTicketing(Long ticketingSq) {
        Ticketing ticketing = ticketingRepo.findById(ticketingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));
        return TicketingResDto.from(ticketing);
    }

    /**
     * 예매 전체 취소
     */
    @Transactional
    public void cancelTicketing(Long ticketingSq) {
        Ticketing ticketing = ticketingRepo.findById(ticketingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        // Entity 내의 비즈니스 로직 호출
        ticketing.cancel();
    }
}