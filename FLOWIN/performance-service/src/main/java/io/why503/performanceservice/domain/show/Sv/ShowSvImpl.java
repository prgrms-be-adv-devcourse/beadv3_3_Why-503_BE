/**
 * Show Service Implementation
 * 공연(Show) 도메인의 비즈니스 로직 구현체
 *
 * 사용 목적 :
 * - 공연 등록 비즈니스 로직 처리
 * - 공연 단건 조회 비즈니스 로직 처리
 *
 * 설계 포인트 :
 * - 트랜잭션 경계는 Service 계층에서 관리
 * - Entity는 DB 구조에 맞춰 int(code) 기반으로 저장
 * - 외부(API, DTO)와의 통신은 Enum 기반으로 처리
 */
package io.why503.performanceservice.domain.show.Sv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.why503.performanceservice.domain.show.Model.Dto.ShowReqDto;
import io.why503.performanceservice.domain.show.Model.Dto.ShowResDto;
import io.why503.performanceservice.domain.show.Model.Ett.ShowEtt;
import io.why503.performanceservice.domain.show.Model.Enum.ShowCategory;
import io.why503.performanceservice.domain.show.Model.Enum.ShowStatus;
import io.why503.performanceservice.domain.show.Repo.ShowRepo;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본은 조회 전용 트랜잭션
public class ShowSvImpl implements ShowSv {

    private final ShowRepo showRepo;

    /**
     * 공연 등록
     *
     * 처리 흐름 :
     * 1. 요청 DTO의 category(code)를 Enum으로 변환
     * 2. 공연 기본 정보 Entity 생성
     * 3. Enum → int(code) 변환 후 Entity에 세팅
     * 4. DB 저장
     * 5. 저장 결과를 Response DTO로 변환하여 반환
     *
     * 주의 사항 :
     * - concertHallSq, companySq 는 FK 제약 조건 존재
     * - 존재하지 않는 공연장 식별자 전달 시 DB 무결성 오류(500) 발생 가능
     */
    @Override
    @Transactional // 등록이므로 쓰기 트랜잭션
    public ShowResDto createShow(ShowReqDto reqDto) {

        // 요청으로 받은 category 코드 → Enum 변환
        ShowCategory category = ShowCategory.fromCode(reqDto.getCategory());

        // 공연 Entity 생성 (enum, status 제외)
        ShowEtt show = ShowEtt.builder()
                .showName(reqDto.getShowName())
                .startDate(reqDto.getStartDate())
                .endDate(reqDto.getEndDate())
                .openDt(reqDto.getOpenDt())
                .showTime(reqDto.getShowTime())
                .viewingAge(reqDto.getViewingAge())
                .concertHallSq(reqDto.getConcertHallSq())
                .companySq(reqDto.getCompanySq())
                .build();

        // Enum → DB 저장용 int(code) 세팅
        show.setCategory(category);

        // 신규 공연은 기본 상태를 "공연 예정(SCHEDULED)" 으로 설정
        show.setShowStatus(ShowStatus.SCHEDULED);

        // 공연 저장
        ShowEtt saved = showRepo.save(show);

        // 저장 결과를 Response DTO로 변환
        return ShowResDto.builder()
                .showSq(saved.getShowSq())
                .showName(saved.getShowName())
                .startDate(saved.getStartDate())
                .endDate(saved.getEndDate())
                .openDt(saved.getOpenDt())
                .showTime(saved.getShowTime())
                .viewingAge(saved.getViewingAge())
                .category(saved.getCategoryEnum())   // code → Enum 변환
                .showStat(saved.getShowStatus())     // code → Enum 변환
                .concertHallSq(saved.getConcertHallSq())
                .companySq(saved.getCompanySq())
                .build();
    }

    /**
     * 공연 단건 조회
     *
     * @param showSq 조회할 공연 식별자
     * @return 공연 상세 정보
     *
     * 예외 처리 :
     * - 존재하지 않는 공연 식별자 요청 시 IllegalArgumentException 발생
     */
    @Override
    public ShowResDto getShow(Long showSq) {

        // 공연 조회 (없을 경우 예외 발생)
        ShowEtt show = showRepo.findById(showSq)
                .orElseThrow(() -> new IllegalArgumentException("show not found"));

        // 조회 결과를 Response DTO로 변환
        return ShowResDto.builder()
                .showSq(show.getShowSq())
                .showName(show.getShowName())
                .startDate(show.getStartDate())
                .endDate(show.getEndDate())
                .openDt(show.getOpenDt())
                .showTime(show.getShowTime())
                .viewingAge(show.getViewingAge())
                .category(show.getCategoryEnum())   // DB code → Enum
                .showStat(show.getShowStatus())     // DB code → Enum
                .concertHallSq(show.getConcertHallSq())
                .companySq(show.getCompanySq())
                .build();
    }
}
