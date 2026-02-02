package io.why503.performanceservice.domain.round.service;

import io.why503.performanceservice.domain.round.model.dto.request.RoundRequest;
import io.why503.performanceservice.domain.round.model.dto.response.RoundResponse;
import io.why503.performanceservice.domain.round.model.enums.RoundStatus;
import java.util.List;


public interface RoundService {

    //회차 생성
    RoundResponse createRound(Long userSq, RoundRequest request);

    //특정 공연의 모든 회차 조회
    //예매 가능 여부와 상관없이 해당 공연의 전체 스케줄 확인
    List<RoundResponse> getRoundListByShow(Long userSq, Long showSq);

    //예매 가능한 회차만 조회
    List<RoundResponse> getAvailableRoundList(Long showSq);

    //회차 단건 상세 조회
    RoundResponse getRoundDetail(Long roundSq);

    //회차 상태 변경
    RoundResponse patchRoundStat(Long userSq, Long roundSq, RoundStatus newStatus);

}
