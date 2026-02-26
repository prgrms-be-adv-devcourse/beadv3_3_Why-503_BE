package io.why503.paymentservice.domain.settlement.service.impl;

import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory;
import io.why503.paymentservice.domain.settlement.mapper.SettlementMapper;
import io.why503.paymentservice.domain.settlement.model.dto.response.SettlementResponse;
import io.why503.paymentservice.domain.settlement.model.entity.Settlement;
import io.why503.paymentservice.domain.settlement.model.enums.SettlementStatus;
import io.why503.paymentservice.domain.settlement.repository.SettlementRepository;
import io.why503.paymentservice.domain.settlement.service.SettlementService;
import io.why503.paymentservice.domain.ticket.model.entity.Ticket;
import io.why503.paymentservice.domain.ticket.repository.TicketRepository;
import io.why503.paymentservice.global.client.AccountClient;
import io.why503.paymentservice.global.client.PerformanceClient;
import io.why503.paymentservice.global.client.dto.response.CompanySettlementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 정산 데이터 생성, 조회 및 외부 뱅킹 시스템 연동을 통한 지급 처리를 담당하는 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementServiceImpl implements SettlementService {

    private final SettlementRepository settlementRepository;
    private final SettlementMapper settlementMapper;
    private final TicketRepository ticketRepository;

    private final PerformanceClient performanceClient;
    private final AccountClient accountClient;

    // 기획사 식별자를 기준으로 정산 이력 목록 반환
    @Override
    public List<SettlementResponse> getSettlementsByCompanySq(Long companySq) {
        return settlementMapper.entityToResponseList(settlementRepository.findByCompanySqOrderByCreatedDtDesc(companySq));
    }

    // 공연 식별자를 기준으로 정산 이력 목록 반환
    @Override
    public List<SettlementResponse> getSettlementsByShowSq(Long showSq) {
        return settlementMapper.entityToResponseList(settlementRepository.findByShowSqOrderByCreatedDtDesc(showSq));
    }

    // 공연 종료 시점의 티켓 판매 수익을 집계하여 신규 정산 데이터 생성
    @Override
    @Transactional
    public void createSettlement(Long showSq, Long companySq) {
        /*
         * 1. 동일 공연에 대한 정산 중복 생성 방지 검증
         * 2. 외부 연동을 통한 공연 좌석 및 티켓 판매 정보 수집
         * 3. 실제 판매된 티켓의 최종 수익 산출 및 수익 유무 검증
         * 4. 플랫폼 수수료(10%) 제외 후 정산 대기 데이터 저장
         */
        if (settlementRepository.existsByShowSq(showSq)) {
            log.error("[정산 실패] 중복 요청 - 공연 식별자: {}", showSq);
            throw PaymentExceptionFactory.paymentBadRequest("이미 해당 공연에 대한 정산 내역이 존재합니다.");
        }

        List<Long> roundSeatSqs = performanceClient.getRoundSeatSqsByShowSq(showSq);
        if (roundSeatSqs == null || roundSeatSqs.isEmpty()) {
            throw PaymentExceptionFactory.paymentBadRequest("공연 좌석 정보가 존재하지 않습니다.");
        }

        List<Ticket> tickets = ticketRepository.findAllByRoundSeatSqIn(roundSeatSqs);

        long totalRevenue = 0L;
        for (Ticket ticket : tickets) {
            if (ticket.isSold()) {
                totalRevenue += ticket.getFinalPrice();
            }
        }

        if (totalRevenue == 0) {
            log.warn("[정산 보류] 결제된 수익이 0원입니다. 공연 식별자: {}", showSq);
            throw PaymentExceptionFactory.paymentBadRequest("해당 공연의 총 결제 수익이 0원이라 정산을 진행할 수 없습니다.");
        }

        long platformFee = (long) (totalRevenue * 0.1);
        long settlementAmount = totalRevenue - platformFee;

        Settlement settlement = settlementMapper.responseToEntity(
                showSq, companySq, totalRevenue, platformFee, settlementAmount
        );
        settlementRepository.save(settlement);
        log.info("[정산 대기 생성 완료] 공연: {}, 총수익: {}, 정산액: {}", showSq, totalRevenue, settlementAmount);
    }

    // 정산 대기 건들을 추출하여 순차적으로 지급 처리 실행
    @Override
    @Transactional
    public void processPendingSettlements() {
        /*
         * 1. 대기 상태인 정산 대상 목록 조회
         * 2. 개별 건별로 기획사 계좌 정보 확인
         * 3. 가상 뱅킹 시스템을 통한 송금 요청
         * 4. 송금 성공 시 정산 상태를 완료로 변경 (예외 발생 시 해당 건만 격리)
         */
        List<Settlement> pendingSettlements = settlementRepository.findBySettlementStatus(SettlementStatus.READY);

        if (pendingSettlements == null || pendingSettlements.isEmpty()) {
            log.info("처리할 대기 중인 정산 건이 없습니다.");
            return;
        }

        for (Settlement settlement : pendingSettlements) {
            try {
                CompanySettlementResponse companyInfo = accountClient.getCompanySettlementInfo(settlement.getCompanySq());

                if (companyInfo == null) {
                    log.error("[정산 송금 실패] 기획사({}) 계좌 정보 누락", settlement.getCompanySq());
                    continue;
                }

                boolean isTransferSuccess = mockFirmbankingTransfer(companyInfo, settlement.getSettlementAmount());

                if (isTransferSuccess) {
                    settlement.completeSettlement();
                    log.info("[정산 송금 완료] 정산 식별자: {}, 기획사: {}", settlement.getSq(), companyInfo.ownerName());
                } else {
                    log.error("[정산 송금 실패] 가상 은행망 오류 발생 - 정산 식별자: {}", settlement.getSq());
                }

            } catch (Exception e) {
                log.error("[정산 처리 중 치명적 오류] 정산 식별자: {}, 원인: {}", settlement.getSq(), e.getMessage());
            }
        }
    }

    // 테스트 환경을 위한 가상 송금 처리
    private boolean mockFirmbankingTransfer(CompanySettlementResponse info, Long amount) {
        log.info(">>> [MOCK 송금 API 호출] 은행: {}, 계좌: {}, 예금주: {}, 송금액: {}",
                info.bank(), info.accountNumber(), info.ownerName(), amount);

        if ("000-000-000".equals(info.accountNumber())) {
            return false;
        }

        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        return true;
    }
}