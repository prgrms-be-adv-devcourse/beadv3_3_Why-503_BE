package io.why503.performanceservice.domain.show.service.impl;

import feign.FeignException;
import io.why503.performanceservice.domain.hall.model.entity.HallEntity;
import io.why503.performanceservice.domain.hall.repository.HallRepository;
import io.why503.performanceservice.domain.seat.model.entity.SeatEntity;
import io.why503.performanceservice.domain.seat.repository.SeatRepository;
import io.why503.performanceservice.domain.show.model.dto.request.ShowCreateWithSeatPolicyRequest;
import io.why503.performanceservice.domain.show.model.dto.request.ShowRequest;
import io.why503.performanceservice.domain.show.model.dto.response.ShowResponse;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import io.why503.performanceservice.domain.show.model.enums.ShowCategory;
import io.why503.performanceservice.domain.show.model.enums.ShowGenre;
import io.why503.performanceservice.domain.show.repository.ShowRepository;
import io.why503.performanceservice.domain.show.service.ShowService;
import io.why503.performanceservice.domain.show.util.ShowExceptionFactory;
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

import java.util.ArrayList;
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
    private final HallRepository hallRepository;
    private final AccountServiceClient accountServiceClient;
    private final ShowMapper showMapper;


    //공연 엔티티 조회 및 예외 처리
    @Override
    public ShowEntity findShowBySq(Long showSq) {
        return showRepository.findById(showSq)
                .orElseThrow(() -> ShowExceptionFactory.showNotFound("존재하지 않는 공연입니다"));
    }

    @Override
    @Transactional
    public Long createShowWithSeats(
            ShowCreateWithSeatPolicyRequest request,
            Long userSq
    ) {

        Long companySq = resolveCompanySq(userSq);

        //공연장 id로 hallEntity 조회
        HallEntity hallEntity = hallRepository.findById(request.showRequest().hallSq())
                .orElseThrow(() -> ShowExceptionFactory.showNotFound("존재하지 않는 공연장입니다."));

        //mapper에 hallEntity 전달
        ShowEntity show = showMapper.requestToEntity(request.showRequest(), companySq, hallEntity);
        ShowEntity savedShow = showRepository.save(show);

        List<SeatEntity> allSeats =
                seatRepository.findAllByHallSqOrderByAreaAscNumInAreaAsc(
                        savedShow.getHall().getSq()
                );

        if (allSeats.isEmpty()) {
            throw ShowExceptionFactory.showNotFound("공연 좌석이 존재하지 않습니다");
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
        //공연장 id로 hallEntity 조회
        HallEntity hallEntity = hallRepository.findById(request.hallSq())
                .orElseThrow(() -> ShowExceptionFactory.showNotFound("존재하지 않는 공연장 입니다"));

        //mapper에 hallEntity 전달
        ShowEntity show = showMapper.requestToEntity(request, companySq, hallEntity);
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
            throw ShowExceptionFactory.showNotFound("좌석이 존재하지 않습니다.");
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
                throw ShowExceptionFactory.showForbidden("회사 정보를 찾을 수 없거나 공연 생성 권한이 없습니다.");}
            return response.companySq();

        } catch (FeignException.Forbidden e) {
            // Feign Client 403 에러 -> 권한 없음
            throw ShowExceptionFactory.showForbidden("공연 생성 권한이 없습니다.");

        } catch (FeignException.Unauthorized e) {
            // Feign Client 401 에러 -> 인증 실패
            throw ShowExceptionFactory.showForbidden("인증되지 않은 사용자입니다.");
        } catch (FeignException e) {
            // 그 외 Feign 통신 에러 (서버 다운 등) -> 502 Bad Gateway
            throw new BusinessException(ErrorCode.USER_SERVICE_UNAVAILABLE);        }
    }
    // 카테고리별 조회
    @Override
    public List<ShowResponse> findShowsByCategory(ShowCategory category) {
        List<ShowEntity> shows = showRepository.findByCategory(category);

        List<ShowResponse> list = new ArrayList<>();
        for (ShowEntity show : shows) {
            ShowResponse showResponse = showMapper.entityToResponse(show);
            list.add(showResponse);
        }
        return list;
    }

    // 카테고리 + 장르 조회
    @Override
    public List<ShowResponse> findShowsByCategoryAndGenre(ShowCategory category, ShowGenre genre) {
        List<ShowEntity> shows = showRepository.findByCategoryAndGenre(category, genre);

        List<ShowResponse> list = new ArrayList<>();
        for (ShowEntity show : shows) {
            ShowResponse showResponse = showMapper.entityToResponse(show);
            list.add(showResponse);
        }
        return list;
    }
}
