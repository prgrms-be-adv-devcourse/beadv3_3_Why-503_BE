/**
 * Concert Hall Service Implementation
 * 공연장 관련 비즈니스 로직 구현체
 *
 * 처리 내용 :
 * - 공연장 등록
 * - 공연장 조회
 * - 공연장 등록 시 좌석 자동 생성
 *
 * 주의 사항 :
 * - 좌석은 공영장에 종속된 고정 자원
 * - 좌석 생성 실패 시 공연장 생성도 롤백 되어야 함
 */
package io.why503.performanceservice.domain.concerthall.service;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.why503.performanceservice.domain.concerthall.model.dto.ConcertHallRequest;
import io.why503.performanceservice.domain.concerthall.model.dto.ConcertHallResponse;
import io.why503.performanceservice.domain.concerthall.model.dto.enums.ConcertHallStatus;
import io.why503.performanceservice.domain.concerthall.model.entity.ConcertHallEntity;
import io.why503.performanceservice.domain.concerthall.repository.ConcertHallRepository;
import io.why503.performanceservice.domain.seat.model.dto.cmd.SeatAreaCreateCmd;
import io.why503.performanceservice.domain.seat.service.SeatService;

import java.math.BigDecimal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Transactional(readOnly = true)
public class ConcertHallServiceImpl implements ConcertHallService {

    private final ConcertHallRepository concertHallRepo;
    private final SeatService seatSv;

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
    public void createConcertHall(ConcertHallRequest reqDto) {

        //기업 회원이 아닌 경우 예외처리
//        boolean isUserRole = auth.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
//
//        if (isUserRole) {
//            throw new IllegalArgumentException("기업 회원만 사용할 수 있습니다.");
//        }

        //공연장 엔트리값 조건 추가
        /**
         * 공연장명
         */
        if ( reqDto.getConcertHallName() == null || reqDto.getConcertHallName().isBlank()) {
            throw new IllegalArgumentException("공연장 이름 필수입니다.");
        }
        /**
         * 우편번호
         */
        if ( reqDto.getConcertHallPost() == null || reqDto.getConcertHallPost().isBlank()) {
            throw new IllegalArgumentException("우편 번호 이름 필수입니다.");
        }
        /**
         * 기본 주소
         */
        if ( reqDto.getConcertHallBasicAddr() == null || reqDto.getConcertHallBasicAddr().isBlank()) {
            throw new IllegalArgumentException("기본 주소 이름 필수입니다.");
        }
        /**
         * 상세 주소
         */
        if ( reqDto.getConcertHallDetailAddr() == null || reqDto.getConcertHallDetailAddr().isBlank()) {
            throw new IllegalArgumentException("상세 주소 이름 필수입니다.");
        }
        /**
         * 공연장 총 좌석 수
         */
        if ( reqDto.getConcertHallSeatScale() == null || reqDto.getConcertHallSeatScale() < 50) {
            throw new IllegalArgumentException("좌석 수가  50이상이어야 합니다");
        }
        /**
         * 공연장 구조 정보
         */
        if ( reqDto.getConcertHallStructure() == null || reqDto.getConcertHallStructure().isBlank()) {
            throw new IllegalArgumentException("구조 이름 필수입니다.");
        }
        /**
         * 공연장 위도
         */
        if (reqDto.getConcertHallLatitude().compareTo(BigDecimal.valueOf(-90)) < 0 ||
                reqDto.getConcertHallLatitude().compareTo(BigDecimal.valueOf(90)) > 0) {
            throw new IllegalArgumentException("위도는 -90 ~ 90 사이여야 합니다.");
        }
        /**
         * 공연장 경도
         */
        if (reqDto.getConcertHallLongitude().compareTo(BigDecimal.valueOf(-180)) < 0 ||
                reqDto.getConcertHallLongitude().compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new IllegalArgumentException("경도는 -180 ~ 180 사이여야 합니다.");
        }

        ConcertHallEntity hall = ConcertHallEntity.builder()
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
    public ConcertHallResponse getConcertHall(Long concertHallSq) {

        ConcertHallEntity hall = concertHallRepo.findById(concertHallSq)
                .orElseThrow(() -> new IllegalArgumentException("concert hall not found"));

        return ConcertHallResponse.builder()
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
    
    /**
     * 관리자 입력 기반 좌석 생성 공연장 등록
     */
    @Override
    @Transactional
    public Long createWithCustomSeats(
            ConcertHallRequest reqDto,
            List<SeatAreaCreateCmd> seatAreaCmds
    ) {

        ConcertHallEntity hall = concertHallRepo.save(
                ConcertHallEntity.builder()
                        .concertHallName(reqDto.getConcertHallName())
                        .concertHallPost(reqDto.getConcertHallPost())
                        .concertHallBasicAddr(reqDto.getConcertHallBasicAddr())
                        .concertHallDetailAddr(reqDto.getConcertHallDetailAddr())
                        .concertHallStat(reqDto.getConcertHallStat())
                        .concertHallSeatScale(reqDto.getConcertHallSeatScale())
                        .concertHallStructure(reqDto.getConcertHallStructure())
                        .concertHallLatitude(reqDto.getConcertHallLatitude())
                        .concertHallLongitude(reqDto.getConcertHallLongitude())
                        .build()
        );

        // 공연장 생성 후 관리자 입력 기반 좌석 생성
        seatSv.createCustomSeats(hall, seatAreaCmds);

        return hall.getConcertHallSq();
    }
}
