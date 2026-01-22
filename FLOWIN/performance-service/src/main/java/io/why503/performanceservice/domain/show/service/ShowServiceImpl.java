package io.why503.performanceservice.domain.show.service;

import feign.FeignException;
import io.why503.performanceservice.domain.seat.model.entity.SeatEntity;
import io.why503.performanceservice.domain.seat.repository.SeatRepository;
import io.why503.performanceservice.domain.show.mapper.ShowMapper;
import io.why503.performanceservice.domain.show.model.dto.ShowCreateWithSeatPolicyRequest;
import io.why503.performanceservice.domain.show.model.dto.ShowRequest;
import io.why503.performanceservice.domain.show.model.dto.ShowResponse;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
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
    private final ShowMapper showMapper;

    @Override
    @Transactional
    public Long createShowWithSeats(
            ShowCreateWithSeatPolicyRequest request,
            Long userSq
    ) {
        Long companySq = resolveCompanySq(userSq);

        ShowEntity show = showMapper.toEntity(request.getShow(), companySq);
        ShowEntity savedShow = showRepository.save(show);

        List<SeatEntity> allSeats =
                seatRepository.findAllByConcertHall_ConcertHallSqOrderBySeatAreaAscAreaSeatNoAsc(
                        savedShow.getConcertHallSq()
                );

        if (allSeats.isEmpty()) {
            throw new IllegalStateException("no seats found for concert hall");
        }

        Map<String, List<SeatEntity>> seatsByArea =
                allSeats.stream().collect(Collectors.groupingBy(SeatEntity::getSeatArea));

        List<ShowSeatEntity> showSeats =
                request.getSeatPolicies().stream()
                        .flatMap(p -> createShowSeatsByPolicy(savedShow, p, seatsByArea).stream())
                        .toList();

        showSeatService.saveAll(showSeats);
        return savedShow.getShowSq();
    }

    @Override
    @Transactional
    public ShowResponse createShow(ShowRequest request, Long userSq) {
        Long companySq = resolveCompanySq(userSq);
        ShowEntity show = showMapper.toEntity(request, companySq);
        ShowEntity saved = showRepository.save(show);
        return showMapper.toResponse(saved);
    }

    @Override
    public ShowResponse getShow(Long showSq) {
        ShowEntity show = showRepository.findById(showSq)
                .orElseThrow(() -> new IllegalArgumentException("show not found"));
        return showMapper.toResponse(show);
    }

    private List<ShowSeatEntity> createShowSeatsByPolicy(
            ShowEntity show,
            SeatPolicyRequest policy,
            Map<String, List<SeatEntity>> seatsByArea
    ) {
        List<SeatEntity> seats = seatsByArea.get(policy.getSeatArea());
        if (seats == null || seats.isEmpty()) {
            throw new IllegalArgumentException("seat area not found: " + policy.getSeatArea());
        }
        ShowSeatGrade grade = ShowSeatGrade.valueOf(policy.getGrade());
        return seats.stream()
                .map(seat -> new ShowSeatEntity(show, seat, grade, policy.getPrice()))
                .toList();
    }

    private Long resolveCompanySq(Long userSq) {
        try {
            CompanyInfoResponse response = accountServiceClient.getMyCompanyInfo(userSq);
            if (response == null || response.getCompanySq() == null) {
                throw new PerformanceForbiddenException("company info not found for current user");
            }
            return response.getCompanySq();
        } catch (FeignException.Forbidden e) {
            throw new PerformanceForbiddenException("only COMPANY users can create shows");
        } catch (FeignException.Unauthorized e) {
            throw new UnauthorizedException("invalid authorization");
        } catch (FeignException e) {
            throw new UserServiceUnavailableException("account-service call failed", e);
        }
    }
}
