/**
 * Concert Hall Service Implementation
 * 공연장 관련 비즈니스 로직 구현체
 *
 * 처리 내용 :
 * - 공연장 등록 시 Entity 변환 및 저장
 * - 공연장 조회 시 Entity → DTO 변환
 *
 * 주의 사항 :
 * - 현재는 단순 CRUD 수준
 * - 추후 공연장 상태 관리, 권한 체크 로직 추가 가능
 */
package io.why503.performanceservice.domain.concerthall.Sv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallReqDto;
import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallResDto;
import io.why503.performanceservice.domain.concerthall.Model.Dto.Enum.ConcertHallStatus;
import io.why503.performanceservice.domain.concerthall.Model.Ett.ConcertHallEtt;
import io.why503.performanceservice.domain.concerthall.Repo.ConcertHallRepo;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConcertHallSvImpl implements ConcertHallSv {

    private final ConcertHallRepo concertHallRepo;

    /**
     * 공연장 등록
     *
     * 처리 흐름 :
     * 1. 요청 DTO → Entity 변환
     * 2. 공연장 Entity 저장
     *
     * @param reqDto 공연장 등록 요청 DTO
     */
    @Override
    @Transactional
    public void createConcertHall(ConcertHallReqDto reqDto) {

        ConcertHallEtt hall = ConcertHallEtt.builder()
                .concertHallName(reqDto.getConcertHallName())
                .concertHallPost(reqDto.getConcertHallPost())
                .concertHallBasicAddr(reqDto.getConcertHallBasicAddr())
                .concertHallDetailAddr(reqDto.getConcertHallDetailAddr())
                .concertHallSeatScale(reqDto.getConcertHallSeatScale())
                .concertHallStructure(reqDto.getConcertHallStructure())
                .concertHallLatitude(reqDto.getConcertHallLatitude())
                .concertHallLongitude(reqDto.getConcertHallLongitude())
                .build();

        hall.setConcertHallStatus(
            ConcertHallStatus.fromCode(reqDto.getConcertHallStat())
        );
        concertHallRepo.save(hall);
    }

    /**
     * 공연장 단건 조회
     *
     * 처리 흐름 :
     * 1. 공연장 식별자 기준 조회
     * 2. Entity → Response DTO 변환
     *
     * @param concertHallSq 공연장 식별자
     * @return 공연장 응답 DTO
     */
    @Override
    public ConcertHallResDto getConcertHall(Long concertHallSq) {

        ConcertHallEtt hall = concertHallRepo.findById(concertHallSq)
                .orElseThrow(() -> new IllegalArgumentException("concert hall not found"));

        return ConcertHallResDto.builder()
                .concertHallSq(hall.getConcertHallSq())
                .concertHallName(hall.getConcertHallName())
                .concertHallPost(hall.getConcertHallPost())
                .concertHallBasicAddr(hall.getConcertHallBasicAddr())
                .concertHallDetailAddr(hall.getConcertHallDetailAddr())
                .concertHallStatus(hall.getConcertHallStatus())
                .concertHallSeatScale(hall.getConcertHallSeatScale())
                .concertHallStructure(hall.getConcertHallStructure())
                .concertHallLatitude(hall.getConcertHallLatitude())
                .concertHallLongitude(hall.getConcertHallLongitude())
                .build();
    }
}
