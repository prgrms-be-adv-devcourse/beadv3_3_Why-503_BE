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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallReqDto;
import io.why503.performanceservice.domain.concerthall.Model.Dto.ConcertHallResDto;
import io.why503.performanceservice.domain.concerthall.Model.Dto.Enum.ConcertHallStatus;
import io.why503.performanceservice.domain.concerthall.Model.Ett.ConcertHallEtt;
import io.why503.performanceservice.domain.concerthall.Repo.ConcertHallRepo;

import java.math.BigDecimal;

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

//        Authentication auth = SecurityContextHolder
//                .getContext()
//                .getAuthentication();
//
//        if (auth == null || !auth.isAuthenticated()) {
//            throw new IllegalArgumentException("로그인이 필요합니다.");
//        }
//
//        boolean isUserRole = auth.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
//
//        if (isUserRole) {
//            throw new IllegalArgumentException("기업 회원만 사용할 수 있습니다.");
//        }


        if ( reqDto.getConcertHallName() == null || reqDto.getConcertHallName().isBlank()) {
            throw new IllegalArgumentException("공연장 이름 필수입니다.");
        }
        if ( reqDto.getConcertHallPost() == null || reqDto.getConcertHallPost().isBlank()) {
            throw new IllegalArgumentException("우편 번호 이름 필수입니다.");
        }
        if ( reqDto.getConcertHallBasicAddr() == null || reqDto.getConcertHallBasicAddr().isBlank()) {
            throw new IllegalArgumentException("기본 주소 이름 필수입니다.");
        }
        if ( reqDto.getConcertHallDetailAddr() == null || reqDto.getConcertHallDetailAddr().isBlank()) {
            throw new IllegalArgumentException("상세 주소 이름 필수입니다.");
        }
        if ( reqDto.getConcertHallSeatScale() == null || reqDto.getConcertHallSeatScale() <= 0) {
            throw new IllegalArgumentException("좌석 수가  1이상이어야 합니다");
        }
        if ( reqDto.getConcertHallStructure() == null || reqDto.getConcertHallStructure().isBlank()) {
            throw new IllegalArgumentException("구조 이름 필수입니다.");
        }

        BigDecimal lat = reqDto.getConcertHallLatitude();
        BigDecimal lon = reqDto.getConcertHallLongitude();

        if (lat.compareTo(BigDecimal.valueOf(-90)) < 0 ||
                lat.compareTo(BigDecimal.valueOf(90)) > 0) {
            throw new IllegalArgumentException("위도는 -90 ~ 90 사이여야 합니다.");
        }

        if (lon.compareTo(BigDecimal.valueOf(-180)) < 0 ||
                lon.compareTo(BigDecimal.valueOf(180)) > 0) {
            throw new IllegalArgumentException("경도는 -180 ~ 180 사이여야 합니다.");
        }

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
