package io.why503.paymentservice.domain.ticket.service.impl;

import io.why503.paymentservice.domain.payment.model.entity.Payment;
import io.why503.paymentservice.domain.ticket.mapper.TicketMapper;
import io.why503.paymentservice.domain.ticket.model.dto.request.TicketCreateRequest;
import io.why503.paymentservice.domain.ticket.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.ticket.model.entity.Ticket;
import io.why503.paymentservice.domain.ticket.model.enums.DiscountPolicy;
import io.why503.paymentservice.domain.ticket.repository.TicketRepository;
import io.why503.paymentservice.domain.ticket.service.TicketService;
import io.why503.paymentservice.domain.ticket.util.TicketExceptionFactory;
import io.why503.paymentservice.global.client.PerformanceClient;
import io.why503.paymentservice.global.client.dto.request.SeatReserveRequest;
import io.why503.paymentservice.global.client.dto.response.BookingSeatResponse;
import io.why503.paymentservice.global.client.dto.response.RoundSeatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 티켓 발권 라이프사이클 및 소유권 전환을 관리하는 서비스 구현체
 * - 거래 결과에 따른 데이터 정합성 유지와 가격 산정 로직을 포함
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final PerformanceClient performanceClient;

    // 공연 회차 정보와 연동하여 판매 가능한 티켓 데이터 슬롯을 일괄 생성
    @Override
    @Transactional
    public void createTicketSlots(TicketCreateRequest request) {
        if (request == null || request.roundSeatSqs() == null || request.roundSeatSqs().isEmpty()) {
            throw TicketExceptionFactory.ticketBadRequest("생성할 티켓 좌석 정보가 없습니다.");
        }

        List<Long> requestedSeatSqs = request.roundSeatSqs();

        List<Ticket> existingTickets = ticketRepository.findAllByRoundSeatSqIn(requestedSeatSqs);
        if (!existingTickets.isEmpty()) {
            throw TicketExceptionFactory.ticketConflict(
                    String.format("이미 생성된 티켓 슬롯이 존재합니다. (중복 건수: %d)", existingTickets.size())
            );
        }

        List<Ticket> newTickets = requestedSeatSqs.stream()
                .map(seatSq -> ticketMapper.requestToEntity(seatSq))
                .toList();

        ticketRepository.saveAll(newTickets);
        log.info("티켓 슬롯 일괄 생성 완료. (Count: {})", newTickets.size());
    }

    @Override
    public TicketResponse findTicketByRoundSeat(Long roundSeatSq) {
        if (roundSeatSq == null) {
            throw TicketExceptionFactory.ticketBadRequest("조회할 좌석 ID는 필수입니다.");
        }

        Ticket ticket = ticketRepository.findByRoundSeatSq(roundSeatSq)
                .orElseThrow(() -> TicketExceptionFactory.ticketNotFound("해당 좌석의 티켓 슬롯이 존재하지 않습니다. RoundSeatSq: " + roundSeatSq));

        return ticketMapper.entityToResponse(ticket);
    }

    // 다수의 좌석 식별자를 기반으로 현재 티켓의 발권 현황 정보를 일괄 추출
    @Override
    public List<TicketResponse> findTicketsByRoundSeats(List<Long> roundSeatSqs) {
        if (roundSeatSqs == null || roundSeatSqs.isEmpty()) {
            throw TicketExceptionFactory.ticketBadRequest("조회할 좌석 ID 목록이 비어있습니다.");
        }

        List<Ticket> tickets = ticketRepository.findAllByRoundSeatSqIn(roundSeatSqs);

        if (tickets.size() != roundSeatSqs.size()) {
            throw TicketExceptionFactory.ticketNotFound(
                    String.format("요청한 좌석 중 일부 티켓 슬롯을 찾을 수 없습니다. (요청: %d, 조회: %d)",
                            roundSeatSqs.size(), tickets.size())
            );
        }

        return tickets.stream()
                .map(ticket -> ticketMapper.entityToResponse(ticket))
                .toList();
    }

    @Override
    public List<TicketResponse> findMyTickets(Long userSq) {
        if (userSq == null || userSq <= 0) {
            throw TicketExceptionFactory.ticketBadRequest("사용자 정보가 유효하지 않습니다.");
        }

        List<Ticket> tickets = ticketRepository.findAllByUserSqOrderByCreatedDtDesc(userSq);

        return tickets.stream()
                .map(ticket -> ticketMapper.entityToResponse(ticket))
                .toList();
    }

    @Override
    public TicketResponse findMyTicket(Long userSq, Long ticketSq) {
        if (userSq == null || userSq <= 0) {
            throw TicketExceptionFactory.ticketBadRequest("사용자 정보가 유효하지 않습니다.");
        }
        if (ticketSq == null) {
            throw TicketExceptionFactory.ticketBadRequest("티켓 ID는 필수입니다.");
        }

        Ticket ticket = ticketRepository.findById(ticketSq)
                .orElseThrow(() -> TicketExceptionFactory.ticketNotFound("존재하지 않는 티켓입니다."));

        if (!Objects.equals(ticket.getUserSq(), userSq)) {
            throw TicketExceptionFactory.ticketForbidden("해당 티켓에 대한 접근 권한이 없습니다.");
        }

        return ticketMapper.entityToResponse(ticket);
    }

    // 결제 성공 정보와 적용된 혜택을 기반으로 실거래 가격을 산출하여 티켓 소유권 확정
    @Override
    @Transactional
    public void issueTickets(Long userSq, Payment payment, Long bookingSq, List<BookingSeatResponse> bookingSeats) {

        List<Long> roundSeatSqs = bookingSeats.stream()
                .map(seat -> seat.roundSeatSq())
                .toList();

        List<Ticket> tickets = ticketRepository.findAllByRoundSeatSqIn(roundSeatSqs);
        List<RoundSeatResponse> seatDetails =
        performanceClient.findRoundSeats(new SeatReserveRequest(roundSeatSqs));

        Map<Long, RoundSeatResponse> seatMap = seatDetails.stream()
                .collect(Collectors.toMap(seat -> seat.roundSeatSq(), Function.identity()));

        Map<Long, DiscountPolicy> discountMap = bookingSeats.stream()
                .collect(Collectors.toMap(seat -> seat.roundSeatSq(), seat -> seat.discountPolicy()));

        for (Ticket ticket : tickets) {
            RoundSeatResponse seatInfo = seatMap.get(ticket.getRoundSeatSq());
            DiscountPolicy policy = discountMap.getOrDefault(ticket.getRoundSeatSq(), DiscountPolicy.NONE);

            long originalPrice = 0L;
            if (seatInfo != null && seatInfo.price() != null) {
                originalPrice = seatInfo.price();
            }

            // 적용된 할인 정책에 따라 최종 결제 금액 및 혜택 내역 계산
            long discountAmount = (originalPrice * policy.getDiscountPercent()) / 100;
            long finalPrice = originalPrice - discountAmount;

            ticket.issue(userSq, payment, bookingSq, originalPrice, policy.name(), finalPrice);
        }
    }

    // 거래 무효화 또는 환불 발생 시 발권된 티켓 정보를 초기화하여 판매 가능 상태로 전환
    @Override
    @Transactional
    public void resetTickets(List<Long> roundSeatSqs) {
        List<Ticket> tickets = ticketRepository.findAllByRoundSeatSqIn(roundSeatSqs);
        tickets.forEach(ticket -> ticket.clear());
    }
}