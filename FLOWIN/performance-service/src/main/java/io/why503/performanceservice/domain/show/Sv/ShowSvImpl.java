/**
 * Show Service Implementation
 * 공연 관련 비즈니스 로직 구현체
 *
 * 처리 내용 :
 * - 공연 등록
 *   - 공연 카테고리 코드 → Enum 변환
 *   - 공연장(concert_hall) 존재 여부 검증
 *   - 기본 공연 상태(SCHEDULED) 설정
 * - 공연 단건 조회
 * 
 * * 설계 포인트 :
 * - 트랜잭션 경계는 Service 계층에서 관리
 * - Entity는 DB 구조에 맞춰 int(code) 기반으로 저장
 * - 외부(API, DTO)와의 통신은 Enum 기반으로 처리
 * 
 * 주의 사항 :
 * - 현재는 로그인 / 권한 검증 미포함
 * - 추후 company 권한 검증, 공연 상태 전이 로직 추가 예정
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
import io.why503.performanceservice.domain.concerthall.Repo.ConcertHallRepo;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowSvImpl implements ShowSv {

    private final ShowRepo showRepo;
    private final ConcertHallRepo concertHallRepo;

    /**
     * 공연 등록
     *
     * 처리 흐름 :
     * 1. 공연 카테고리 코드 → Enum 변환
     * 2. 공연장 식별자 유효성 검증
     * 3. Show Entity 생성
     * 4. 기본 공연 상태(SCHEDULED) 설정
     * 5. 공연 저장
     *
     * @param reqDto 공연 등록 요청 DTO
     * @return 공연 등록 결과 DTO
     */
    @Override
    @Transactional
    public ShowResDto createShow(ShowReqDto reqDto) {

        // 1. 공연 카테고리 코드 검증 및 변환
        ShowCategory category = ShowCategory.fromCode(reqDto.getCategory());

        // 2. 공연장 존재 여부 검증
        boolean existsConcertHall =
                concertHallRepo.existsById(reqDto.getConcertHallSq());

        if (!existsConcertHall) {
            throw new IllegalArgumentException("invalid concert hall");
        }

        // 3. 공연 Entity 생성
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

        // 4. Enum → code 설정
        show.setCategory(category);
        show.setShowStatus(ShowStatus.SCHEDULED);

        // 5. 저장
        ShowEtt saved = showRepo.save(show);

        // 6. 응답 DTO 변환
        return ShowResDto.builder()
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
     *
     * 처리 흐름 :
     * 1. 공연 식별자 기준 조회
     * 2. Entity → Response DTO 변환
     *
     * @param showSq 공연 식별자
     * @return 공연 응답 DTO
     */
    @Override
    public ShowResDto getShow(Long showSq) {

        ShowEtt show = showRepo.findById(showSq)
                .orElseThrow(() -> new IllegalArgumentException("show not found"));

        return ShowResDto.builder()
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
}
