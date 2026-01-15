package io.why503.performanceservice.domain.showing.sv;

import io.why503.performanceservice.domain.showing.mapper.ShowingMapper;
import io.why503.performanceservice.domain.showing.model.dto.ShowingReqDto;
import io.why503.performanceservice.domain.showing.model.dto.ShowingResDto;
import io.why503.performanceservice.domain.showing.model.dto.ShowingStat;
import io.why503.performanceservice.domain.showing.model.ett.ShowingEtt;
import io.why503.performanceservice.domain.showing.repo.ShowingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowingSv {

    private final ShowingRepo showingRepo;
    private final ShowingMapper showingMapper;

    @Transactional
    public ShowingResDto createShowing(ShowingReqDto req) {

        //초기 생성 시엔 상태가 예매 가능이여야만 함
        if (req.getStat() != ShowingStat.AVAILABLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "회차 생성 시 상태는 '예매가능(AVAILABLE)'만 가능합니다.");
        }
        // 같은 공연 + 같은 시간의 회차가 이미 있는지 확인
        if (showingRepo.existsByShowAndDt(req.getShowSq(), req.getDt())) {
            // 이미 존재하면 409 Conflict 또는 400 Bad Request 에러 발생
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 해당 시간에 등록된 회차가 존재합니다.");
        }

        // 날짜 범위 계산
        LocalDateTime targetDateTime = req.getDt();
        LocalDate targetDate = targetDateTime.toLocalDate();
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);

        // 해당 날짜의 기존 회차 개수 조회 + 1 = 새로운 회차 번호
        Integer count = showingRepo.countByShowAndDtBetween(req.getShowSq(), startOfDay, endOfDay);
        Integer nextNo = count + 1;

        //엔티티 생성
        ShowingEtt showing = showingMapper.dtoToEtt(req, nextNo);

        //저장 및 반환
        ShowingEtt savedShowing = showingRepo.save(showing);
        return showingMapper.ettToDto(savedShowing);
    }

    /**
     * 특정 공연의 모든 회차 조회 (Admin/Management 용)
     * - 예매 가능 여부와 상관없이, 해당 공연의 전체 스케줄을 확인할 때 사용
     * - 예: 관리자 페이지의 회차 관리 목록
     */
    public List<ShowingResDto> getShowingListByShow(Long showSq) {
        return showingRepo.findByShow(showSq).stream()
                .map(showingMapper::ettToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 공연의 예매 가능한 회차만 조회 (User 용)
     * - 사용자가 예매를 위해 날짜/회차를 선택할 때 사용
     * - 예매 종료나 취소된 회차는 제외
     */
    public List<ShowingResDto> getAvailableShowingList(Long showSq) {
        return showingRepo.findByShowAndStat(showSq, ShowingStat.AVAILABLE).stream()
                .map(showingMapper::ettToDto)
                .collect(Collectors.toList());
    }
    /**
     * 회차 단건 상세 조회
     * - 특정 회차의 상세 정보를 보여줌
     * - 결제 전 최종 확인 페이지나, 좌석 선택 진입 전 정보 확인에 사용
     * - 데이터가 없으면 404 Not Found 에러를 발생
     */
    public ShowingResDto getShowingDetail(Long showingSq) {
        ShowingEtt showing = showingRepo.findById(showingSq)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 회차를 찾을 수 없습니다."));

        return showingMapper.ettToDto(showing);
    }

    //회차 상태 변경
    @Transactional
    public ShowingResDto patchShowingStat(Long showingSq, ShowingStat newStat) {
        // 존재 여부 확인 (없으면 404 에러 발생 후 중단)
        ShowingEtt showing = showingRepo.findById(showingSq)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 회차입니다."));

        // 존재한다면 상태 변경 수행
        showing.updateStat(newStat);

        return showingMapper.ettToDto(showing);
    }
}
