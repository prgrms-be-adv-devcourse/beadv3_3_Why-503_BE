package io.why503.performanceservice.domain.show.service;

import feign.FeignException;
import io.why503.performanceservice.domain.seat.model.entity.SeatEntity;
import io.why503.performanceservice.domain.seat.repository.SeatRepository;
import io.why503.performanceservice.domain.show.model.dto.ShowCreateWithSeatPolicyRequest;
import io.why503.performanceservice.domain.show.model.dto.ShowRequest;
import io.why503.performanceservice.domain.show.model.dto.ShowResponse;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import io.why503.performanceservice.domain.show.model.enums.ShowCategory;
import io.why503.performanceservice.domain.show.model.enums.ShowStatus;
import io.why503.performanceservice.domain.show.repository.ShowRepository;
import io.why503.performanceservice.domain.showseat.model.dto.SeatPolicyRequest;
import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;
import io.why503.performanceservice.domain.showseat.service.ShowSeatService;
import io.why503.performanceservice.global.client.accountservice.AccountServiceClient;
import io.why503.performanceservice.global.client.accountservice.dto.CompanyInfoResponse;
import io.why503.performanceservice.global.error.exception.PerformanceForbiddenException;
import io.why503.performanceservice.global.error.exception.UnauthorizedException;
import io.why503.performanceservice.global.error.exception.UserServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowServiceImpl implements ShowService {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final ShowSeatService showSeatService;
    private final AccountServiceClient accountServiceClient;

    /**
     * 공연 + 좌석 정책 생성
     */
    @Override
    @Transactional
    public Long createShowWithSeats(
            ShowCreateWithSeatPolicyRequest req,
            String authorization
    ) {
        ShowRequest showReq = req.getShow();

        Long companySq = resolveCompanySq(authorization);
        ShowCategory category = ShowCategory.fromCode(showReq.getCategory());

        ShowEntity show = ShowEntity.builder()
                .showName(showReq.getShowName())
                .startDate(showReq.getStartDate())
                .endDate(showReq.getEndDate())
                .openDt(showReq.getOpenDt())
                .showTime(showReq.getShowTime())
                .viewingAge(showReq.getViewingAge())
                .concertHallSq(showReq.getConcertHallSq())
                .companySq(companySq)
                .build();

        show.setCategory(category);
        show.setShowStatus(ShowStatus.SCHEDULED);

        ShowEntity savedShow = showRepository.save(show);

        List<SeatEntity> allSeats =
                seatRepository
                        .findAllByConcertHall_ConcertHallSqOrderBySeatAreaAscAreaSeatNoAsc(
                                savedShow.getConcertHallSq()
                        );

        if (allSeats.isEmpty()) {
            throw new IllegalStateException("no seats found for concert hall");
        }

        Map<String, List<SeatEntity>> seatsByArea =
                allSeats.stream()
                        .collect(Collectors.groupingBy(SeatEntity::getSeatArea));

        List<ShowSeatEntity> showSeats =
                req.getSeatPolicies().stream()
                        .flatMap(policy ->
                                createShowSeatsByPolicy(savedShow, policy, seatsByArea).stream()
                        )
                        .toList();

        showSeatService.saveAll(showSeats);

        return savedShow.getShowSq();
    }

    /**
     * 공연 단독 생성
     */
    @Override
    @Transactional
    public ShowResponse createShow(
            ShowRequest req,
            String authorization
    ) {
        Long companySq = resolveCompanySq(authorization);
        ShowCategory category = ShowCategory.fromCode(req.getCategory());

        ShowEntity show = ShowEntity.builder()
                .showName(req.getShowName())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .openDt(req.getOpenDt())
                .showTime(req.getShowTime())
                .viewingAge(req.getViewingAge())
                .concertHallSq(req.getConcertHallSq())
                .companySq(companySq)
                .build();

        show.setCategory(category);
        show.setShowStatus(ShowStatus.SCHEDULED);

        ShowEntity saved = showRepository.save(show);

        return ShowResponse.builder()
                .showSq(saved.getShowSq())
                .showName(saved.getShowName())
                .startDate(saved.getStartDate())
                .endDate(saved.getEndDate())
                .openDt(saved.getOpenDt())
                .showTime(saved.getShowTime())
                .viewingAge(saved.getViewingAge())
                .category(saved.getCategoryEnum())
                .showStat(saved.getShowStatus())
                .concertHallSq(saved.getConcertHallSq())
                .companySq(saved.getCompanySq())
                .build();
    }

    /**
     * 공연 단건 조회
     */
    @Override
    public ShowResponse getShow(Long showSq) {
        ShowEntity show = showRepository.findById(showSq)
                .orElseThrow(() -> new IllegalArgumentException("show not found"));

        return ShowResponse.builder()
                .showSq(show.getShowSq())
                .showName(show.getShowName())
                .startDate(show.getStartDate())
                .endDate(show.getEndDate())
                .openDt(show.getOpenDt())
                .showTime(show.getShowTime())
                .viewingAge(show.getViewingAge())
                .category(show.getCategoryEnum())
                .showStat(show.getShowStatus())
                .concertHallSq(show.getConcertHallSq())
                .companySq(show.getCompanySq())
                .build();
    }

    /**
     * 좌석 정책 기반 show_seat 생성
     */
    private List<ShowSeatEntity> createShowSeatsByPolicy(
            ShowEntity show,
            SeatPolicyRequest policy,
            Map<String, List<SeatEntity>> seatsByArea
    ) {
        List<SeatEntity> seats = seatsByArea.get(policy.getSeatArea());

        if (seats == null || seats.isEmpty()) {
            throw new IllegalArgumentException(
                    "seat area not found: " + policy.getSeatArea()
            );
        }

        ShowSeatGrade grade = ShowSeatGrade.valueOf(policy.getGrade());

        return seats.stream()
                .map(seat ->
                        new ShowSeatEntity(show, seat, grade, policy.getPrice())
                )
                .toList();
    }

    /**
     * account-service 호출
     * - COMPANY 권한 검증
     * - companySq 조회
     */
    private Long resolveCompanySq(String authorization) {
        try {
            CompanyInfoResponse res =
                    accountServiceClient.getMyCompanyInfo(authorization);

            if (res == null || res.getCompanySq() == null) {
                throw new PerformanceForbiddenException(
                        "company info not found for current user"
                );
            }

            return res.getCompanySq();

        } catch (FeignException.Forbidden e) {
            throw new PerformanceForbiddenException(
                    "only COMPANY users can create shows"
            );
        } catch (FeignException.Unauthorized e) {
            throw new UnauthorizedException(
                    "invalid authorization"
            );
        } catch (FeignException e) {
            throw new UserServiceUnavailableException(
                    "account-service call failed",
                    e
            );
        }
    }
}
