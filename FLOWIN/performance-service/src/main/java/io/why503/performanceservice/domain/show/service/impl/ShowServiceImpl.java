package io.why503.performanceservice.domain.show.service.impl;

import feign.FeignException;
import io.why503.performanceservice.domain.seat.model.entity.SeatEntity;
import io.why503.performanceservice.domain.seat.repository.SeatRepository;
import io.why503.performanceservice.domain.show.model.dto.request.ShowCreateWithSeatPolicyRequest;
import io.why503.performanceservice.domain.show.model.dto.request.ShowRequest;
import io.why503.performanceservice.domain.show.model.dto.response.ShowResponse;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import io.why503.performanceservice.domain.show.repository.ShowRepository;
import io.why503.performanceservice.domain.show.service.ShowService;
import io.why503.performanceservice.domain.showseat.model.dto.request.SeatPolicyRequest;
import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;
import io.why503.performanceservice.domain.showseat.service.ShowSeatService;
import io.why503.performanceservice.global.client.accountservice.AccountServiceClient;
import io.why503.performanceservice.global.client.accountservice.dto.CompanyInfoResponse;
import io.why503.performanceservice.global.error.ErrorCode;
import io.why503.performanceservice.global.error.exception.BusinessException;
import io.why503.performanceservice.util.mapper.ShowMapper;
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


    //공연 엔티티 조회 및 예외 처리
    @Override
    public ShowEntity findShowBySq(Long showSq) {
        return showRepository.findById(showSq)
                .orElseThrow(() ->new BusinessException(ErrorCode.SHOW_NOT_FOUND));
    }

    @Override
    @Transactional
    public Long createShowWithSeats(
            ShowCreateWithSeatPolicyRequest request,
            Long userSq
    ) {
        Long companySq = resolveCompanySq(userSq);

        ShowEntity show = showMapper.requestToEntity(request.showRequest(), companySq);
        ShowEntity savedShow = showRepository.save(show);

        List<SeatEntity> allSeats =
                seatRepository.findAllByConcertHall_SqOrderByAreaAscNumInAreaAsc(
                        savedShow.getConcertHallSq()
                );

        if (allSeats.isEmpty()) {
            throw new BusinessException(ErrorCode.SEAT_NOT_FOUND);
        }

        Map<String, List<SeatEntity>> seatsByArea =
                allSeats.stream().collect(Collectors.groupingBy(SeatEntity::getArea));

        List<ShowSeatEntity> showSeats =
                request.seatPolicies().stream()
                        .flatMap(p -> createShowSeatsByPolicy(savedShow, p, seatsByArea).stream())
                        .toList();

        showSeatService.saveAll(showSeats);
        return savedShow.getSq();
    }

    @Override
    @Transactional
    public ShowResponse createShow(ShowRequest request, Long userSq) {
        Long companySq = resolveCompanySq(userSq);
        ShowEntity show = showMapper.requestToEntity(request, companySq);
        showRepository.save(show);
        return showMapper.entityToResponse(show);
    }

    @Override
    public ShowResponse readShowBySq(Long showSq) {
        ShowEntity show = findShowBySq(showSq);
        return showMapper.entityToResponse(show);
    }

    private List<ShowSeatEntity> createShowSeatsByPolicy(
            ShowEntity show,
            SeatPolicyRequest policy,
            Map<String, List<SeatEntity>> seatsByArea
    ) {
        List<SeatEntity> seats = seatsByArea.get(policy.seatArea());
        if (seats == null || seats.isEmpty()) {
            throw new BusinessException(ErrorCode.SEAT_NOT_FOUND);
        }
        ShowSeatGrade grade = ShowSeatGrade.valueOf(policy.grade());
        return seats.stream()
                .map(seat -> new ShowSeatEntity(grade,  policy.price(), show, seat))
                .toList();
    }

    private Long resolveCompanySq(Long userSq) {
        try {
            CompanyInfoResponse response = accountServiceClient.getMyCompanyInfo(userSq);

            // 응답이 없거나 회사 정보가 없는 경우 -> 권한 없음(Forbidden) 처리
            if (response == null || response.companySq() == null) {
                throw new BusinessException(ErrorCode.PERFORMANCE_CREATE_FORBIDDEN);
            }
            return response.companySq();

        } catch (FeignException.Forbidden e) {
            // Feign Client 403 에러 -> 권한 없음
            throw new BusinessException(ErrorCode.PERFORMANCE_CREATE_FORBIDDEN);

        } catch (FeignException.Unauthorized e) {
            // Feign Client 401 에러 -> 인증 실패
            throw new BusinessException(ErrorCode.UNAUTHORIZED);

        } catch (FeignException e) {
            // 그 외 Feign 통신 에러 (서버 다운 등) -> 502 Bad Gateway
            throw new BusinessException(ErrorCode.USER_SERVICE_UNAVAILABLE);
        }
    }
}
