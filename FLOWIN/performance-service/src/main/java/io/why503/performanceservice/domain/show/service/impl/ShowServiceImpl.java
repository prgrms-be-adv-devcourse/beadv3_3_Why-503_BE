package io.why503.performanceservice.domain.show.service.impl;

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
import io.why503.performanceservice.global.validator.UserValidator;
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
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;
    private final ShowSeatService showSeatService;
    private final ShowMapper showMapper;

    private final UserValidator userValidator;
    private final AccountServiceClient accountServiceClient;


    //공연 엔티티 조회 및 예외 처리
    @Override
    public ShowEntity findShowBySq(Long showSq) {
        return showRepository.findById(showSq)
                .orElseThrow(() -> ShowExceptionFactory.showNotFound("존재하지 않는 공연입니다"));
    }

    // 공연 + 좌석 생성
    @Override
    @Transactional
    public Long createShowWithSeats(
            ShowCreateWithSeatPolicyRequest request,
            Long userSq
    ) {
        // 권한 검증
        userValidator.validateEnterprise(userSq,ShowExceptionFactory.showForbidden("기업 또는 관리자만 공연장 등록이 가능합니다."));
        Long companySq = findCompanySq(userSq);

        //공연장 id로 hallEntity 조회
        HallEntity hallEntity = hallRepository.findById(request.showRequest().hallSq())
                .orElseThrow(() -> 
                        ShowExceptionFactory.showNotFound("존재하지 않는 공연장입니다.")
                );

        //mapper에 hallEntity 전달
        ShowEntity show = showMapper.requestToEntity(
                request.showRequest(), 
                companySq, 
                hallEntity
            );

        ShowEntity savedShow = showRepository.save(show);

        List<SeatEntity> allSeats =
                seatRepository.findAllByHall_SqOrderByAreaAscNumInAreaAsc(
                        savedShow.getHall().getSq()
                );

        if (allSeats.isEmpty()) {
            throw ShowExceptionFactory.showNotFound("공연 좌석이 존재하지 않습니다");
        }

        // 구역별 좌석 Group
        Map<String, List<SeatEntity>> seatsByArea =
                allSeats.stream().
                        collect(Collectors.groupingBy(SeatEntity::getArea));

        // 공연장 좌석 기반 ShowSeat 생성
        List<ShowSeatEntity> showSeats =
                request.seatPolicies().stream()
                        .flatMap(p -> createShowSeatsByPolicy(savedShow, p, seatsByArea).stream())
                        .toList();

        showSeatService.saveAll(showSeats);
        return savedShow.getSq();
    }

    // 공연 단독 등록
    @Override
    @Transactional
    public ShowResponse createShow(ShowRequest request, Long userSq) {
        // 권한 검증
        userValidator.validateEnterprise(userSq,ShowExceptionFactory.showForbidden("기업 또는 관리자만 공연장 등록이 가능합니다."));

        Long companySq = findCompanySq(userSq);
        //공연장 id로 hallEntity 조회
        HallEntity hallEntity = hallRepository.findById(request.hallSq())
                .orElseThrow(() -> ShowExceptionFactory.showNotFound("존재하지 않는 공연장 입니다."));

        //mapper에 hallEntity 전달
        ShowEntity show = showMapper.requestToEntity(request, companySq, hallEntity);
        showRepository.save(show);
        return showMapper.entityToResponse(show);
    }

    // 공연 조회
    @Override
    public ShowResponse readShowBySq(Long showSq) {
        ShowEntity show = findShowBySq(showSq);
        return showMapper.entityToResponse(show);
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

    // 좌석 기반 ShowSeat 생성
    private List<ShowSeatEntity> createShowSeatsByPolicy(
            ShowEntity show,
            SeatPolicyRequest policy,
            Map<String, List<SeatEntity>> seatsByArea
    ) {
        List<SeatEntity> seats = seatsByArea.get(policy.seatArea());
        if (seats == null || seats.isEmpty()) {
            throw ShowExceptionFactory.showNotFound(
                    "좌석 구역 [" + policy.seatArea() + "] 에 좌석이 존재하지 않습니다."
            );

        }

        ShowSeatGrade grade = ShowSeatGrade.valueOf(policy.grade());

        return seats.stream()
                .map(seat -> new ShowSeatEntity(grade,  policy.price(), show, seat))
                .toList();
    }

    // 회사 정보 조회
    private Long findCompanySq(Long userSq) {

        CompanyInfoResponse response =
                accountServiceClient.getMyCompanyInfo(userSq);

        if (response == null || response.companySq() == null) {
            throw ShowExceptionFactory.showForbidden(
                    "회사 정보가 존재하지 않아 공연을 생성할 수 없습니다."
            );
        }

        return response.companySq();
    }

}
