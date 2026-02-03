package io.why503.performanceservice.domain.round.service.Impl;

import io.why503.performanceservice.domain.round.model.dto.request.RoundRequest;
import io.why503.performanceservice.domain.round.model.dto.response.RoundResponse;
import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.round.model.enums.RoundStatus;
import io.why503.performanceservice.domain.round.repository.RoundRepository;
import io.why503.performanceservice.domain.round.service.RoundService;
import io.why503.performanceservice.domain.roundSeat.model.entity.RoundSeatEntity;
import io.why503.performanceservice.domain.roundSeat.model.enums.RoundSeatStatus;
import io.why503.performanceservice.domain.roundSeat.repository.RoundSeatRepository;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import io.why503.performanceservice.domain.show.service.ShowService;
import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.service.ShowSeatService;
import io.why503.performanceservice.global.error.ErrorCode;
import io.why503.performanceservice.global.error.exception.BusinessException;
import io.why503.performanceservice.global.validator.UserValidator;
import io.why503.performanceservice.util.mapper.RoundMapper;
import io.why503.performanceservice.util.mapper.RoundSeatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoundServiceImpl implements RoundService {

    private final RoundRepository roundRepository;
    private final RoundSeatRepository roundSeatRepository;
    private final RoundMapper roundMapper;
    private final RoundSeatMapper roundSeatMapper;
    private final ShowService showService;
    private final ShowSeatService showSeatService;
    private final UserValidator userValidator;

    //회차 생성
    @Override
    @Transactional
    public RoundResponse createRound(Long userSq, RoundRequest request) {

        // 권한 검증
        userValidator.validateEnterprise(userSq);

        ShowEntity show = showService.findShowBySq(request.showSq());

        // 초기 생성 시엔 상태가 예매 대기여야 함
        if (request.roundStatus() != RoundStatus.WAIT) {
            throw new BusinessException(ErrorCode.ROUND_INITIAL_STATUS_MUST_BE_WAIT);
        }

        // 이미 등록된 시간인지 확인
        if (roundRepository.existsByShowAndStartDt(show, request.roundDt())) {
            throw new BusinessException(ErrorCode.ROUND_CONFLICT);
        }

        // 날짜 범위 계산
        LocalDateTime targetDateTime = request.roundDt();
        LocalDate targetDate = targetDateTime.toLocalDate();
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

        // 해당 날짜의 기존 회차 리스트 조회 및 새 회차 추가
        List<RoundEntity> roundList = roundRepository.findAllByShowAndStartDtBetween(show, startOfDay, endOfDay);
        RoundEntity newEntity = roundMapper.dtoToEntity(request, show, 0);
        roundList.add(newEntity);

        // 시간 순서대로 정렬
        roundList.sort((r1, r2) -> r1.getStartDt().compareTo(r2.getStartDt()));

        // 회차 번호 재부여
        for (int i = 0; i < roundList.size(); i++) {
            roundList.get(i).updateRoundNum(i + 1);
        }

        // 일괄 저장
        roundRepository.saveAll(roundList);

        return roundMapper.entityToDto(newEntity);
    }

    //회차, 회차 좌석 일괄 생성
    @Override
    @Transactional
    public RoundResponse createRoundWithSeats(Long userSq, RoundRequest request) {

        // createRound를 재사용하여 회차를 먼저 만듦
        RoundResponse roundResponse = this.createRound(userSq, request);

        //회차 엔티티를 조회
        RoundEntity round = roundRepository.findById(roundResponse.roundSq())
                .orElseThrow(() -> new BusinessException(ErrorCode.ROUND_NOT_FOUND));

        //ShowSeatService 이용해 공연 좌석 등급/가격을 가져옴
        List<ShowSeatEntity> showSeats = showSeatService.getSeatsByShowSq(request.showSq());

        if (showSeats.isEmpty()) {
            throw new BusinessException(ErrorCode.SEAT_NOT_FOUND);
        }

        //ShowSeat -> RoundSeat 변환
        List<RoundSeatEntity> roundSeats = roundSeatMapper.showSeatListToRoundSeatList(showSeats, round);

        // 좌석 일괄 저장
        roundSeatRepository.saveAll(roundSeats);

        return roundResponse;
    }

    //특정 공연의 모든 회차 조회
    @Override
    public List<RoundResponse> getRoundListByShow(Long userSq, Long showSq) {
        //기업,관리자 회원인지 확인
        userValidator.validateEnterprise(userSq);
        //요청된 공연 정보를 찾음
        ShowEntity show = showService.findShowBySq(showSq);
        //해당 공연에 소속된 모든 회차를 가져옴
        List<RoundEntity> entities = roundRepository.findByShow(show);

        return roundMapper.entityListToDtoList(entities);
    }

    //예매 가능한 회차 목록 조회
    @Override
    public List<RoundResponse> getAvailableRoundList(Long showSq) {
        //요청된 공연 정보를 찾음
        ShowEntity show = showService.findShowBySq(showSq);
        //해당 공연의 회차중 상태가 AVAILABLE인 것만 DB에서 가져옴
        List<RoundEntity> entities = roundRepository.findByShowAndStatus(show, RoundStatus.AVAILABLE);
        return roundMapper.entityListToDtoList(entities);
    }

    //회차 단건 상세 조회
    @Override
    public RoundResponse getRoundDetail(Long roundSq) {
        return roundMapper.entityToDto(findRoundBySq(roundSq));
    }

    //회차 상태 변경
    @Override
    @Transactional
    public RoundResponse patchRoundStat(Long userSq, Long roundSq, RoundStatus newStatus) {
        userValidator.validateEnterprise(userSq);
        //변경할 회차를 DB에서 찾아옴
        RoundEntity entity = findRoundBySq(roundSq);
        //해당 회차의 상태를 새로운 상태로 변경
        entity.updateStat(newStatus);
        return roundMapper.entityToDto(entity);
    }

    // 회차 ID로 엔티티를 찾음
    private RoundEntity findRoundBySq(Long roundSq) {
        return roundRepository.findById(roundSq)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROUND_NOT_FOUND));
    }

}
