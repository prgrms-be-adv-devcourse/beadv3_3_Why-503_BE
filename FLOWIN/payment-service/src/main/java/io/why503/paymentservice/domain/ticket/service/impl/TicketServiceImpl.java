package io.why503.paymentservice.domain.ticket.service.impl;

import io.why503.paymentservice.domain.payment.model.entity.Payment;
import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import io.why503.paymentservice.domain.ticket.mapper.TicketMapper;
import io.why503.paymentservice.domain.ticket.model.dto.request.TicketCreateRequest;
import io.why503.paymentservice.domain.ticket.model.dto.response.TicketResponse;
import io.why503.paymentservice.domain.ticket.model.entity.Ticket;
import io.why503.paymentservice.domain.ticket.repository.TicketRepository;
import io.why503.paymentservice.domain.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 티켓 슬롯 생성 및 조회 비즈니스 로직 구현체
 * - Service 레이어에서 트랜잭션을 관리하며 데이터 정합성을 보장
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    /**
     * 공연 회차 좌석 생성 시점에 맞춰 티켓 슬롯을 일괄 생성
     * - 중복 생성 요청 시 Conflict 예외 발생 (데이터 무결성 보장)
     */
    @Override
    @Transactional
    public void createTicketSlots(TicketCreateRequest request) {
        if (request == null || request.roundSeatSqs() == null || request.roundSeatSqs().isEmpty()) {
            throw PaymentExceptionFactory.paymentBadRequest("생성할 티켓 좌석 정보가 없습니다.");
        }

        List<Long> requestedSeatSqs = request.roundSeatSqs();

        // 1. 이미 존재하는 티켓 슬롯인지 검증 (중복 생성 방지)
        List<Ticket> existingTickets = ticketRepository.findAllByRoundSeatSqIn(requestedSeatSqs);
        if (!existingTickets.isEmpty()) {
            throw PaymentExceptionFactory.paymentConflict(
                    String.format("이미 생성된 티켓 슬롯이 존재합니다. (중복 건수: %d)", existingTickets.size())
            );
        }

        // 2. 요청받은 좌석 ID 목록을 엔티티 리스트로 변환
        List<Ticket> newTickets = requestedSeatSqs.stream()
                .map(seatSq -> ticketMapper.toEntity(seatSq))
                .toList();

        // 3. 일괄 저장 (Batch Insert)
        ticketRepository.saveAll(newTickets);
        log.info("티켓 슬롯 일괄 생성 완료. (RoundSq: {}, Count: {})", request.roundSq(), newTickets.size());
    }

    /**
     * 특정 좌석 ID(RoundSeatSq)에 해당하는 티켓 슬롯 단건 조회
     */
    @Override
    public TicketResponse findTicketByRoundSeat(Long roundSeatSq) {
        if (roundSeatSq == null) {
            throw PaymentExceptionFactory.paymentBadRequest("조회할 좌석 ID는 필수입니다.");
        }

        Ticket ticket = ticketRepository.findByRoundSeatSq(roundSeatSq)
                .orElseThrow(() -> PaymentExceptionFactory.paymentNotFound("해당 좌석의 티켓 슬롯이 존재하지 않습니다. RoundSeatSq: " + roundSeatSq));

        return ticketMapper.toResponse(ticket);
    }

    /**
     * 여러 좌석 ID에 해당하는 티켓 슬롯 목록 일괄 조회
     * - 요청한 개수와 조회된 개수가 다를 경우 예외 처리 (엄격한 검증)
     */
    @Override
    public List<TicketResponse> findTicketsByRoundSeats(List<Long> roundSeatSqs) {
        if (roundSeatSqs == null || roundSeatSqs.isEmpty()) {
            throw PaymentExceptionFactory.paymentBadRequest("조회할 좌석 ID 목록이 비어있습니다.");
        }

        List<Ticket> tickets = ticketRepository.findAllByRoundSeatSqIn(roundSeatSqs);

        // 요청한 좌석 수와 실제 조회된 티켓 수가 다르면 데이터 불일치로 간주
        if (tickets.size() != roundSeatSqs.size()) {
            throw PaymentExceptionFactory.paymentNotFound(
                    String.format("요청한 좌석 중 일부 티켓 슬롯을 찾을 수 없습니다. (요청: %d, 조회: %d)",
                            roundSeatSqs.size(), tickets.size())
            );
        }

        return tickets.stream()
                .map(ticket -> ticketMapper.toResponse(ticket))
                .toList();
    }

    /**
     * 사용자의 티켓 목록 전체 조회
     */
    @Override
    public List<TicketResponse> findMyTickets(Long userSq) {
        if (userSq == null || userSq <= 0) {
            throw PaymentExceptionFactory.paymentBadRequest("사용자 정보가 유효하지 않습니다.");
        }

        List<Ticket> tickets = ticketRepository.findAllByUserSqOrderByCreatedDtDesc(userSq);

        return tickets.stream()
                .map(ticket -> ticketMapper.toResponse(ticket))
                .toList();
    }

    /**
     * 사용자의 특정 티켓 상세 조회
     * - 본인 소유 확인 로직 포함
     */
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

        // 본인 소유 티켓인지 검증 (공석이거나 다른 사람의 티켓인 경우 접근 불가)
        if (!Objects.equals(ticket.getUserSq(), userSq)) {
            throw PaymentExceptionFactory.paymentForbidden("해당 티켓에 대한 접근 권한이 없습니다.");
        }

        return ticketMapper.toResponse(ticket);
    }

    @Override
    @Transactional
    public void issueTickets(Long userSq, Payment payment, Long bookingSq, List<Long> roundSeatSqs) {
        List<Ticket> tickets = ticketRepository.findAllByRoundSeatSqIn(roundSeatSqs);

        // 데이터 정합성 체크 (선택 사항)
        if (tickets.size() != roundSeatSqs.size()) {
            // 로그 남기기 or 예외 처리
        }

        for (Ticket ticket : tickets) {
            // 가격 정보 등은 필요에 따라 파라미터로 받거나 조회하여 설정
            ticket.issue(userSq, payment, bookingSq, 0L, null, 0L);
        }
    }

    @Override
    @Transactional
    public void resetTickets(List<Long> roundSeatSqs) {
        List<Ticket> tickets = ticketRepository.findAllByRoundSeatSqIn(roundSeatSqs);
        tickets.forEach(Ticket::clear);
    }
}