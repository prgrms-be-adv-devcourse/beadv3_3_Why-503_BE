package io.why503.paymentservice.domain.settlement.service.impl;

import io.why503.paymentservice.domain.payment.util.PaymentExceptionFactory; // 예외 팩토리 import 추가
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

    @Override
    public List<SettlementResponse> getSettlementsByCompanySq(Long companySq) {
        return settlementMapper.toResponseList(settlementRepository.findByCompanySqOrderByCreatedDtDesc(companySq));
    }

    @Override
    public List<SettlementResponse> getSettlementsByShowSq(Long showSq) {
        return settlementMapper.toResponseList(settlementRepository.findByShowSqOrderByCreatedDtDesc(showSq));
    }

    @Override
    @Transactional
    public void createSettlement(Long showSq, Long companySq) {
        // [방어 1] 멱등성 보장: 이미 해당 공연에 대한 정산 내역이 있는지 확인 (중복 정산 차단)
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

        // [방어 2] 0원 수익 차단: 팔린 티켓이 없으면 정산 생성 안 함
        if (totalRevenue == 0) {
            log.warn("[정산 보류] 결제된 수익이 0원입니다. 공연 식별자: {}", showSq);
            throw PaymentExceptionFactory.paymentBadRequest("해당 공연의 총 결제 수익이 0원이라 정산을 진행할 수 없습니다.");
        }

        long platformFee = (long) (totalRevenue * 0.1);
        long settlementAmount = totalRevenue - platformFee;

        Settlement settlement = Settlement.builder()
                .showSq(showSq)
                .companySq(companySq)
                .totalAmount(totalRevenue)
                .feeAmount(platformFee)
                .settlementAmount(settlementAmount)
                .settlementStatus(SettlementStatus.READY)
                .build();

        settlementRepository.save(settlement);
        log.info("[정산 대기 생성 완료] 공연: {}, 총수익: {}, 정산액: {}", showSq, totalRevenue, settlementAmount);
    }

    @Override
    @Transactional
    public void processPendingSettlements() {
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
                    continue; // 정보가 없으면 이 건만 건너뛰고 다음 정산 진행
                }

                // [방어 3] 학생용 모킹(Mock) 은행 API 호출
                boolean isTransferSuccess = mockFirmbankingTransfer(companyInfo, settlement.getSettlementAmount());

                if (isTransferSuccess) {
                    settlement.completeSettlement(); // 상태를 COMPLETED로 변경
                    log.info("[정산 송금 완료] 정산 식별자: {}, 기획사: {}", settlement.getSq(), companyInfo.ownerName());
                } else {
                    log.error("[정산 송금 실패] 가상 은행망 오류 발생 - 정산 식별자: {}", settlement.getSq());
                    // 실제라면 실패 횟수를 카운트하거나 상태를 FAILED로 바꿀 수 있습니다.
                }

            } catch (Exception e) {
                // [방어 4] 단일 건 예외 격리: 1번 기획사 통신 에러가 2번 기획사 정산을 멈추지 않도록!
                log.error("[정산 처리 중 치명적 오류] 정산 식별자: {}, 원인: {}", settlement.getSq(), e.getMessage());
            }
        }
    }

    /**
     * 학생 프로젝트를 위한 가상 펌뱅킹(Firmbanking) 송금 모듈
     * - 실제 상용 환경에서는 이 부분에 오픈뱅킹 API, 포트원 등 외부 PG사 연동 코드가 들어갑니다.
     */
    private boolean mockFirmbankingTransfer(CompanySettlementResponse info, Long amount) {
        log.info(">>> [MOCK 송금 API 호출] 은행: {}, 계좌: {}, 예금주: {}, 송금액: {}",
                info.bank(), info.accountNumber(), info.ownerName(), amount);

        // 특정 테스트용 계좌번호를 넣었을 때 일부러 실패하게 만들어 예외 테스트를 할 수도 있습니다.
        if ("000-000-000".equals(info.accountNumber())) {
            return false;
        }

        // 0.5초 정도 네트워크 딜레이를 주는 척 흉내내기 (선택사항)
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}

        return true;
    }
}