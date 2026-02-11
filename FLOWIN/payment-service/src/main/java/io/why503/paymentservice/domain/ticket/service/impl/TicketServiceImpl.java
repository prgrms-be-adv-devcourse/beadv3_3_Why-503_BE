package io.why503.paymentservice.domain.ticket.service.impl;

import io.why503.paymentservice.domain.payment.model.entity.Payment;
import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import io.why503.paymentservice.domain.ticket.mapper.TicketMapper;
import io.why503.paymentservice.domain.ticket.model.dto.request.TicketCreateRequest;
import io.why503.paymentservice.domain.ticket.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.ticket.model.entity.Ticket;
import io.why503.paymentservice.domain.ticket.model.enums.DiscountPolicy;
import io.why503.paymentservice.domain.ticket.repository.TicketRepository;
import io.why503.paymentservice.domain.ticket.service.TicketService;
import io.why503.paymentservice.global.client.PerformanceClient;
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
 * 티켓의 생성, 조회 및 발권 상태 전환을 관리하는 서비스 구현체
 * - 거래 흐름에 따른 티켓 데이터의 정합성과 소유권 보호를 수행
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final PerformanceClient performanceClient;

    // 새로운 공연 회차 정보에 대응하는 티켓 기초 데이터를 일괄 확보
    @Override
    @Transactional
    public void createTicketSlots(TicketCreateRequest request) {
        if (request == null || request.roundSeatSqs() == null || request.roundSeatSqs().isEmpty()) {
            throw PaymentExceptionFactory.paymentBadRequest("생성할 티켓 좌석 정보가 없습니다.");
        }

        List<Long> requestedSeatSqs = request.roundSeatSqs();

        List<Ticket> existingTickets = ticketRepository.findAllByRoundSeatSqIn(requestedSeatSqs);
        if (!existingTickets.isEmpty()) {
            throw PaymentExceptionFactory.paymentConflict(
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
            throw PaymentExceptionFactory.paymentBadRequest("조회할 좌석 ID는 필수입니다.");
        }

        Ticket ticket = ticketRepository.findByRoundSeatSq(roundSeatSq)
                .orElseThrow(() -> PaymentExceptionFactory.paymentNotFound("해당 좌석의 티켓 슬롯이 존재하지 않습니다. RoundSeatSq: " + roundSeatSq));

        return ticketMapper.entityToResponse(ticket);
    }

    // 요청된 좌석 목록과 실제 데이터의 일치 여부를 검증하며 정보 추출
    @Override
    public List<TicketResponse> findTicketsByRoundSeats(List<Long> roundSeatSqs) {
        if (roundSeatSqs == null || roundSeatSqs.isEmpty()) {
            throw PaymentExceptionFactory.paymentBadRequest("조회할 좌석 ID 목록이 비어있습니다.");
        }

        List<Ticket> tickets = ticketRepository.findAllByRoundSeatSqIn(roundSeatSqs);

        if (tickets.size() != roundSeatSqs.size()) {
            throw PaymentExceptionFactory.paymentNotFound(
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
            throw PaymentExceptionFactory.paymentBadRequest("사용자 정보가 유효하지 않습니다.");
        }

        List<Ticket> tickets = ticketRepository.findAllByUserSqOrderByCreatedDtDesc(userSq);

        return tickets.stream()
                .map(ticket -> ticketMapper.entityToResponse(ticket))
                .toList();
    }

    @Override
    public TicketResponse findMyTicket(Long userSq, Long ticketSq) {
        if (userSq == null || userSq <= 0) {
            throw PaymentExceptionFactory.paymentBadRequest("사용자 정보가 유효하지 않습니다.");
        }
        if (ticketSq == null) {
            throw PaymentExceptionFactory.paymentBadRequest("티켓 ID는 필수입니다.");
        }

        Ticket ticket = ticketRepository.findById(ticketSq)
                .orElseThrow(() -> PaymentExceptionFactory.paymentNotFound("존재하지 않는 티켓입니다."));

        if (!Objects.equals(ticket.getUserSq(), userSq)) {
            throw PaymentExceptionFactory.paymentForbidden("해당 티켓에 대한 접근 권한이 없습니다.");
        }

        return ticketMapper.entityToResponse(ticket);
    }

    // 결제 완료 정보를 티켓 데이터에 반영하여 최종 발권 처리
    @Override
    @Transactional
    public void issueTickets(Long userSq, Payment payment, Long bookingSq, List<BookingSeatResponse> bookingSeats) {

        List<Long> roundSeatSqs = bookingSeats.stream()
                .map(seat -> seat.roundSeatSq())
                .toList();

        List<Ticket> tickets = ticketRepository.findAllByRoundSeatSqIn(roundSeatSqs);
        List<RoundSeatResponse> seatDetails = performanceClient.findRoundSeats(roundSeatSqs);

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

            long discountAmount = (originalPrice * policy.getDiscountPercent()) / 100;
            long finalPrice = originalPrice - discountAmount;

            ticket.issue(userSq, payment, bookingSq, originalPrice, policy.name(), finalPrice);
        }
    }

    @Override
    @Transactional
    public void resetTickets(List<Long> roundSeatSqs) {
        List<Ticket> tickets = ticketRepository.findAllByRoundSeatSqIn(roundSeatSqs);
        tickets.forEach(ticket -> ticket.clear());
    }
}