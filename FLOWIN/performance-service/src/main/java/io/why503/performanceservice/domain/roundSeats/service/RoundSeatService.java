package io.why503.performanceservice.domain.roundSeats.service;


import io.why503.performanceservice.domain.concerthall.repository.ConcertHallRepository;
import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.round.repository.RoundRepository;
import io.why503.performanceservice.domain.roundSeats.client.PaymentClient;
import io.why503.performanceservice.domain.roundSeats.model.dto.*;
import io.why503.performanceservice.domain.roundSeats.model.entity.RoundSeatEntity;
import io.why503.performanceservice.domain.roundSeats.model.mapper.RoundSeatMapper;
import io.why503.performanceservice.domain.roundSeats.repository.RoundSeatRepository;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.repository.ShowSeatRepository;
import io.why503.performanceservice.global.client.accountservice.AccountServiceClient;
import io.why503.performanceservice.global.client.accountservice.dto.UserRoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoundSeatService {

    private final RoundSeatRepository roundSeatRepository;
    private final RoundRepository roundRepository; // 추가됨
    private final RoundSeatMapper roundSeatMapper;
    private final ShowSeatRepository showSeatRepository; //공연 좌석 정보 조회
    private final PaymentClient paymentClient;
    private final AccountServiceClient accountServiceClient;
    private final ConcertHallRepository concertHallRepository;

    //Redis 작업을 위한 템플릿 주입
    private final RedisTemplate<String, Object> redisTemplate;

    //회차 좌석 생성
    @Transactional
    public RoundSeatResponse createRoundSeat(Long userSq, RoundSeatRequest request) {

        // Account Service 호출하여 권한 확인
        UserRoleResponse roleInfo = accountServiceClient.getUserRole(userSq);

        // 기업(2) 권한 검증
        if (roleInfo == null || roleInfo.userRole() != 2) {
            throw new IllegalArgumentException("기업 권한이 없습니다. 회차 좌석은 기업 회원만 생성 가능합니다.");
        }

        // 권한 통과 시 기존 생성 로직 수행
        RoundEntity roundEntity = roundRepository.findById(request.roundSq())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다."));

        RoundSeatEntity entity = roundSeatMapper.dtoToEntity(request, roundEntity);
        RoundSeatEntity savedEntity = roundSeatRepository.save(entity);

        return roundSeatMapper.entityToDto(savedEntity);
    }


    //전체 조회
    public List<RoundSeatResponse> getRoundSeatList(Long roundSq) {
        // Repository 메서드명 변경
        // RoundSeaEntity에서 roundSq를 찾고 RoundEntity에서 roundSq와 일치하는 것을 찾아라
        List<RoundSeatEntity> entities = roundSeatRepository.findByRoundSq_RoundSq(roundSq);
        return convertToDtoList(entities);
    }


    //예매 가능 좌석 조회
    public List<RoundSeatResponse> getAvailableRoundSeatList(Long roundSq){
        // Repository 메서드명 변경
        List<RoundSeatEntity> entities = roundSeatRepository.findByRoundSq_RoundSqAndRoundSeatStatus(
                roundSq, RoundSeatStatus.AVAILABLE
        );
        return convertToDtoList(entities);
    }

    //상태 변경
    @Transactional
    public RoundSeatResponse patchRoundSeatStatus(Long userSq, Long roundSeatSq, RoundSeatStatus newStatus){

        // Account Service 호출하여 권한 확인
        UserRoleResponse roleInfo = accountServiceClient.getUserRole(userSq);

        // 기업(2) 권한 검증
        if (roleInfo == null || roleInfo.userRole() != 0) {
            throw new IllegalArgumentException("관리자 권한이 없습니다. 회차 좌석 상태는 관리자만 변경 가능합니다.");
        }

        RoundSeatEntity entity = roundSeatRepository.findById(roundSeatSq)
                //존재하지 않는 데이터 조회시
                .orElseThrow(()-> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));

        entity.updateStatus(newStatus);
        return roundSeatMapper.entityToDto(entity);

    }

    // 좌석 선점
    @Transactional
    public List<SeatReserveResponse> reserveSeats(Long userSq, List<Long> roundSeatSqs) {
        List<SeatReserveResponse> responseList = new ArrayList<>();

        // 1. 요청된 모든 회차 좌석 조회
        List<RoundSeatEntity> seats = roundSeatRepository.findAllById(roundSeatSqs);

        if (seats.size() != roundSeatSqs.size()) {
            throw new IllegalArgumentException("요청한 좌석 중 존재하지 않는 좌석이 있습니다.");
        }

        // 2. 공연 좌석 정보(가격, 등급) 조회 및 Map핑
        List<Long> showSeatIds = seats.stream()
                .map(seat -> seat.getShowSeatSq())
                .collect(Collectors.toList());

        Map<Long, ShowSeatEntity> showSeatMap = showSeatRepository.findAllById(showSeatIds).stream()
                .collect(Collectors.toMap(
                        showSeat -> showSeat.getShowSeatSq(),
                        showSeat -> showSeat
                ));

        // 3. [2] 공연장 이름 조회를 위한 ID 추출 (이 부분이 누락되어 'concertHallSqs' 미사용 경고가 떴던 것 같습니다)
        // RoundSeat -> Round -> Show -> ConcertHallSq (Long 타입)
        List<Long> concertHallIds = seats.stream()
                .map(seat -> seat.getRoundSq().getShow().getConcertHallSq())
                .distinct() // 중복 ID 제거
                .collect(Collectors.toList());

        // 4. [3] 공연장 이름 조회 (이 부분이 누락되어 'concertHallRepository' 미사용 경고가 떴던 것 같습니다)
        Map<Long, String> concertHallMap = concertHallRepository.findAllById(concertHallIds).stream()
                .collect(Collectors.toMap(
                        concertHall -> concertHall.getSq(),
                        concertHall -> concertHall.getName()
                ));

        // 5. 반복문 돌면서 데이터 조합
        for (RoundSeatEntity roundSeat : seats) {
            // 상태 변경 및 Redis 저장
            roundSeat.reserve();
            String key = "seat_owner:" + roundSeat.getRoundSeatSq();
            redisTemplate.opsForValue().set(key, String.valueOf(userSq));

            // 공연 좌석 정보 (가격, 등급 등)
            ShowSeatEntity showSeat = showSeatMap.get(roundSeat.getShowSeatSq());
            if (showSeat == null) {
                throw new IllegalArgumentException("연결된 공연 좌석 정보가 없습니다.");
            }

            // [4] 변수 선언 (이 부분이 누락되어 'symbol cannot be resolved' 에러가 났던 것 같습니다)
            // 아래 세 줄이 있어야 builder에서 showRequest.xxx, concertHallName 등을 쓸 수 있습니다.
            RoundEntity round = roundSeat.getRoundSq();
            ShowEntity show = round.getShow();
            String concertHallName = concertHallMap.get(show.getConcertHallSq());

            // 응답 객체 생성
            responseList.add(SeatReserveResponse.builder()
                    // 기존 필드
                    .roundSeatSq(roundSeat.getRoundSeatSq())
                    .roundSeatStatus(roundSeat.getRoundSeatStatus().name())
                    .price(showSeat.getPrice())
                    .grade(showSeat.getGrade().name())
                    .seatArea(showSeat.getSeat().getSeatArea())
                    .areaSeatNumber(showSeat.getSeat().getAreaSeatNo())

                    // 추가 정보 매핑
                    .showName(show.getName())              // 위에서 정의한 showRequest 변수 사용
                    .concertHallName(concertHallName)          // 위에서 정의한 concertHallName 변수 사용
                    .roundDate(round.getDateTime())             // 위에서 정의한 round 변수 사용
                    .build());
        }

        // paymentClient 호출 코드 삭제됨 (순환 호출 방지)

        return responseList;
    }

    //선점 해제
    @Transactional
    public void releaseSeats(List<Long> roundSeatSqs) {
        List<RoundSeatEntity> seats = roundSeatRepository.findAllById(roundSeatSqs);

        for (RoundSeatEntity seat : seats) {
            seat.release();
            // 선점이 해제되면 Redis의 소유권 정보도 삭제
            redisTemplate.delete("seat_owner:" + seat.getRoundSeatSq());
        }
    }

    //판매확정
    // 유저 식별자(userSq) 파라미터 추가 (본인 확인을 위함)
    @Transactional
    public void confirmSeats(Long userSq, List<Long> roundSeatSqs) {
        // Redis 검증 로직 (DB 변경 전에 먼저 수행)
        for (Long seatId : roundSeatSqs) {
            String key = "seat_owner:" + seatId;
            Object savedValue = redisTemplate.opsForValue().get(key);

            // Redis에 데이터가 없으면 (선점이 풀렸거나 없는 좌석) null반환
            if (savedValue == null) {
                throw new IllegalArgumentException("선점 정보가 존재하지 않습니다. 다시 예매해주세요.");
            }

            //Object를 String으로 변환해서 비교
            String savedUserSq = String.valueOf(savedValue);
            // 저장된 주인과 요청한 사람이 다르면
            if (!savedUserSq.equals(String.valueOf(userSq))) {
                throw new IllegalArgumentException("본인이 선점한 좌석만 결제할 수 있습니다.");
            }
        }

        // 검증 통과 시 DB 업데이트 진행
        List<RoundSeatEntity> seats = roundSeatRepository.findAllById(roundSeatSqs);

        for (RoundSeatEntity seat : seats) {
            seat.confirm();
            // 결제가 확정되었으므로 Redis 메모 삭제
            redisTemplate.delete("seat_owner:" + seat.getRoundSeatSq());
        }
    }

    //Entity 리스트 -> DTO 리스트 변환기
    private List<RoundSeatResponse> convertToDtoList(List<RoundSeatEntity> entities) {
        List<RoundSeatResponse> dtoList = new ArrayList<>();

        for (RoundSeatEntity entity : entities) {
            dtoList.add(roundSeatMapper.entityToDto(entity));
        }

        return dtoList;
    }

}