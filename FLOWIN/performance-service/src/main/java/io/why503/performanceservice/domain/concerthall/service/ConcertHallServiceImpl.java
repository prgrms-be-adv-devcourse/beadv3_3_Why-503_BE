/**
 * Concert Hall Service Implementation
 * 공연장 관련 비즈니스 로직 구현체
 * 처리 내용 :
 * - 공연장 등록
 * - 공연장 조회
 * - 공연장 등록 시 좌석 자동 생성
 * 주의 사항 :
 * - 좌석은 공영장에 종속된 고정 자원
 * - 좌석 생성 실패 시 공연장 생성도 롤백 되어야 함
 */
package io.why503.performanceservice.domain.concerthall.service;

import java.util.List;

import io.why503.performanceservice.util.mapper.ConcertHallMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.why503.performanceservice.domain.concerthall.model.dto.request.ConcertHallRequest;
import io.why503.performanceservice.domain.concerthall.model.dto.response.ConcertHallResponse;
import io.why503.performanceservice.domain.concerthall.model.dto.enums.ConcertHallStatus;
import io.why503.performanceservice.domain.concerthall.model.entity.ConcertHallEntity;
import io.why503.performanceservice.domain.concerthall.repository.ConcertHallRepository;
import io.why503.performanceservice.domain.seat.model.dto.cmd.SeatAreaCreateCmd;
import io.why503.performanceservice.domain.seat.service.SeatService;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
@Transactional(readOnly = true)
public class ConcertHallServiceImpl implements ConcertHallService {

    private final ConcertHallRepository concertHallRepo;
    private final SeatService seatSv;
    private final ConcertHallMapper concertHallMapper;
    /**
     * 공연장 등록
     * 처리 흐름 :
     * 1. 요청 DTO → Entity 변환
     * 2. 공연장 Entity 저장
     * @param request 공연장 등록 요청 DTO
     */
    @Override
    @Transactional
    public void createConcertHall(ConcertHallRequest request) {

        //기업 회원이 아닌 경우 예외처리
//        boolean isUserRole = auth.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
//
//        if (isUserRole) {
//            throw new IllegalArgumentException("기업 회원만 사용할 수 있습니다.");
//        }

        //공연장 엔트리값 조건 추가
        //공연장명
        if (request.concertHallName() == null || request.concertHallName().isBlank()) {
            throw new IllegalArgumentException("공연장 이름 필수입니다.");
        }
        //우편번호
        if (request.concertHallPost() == null || request.concertHallPost().isBlank()) {
            throw new IllegalArgumentException("우편 번호 이름 필수입니다.");
        }
        //기본 주소
        if ( request.concertHallBasicAddr() == null || request.concertHallBasicAddr().isBlank()) {
            throw new IllegalArgumentException("기본 주소 이름 필수입니다.");
        }
        //상세 주소
        if ( request.concertHallDetailAddr() == null || request.concertHallDetailAddr().isBlank()) {
            throw new IllegalArgumentException("상세 주소 이름 필수입니다.");
        }
        //공연장 총 좌석 수
        if ( request.concertHallSeatScale() == null || request.concertHallSeatScale() <= 50) {
            throw new IllegalArgumentException("좌석 수가  50이상이어야 합니다");
        }
        //공연장 구조 정보
        if ( request.concertHallStructure() == null || request.concertHallStructure().isBlank()) {
            throw new IllegalArgumentException("구조 이름 필수입니다.");
        }
        //공연장 위도
        if (request.concertHallLatitude().compareTo(BigDecimal.valueOf(-90)) < 0 ||
                request.concertHallLatitude().compareTo(BigDecimal.valueOf(90)) > 0) {
            throw new IllegalArgumentException("위도는 -90 ~ 90 사이여야 합니다.");
        }
        //공연장 경도
        if (request.concertHallLongitude().compareTo(BigDecimal.valueOf(-180)) < 0 ||
                request.concertHallLongitude().compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new IllegalArgumentException("경도는 -180 ~ 180 사이여야 합니다.");
        }

        ConcertHallEntity hall = concertHallMapper.requestToEntity(request);

        hall.setConcertHallStatus(
            ConcertHallStatus.fromCode(request.concertHallStat())
        );
        concertHallRepo.save(hall);
    }

    /**
     * 공연장 단건 조회
     * 처리 흐름 :
     * 1. 공연장 식별자 기준 조회
     * 2. Entity → Response DTO 변환
     * @param concertHallSq 공연장 식별자
     * @return 공연장 응답 DTO
     */
    @Override
    public ConcertHallResponse getConcertHall(Long concertHallSq) {

        ConcertHallEntity hall = concertHallRepo.findById(concertHallSq)
                .orElseThrow(() -> new IllegalArgumentException("concert hall not found"));

        return concertHallMapper.entityToResponse(hall);
    }
    
    /**
     * 관리자 입력 기반 좌석 생성 공연장 등록
     */
    @Override
    @Transactional
    public Long createWithCustomSeats(
            ConcertHallRequest request,
            List<SeatAreaCreateCmd> seatAreaCmds
    ) {

        ConcertHallEntity hall = concertHallMapper.requestToEntity(request);
        concertHallRepo.save(hall);

        // 공연장 생성 후 관리자 입력 기반 좌석 생성
        seatSv.createCustomSeats(hall, seatAreaCmds);

        return hall.getConcertHallSq();
    }
}
