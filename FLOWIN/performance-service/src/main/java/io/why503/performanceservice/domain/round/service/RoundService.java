package io.why503.performanceservice.domain.round.service;

import io.why503.performanceservice.domain.round.mapper.RoundMapper;
import io.why503.performanceservice.domain.round.model.dto.RoundRequest;
import io.why503.performanceservice.domain.round.model.dto.RoundResponse;
import io.why503.performanceservice.domain.round.model.dto.RoundStatus;
import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.round.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoundService {

    private final RoundRepository roundRepository;
    private final RoundMapper roundMapper;

    @Transactional
    public RoundResponse createRound(RoundRequest request) {

        //초기 생성 시엔 상태가 예매 가능이여야만 함
        if (request.getRoundStatus() != RoundStatus.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "회차 생성 시 상태는 '예매가능(AVAILABLE)'만 가능합니다.");
        }
        // 같은 공연 + 같은 시간의 회차가 이미 있는지 확인
        if (roundRepository.existsByShowSqAndRoundDt(request.getShowSq(), request.getRoundDt())) {
            // 이미 존재하면 409 Conflict 또는 400 Bad Request 에러 발생
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 해당 시간에 등록된 회차가 존재합니다.");
        }

        // 날짜 범위 계산
        LocalDateTime targetDateTime = request.getRoundDt();
        LocalDate targetDate = targetDateTime.toLocalDate();
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

        // 해당 날짜의 기존 회차 리스트 조회
        List<RoundEntity> roundList = roundRepository.findAllByShowSqAndRoundDtBetween(request.getShowSq(), startOfDay, endOfDay);

        // 새 회차 엔티티 생성 (번호는 0이나 임시값으로 생성)
        RoundEntity newEntity = roundMapper.dtoToEntity(request, 0);

        // 리스트에 새 회차 추가
        roundList.add(newEntity);

        // 시간순(roundDt)으로 정렬
        roundList.sort((r1, r2) -> r1.getRoundDt().compareTo(r2.getRoundDt()));

        // 정렬된 순서대로 회차 번호(roundNum) 다시 부여 (1번부터 시작)
        for (int i = 0; i < roundList.size(); i++) {
            roundList.get(i).updateRoundNum(i + 1);
        }

        //일괄 저장 (기존 엔티티는 Update, 새 엔티티는 Insert 됨)
        roundRepository.saveAll(roundList);

        return roundMapper.entityToDto(newEntity);
    }

    /**
     * 특정 공연의 모든 회차 조회 (관리자, 기업회원용)
     * - 예매 가능 여부와 상관없이, 해당 공연의 전체 스케줄을 확인할 때 사용
     */
    public List<RoundResponse> getRoundListByShow(Long showSq) {
        //DB에서 리스트를 꺼냄
        List<RoundEntity> entities = roundRepository.findByShowSq(showSq);

        return roundMapper.entityListToDtoList(entities);
    }

    /**
     * 특정 공연의 예매 가능한 회차만 조회 (User 용)
     * - 사용자가 예매를 위해 날짜/회차를 선택할 때 사용
     * - 예매 종료나 취소된 회차는 제외
     */
    public List<RoundResponse> getAvailableRoundList(Long showSq) {
        // DB에서 예매가능 상태인 것만 꺼냄
        List<RoundEntity> entities = roundRepository.findByShowSqAndRoundStatus(showSq, RoundStatus.AVAILABLE);

        return roundMapper.entityListToDtoList(entities);
    }

    /**
     * 회차 단건 상세 조회
     * - 특정 회차의 상세 정보를 보여줌
     * - 결제 전 최종 확인 페이지나, 좌석 선택 진입 전 정보 확인에 사용
     * - 데이터가 없으면 404 Not Found 에러를 발생
     */
    public RoundResponse getRoundDetail(Long roundSq) {
        RoundEntity entity = roundRepository.findById(roundSq)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 회차를 찾을 수 없습니다."));

        return roundMapper.entityToDto(entity);
    }

    //회차 상태 변경
    @Transactional
    public RoundResponse patchRoundStat(Long roundSq, RoundStatus newStatus) {
        // 존재 여부 확인
        RoundEntity entity = roundRepository.findById(roundSq)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회차입니다."));

        // 상태 변경 수행
        entity.updateStat(newStatus);

        return roundMapper.entityToDto(entity);
    }
}