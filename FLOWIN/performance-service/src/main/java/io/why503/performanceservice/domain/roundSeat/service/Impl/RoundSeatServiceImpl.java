package io.why503.performanceservice.domain.roundSeat.service.Impl;

import io.why503.performanceservice.domain.hall.model.entity.HallEntity;
import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.round.model.enums.RoundStatus;
import io.why503.performanceservice.domain.round.repository.RoundRepository;
import io.why503.performanceservice.domain.round.util.RoundExceptionFactory;
import io.why503.performanceservice.domain.roundSeat.model.dto.request.RoundSeatRequest;
import io.why503.performanceservice.domain.roundSeat.model.dto.response.RoundSeatResponse;
import io.why503.performanceservice.domain.roundSeat.model.dto.response.SeatReserveResponse;
import io.why503.performanceservice.domain.roundSeat.model.entity.RoundSeatEntity;
import io.why503.performanceservice.domain.roundSeat.model.enums.RoundSeatStatus;
import io.why503.performanceservice.domain.roundSeat.repository.RoundSeatRepository;
import io.why503.performanceservice.domain.roundSeat.service.RoundSeatService;
import io.why503.performanceservice.domain.roundSeat.util.RoundSeatExceptionFactory;
import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.repository.ShowSeatRepository;
import io.why503.performanceservice.global.validator.UserValidator;
import io.why503.performanceservice.util.mapper.RoundSeatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoundSeatServiceImpl implements RoundSeatService {

    private final RoundSeatRepository roundSeatRepository;
    private final RoundRepository roundRepository;
    private final RoundSeatMapper roundSeatMapper;
    private final ShowSeatRepository showSeatRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserValidator userValidator;


    //회차 좌석 생성
    @Override
    @Transactional
    public RoundSeatResponse createRoundSeat(Long userSq, RoundSeatRequest request) {
        userValidator.validateEnterprise(userSq,RoundSeatExceptionFactory.roundSeatForbidden("기업 또는 관리자만 공연장 등록이 가능합니다."));

        //회차 정보 조회
        RoundEntity roundEntity = roundRepository.findById(request.roundSq())
                .orElseThrow(() -> RoundSeatExceptionFactory.roundSeatNotFound("존재하지 않은 회차 입니다."));
        //이미 종료된 회차에는 좌석을 추가할 수 없음
        if (roundEntity.getStatus() == RoundStatus.CLOSED) {
            throw RoundSeatExceptionFactory.roundSeatBadRequest("이미 종료된 회차에는 좌석을 생성할 수 없습니다.");
        }
        //해당 회차에 이미 동일한 공연 좌석이 등록되어 있는지 확인
        boolean exists = roundSeatRepository.existsByRoundAndShowSeatSq(
                roundEntity, //회차에서
                request.showSeatSq() //좌석번호를 가진 데이터가 있는지
        );

        // 해당 회차에 이미 같은 좌석이 등록되어져 있다면 에러 발생
        if (exists) {
            throw RoundSeatExceptionFactory.roundSeatConflict("해당 회차에 이미 등록된 좌석입니다.");
        }
        //회차 좌석 엔티티 생성
        RoundSeatEntity entity = roundSeatMapper.requestToEntity(request, roundEntity);
        //DB에 저장
        RoundSeatEntity savedEntity = roundSeatRepository.save(entity);

        return roundSeatMapper.entityToResponse(savedEntity);
    }

    //전체 좌석 조회
    @Override
    public List<RoundSeatResponse> getRoundSeatList(Long userSq,Long roundSq) {
        // userValidator.validateEnterprise(userSq,RoundExceptionFactory.roundForbidden(""));
        //해당 회차에 속한 모든 좌석을 가져옴
        List<RoundSeatEntity> entities = roundSeatRepository.findByRound_Sq(roundSq);
        return roundSeatMapper.dbToResponseList(entities);
    }

    //예매 가능 좌석 조회
    @Override
    public List<RoundSeatResponse> getAvailableRoundSeatList(Long roundSq) {
        //회차 좌석 상태가 AVAILABLE 좌석만 DB에서 조회
        List<RoundSeatEntity> entities = roundSeatRepository.findByRound_SqAndStatus(
                roundSq, RoundSeatStatus.AVAILABLE
        );
        return roundSeatMapper.dbToResponseList(entities);
    }

    //좌석 상태 변경
    @Override
    @Transactional
    public RoundSeatResponse patchRoundSeatStatus(Long userSq, Long roundSeatSq, RoundSeatStatus newStatus) {
        userValidator.validateEnterprise(userSq,RoundSeatExceptionFactory.roundSeatForbidden("기업 또는 관리자만 공연장 등록이 가능합니다."));

        //변경할 좌석을 DB에서 찾음
        RoundSeatEntity entity = roundSeatRepository.findById(roundSeatSq)
                .orElseThrow(() -> RoundSeatExceptionFactory.roundSeatNotFound("존재하지 않는 회차 좌석입니다."));
        //종료된 회차는 상태 변경 불가
        if (entity.getRound().getStatus() == RoundStatus.CLOSED) {
            throw RoundSeatExceptionFactory.roundSeatBadRequest("종료된 회차의 좌석 상태는 변경할 수 없습니다.");
        }
        //상태 변경
        entity.updateStatus(newStatus);
        return roundSeatMapper.entityToResponse(entity);
    }

    //좌석 선점 Redis 사용
    @Override
    @Transactional
    public List<SeatReserveResponse> reserveSeats(Long userSq, List<Long> roundSeatSqs) {
        List<SeatReserveResponse> responseList = new ArrayList<>();

        //빈 리스트가 들어오면 빈 값 리턴
        if (roundSeatSqs == null || roundSeatSqs.isEmpty()) {
            return responseList;
        }

        // 좌석 조회
        List<RoundSeatEntity> seats = roundSeatRepository.findAllById(roundSeatSqs);

        if (seats.size() != roundSeatSqs.size()) {
            throw RoundSeatExceptionFactory.roundSeatNotFound("요청하신 좌석 중 존재하지 않는 좌석이 포함되어 있습니다.");        }

        for (RoundSeatEntity seat : seats) {
            if (seat.getStatus() != RoundSeatStatus.AVAILABLE) {
                throw RoundSeatExceptionFactory.roundSeatConflict("이미 선택되었거나 예매가 불가능한 좌석 입니다.");            }
        }
        // 같은 회차의 좌석들이므로 공연장 정보를 얻어올때 첫번째 좌석의 정보를 이용
        RoundSeatEntity firstSeat = seats.get(0);

        // 좌석을 통해 공연장 sq 찾기
        HallEntity hallEntity = firstSeat.getRound().getShow().getHall();

        // 공연장 이름 조회
        String fixedConcertHallName = hallEntity.getName();
        // 좌석 등급 추출
        List<Long> showSeatSqs = new ArrayList<>();
        for (RoundSeatEntity seat : seats) {
            showSeatSqs.add(seat.getShowSeatSq());
        }

        List<ShowSeatEntity> showSeatEntities = showSeatRepository.findAllById(showSeatSqs);
        Map<Long, ShowSeatEntity> showSeatMap = new HashMap<>();
        for (ShowSeatEntity showSeat : showSeatEntities) {
            showSeatMap.put(showSeat.getSq(), showSeat);
        }

        // 좌석 선점 루프
        for (RoundSeatEntity roundSeat : seats) {
            // DB 상태 변경 (AVAILABLE -> RESERVED)
            roundSeat.reserve();

            String key = "seat_owner:" + roundSeat.getSq();

            // Redis에 저장할 때 유효 시간 설정
            // 10분 뒤 자동 취소
            //-> bookingRepository.save()코드가 실행되기전 에러 발생시 회차 좌석의 redis엔 여전히 회원정보가 남아 있으므로 설정
            redisTemplate.opsForValue().set(key, String.valueOf(userSq), Duration.ofMinutes(10));

            // 좌석 등급/가격 정보 Map에서 꺼내기
            ShowSeatEntity showSeat = showSeatMap.get(roundSeat.getShowSeatSq());
            if (showSeat == null) {
                throw RoundSeatExceptionFactory.roundSeatConflict(
                    "좌석 등급 정보가 존재하지 않습니다. (showSeatSq=" + roundSeat.getShowSeatSq() + ")"
                );
            }

            // Response 생성
            SeatReserveResponse response = roundSeatMapper.entityToReserveResponse(
                    roundSeat,
                    showSeat,
                    fixedConcertHallName
            );

            responseList.add(response);
        }

        return responseList;
    }

    //선점 해제 ,redis락 삭제
    @Override
    @Transactional
    public void releaseSeats(List<Long> roundSeatSqs) {
        //해제할 좌석을 찾아옴
        List<RoundSeatEntity> seats = roundSeatRepository.findAllById(roundSeatSqs);

        for (RoundSeatEntity seat : seats) {
           //좌석 상태를 예매가능으로 되돌림
            seat.release();
            redisTemplate.delete("seat_owner:" + seat.getSq());
        }
    }

    //판매 확정
    @Override
    @Transactional
    public void confirmSeats(Long userSq, List<Long> roundSeatSqs) {
        //선점 정보 가져옴
        for (Long seatId : roundSeatSqs) {
            String key = "seat_owner:" + seatId;
            Object savedValue = redisTemplate.opsForValue().get(key);
            //선점 시간이 만료되어 선점상태가 아님
            if (savedValue == null) {
                throw RoundSeatExceptionFactory.roundSeatBadRequest("선점 가능 시간이 만료되었거나 유효하지 않은 예약입니다.");
            }
            String savedUserSq = String.valueOf(savedValue);
            //내가 선점된 좌석만 결제 가능
            if (!savedUserSq.equals(String.valueOf(userSq))) {
                throw RoundSeatExceptionFactory.roundSeatForbidden("본인이 선점한 좌석만 확정(결제)할 수 있습니다.");
            }
        }

        List<RoundSeatEntity> seats = roundSeatRepository.findAllById(roundSeatSqs);
        //선택한 좌석의 개수와 확정하려는 좌석의 개수가 다름
        if (seats.size() != roundSeatSqs.size()) {
            throw RoundSeatExceptionFactory.roundSeatNotFound("요청하신 좌석 중 존재하지 않는 좌석이 포함되어 있습니다.");
        }

        for (RoundSeatEntity seat : seats) {
            seat.confirm();
            redisTemplate.delete("seat_owner:" + seat.getSq());
        }
    }

    @Override
    public List<SeatReserveResponse> getRoundSeatDetails(List<Long> roundSeatSqs) {
        if (roundSeatSqs == null || roundSeatSqs.isEmpty()) {
            return Collections.emptyList();
        }

        List<RoundSeatEntity> roundSeats = roundSeatRepository.findAllById(roundSeatSqs);
        if (roundSeats.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> showSeatSqs = new ArrayList<>();
        for (RoundSeatEntity rs : roundSeats) {
            showSeatSqs.add(rs.getShowSeatSq());
        }

        List<ShowSeatEntity> showSeats = showSeatRepository.findAllById(showSeatSqs);
        Map<Long, ShowSeatEntity> showSeatMap = new HashMap<>();
        for (ShowSeatEntity ss : showSeats) {
            showSeatMap.put(ss.getSq(), ss);
        }

        RoundSeatEntity firstSeat = roundSeats.get(0);
        String hallName = firstSeat.getRound().getShow().getHall().getName();

        List<SeatReserveResponse> responseList = new ArrayList<>();

        for (RoundSeatEntity rs : roundSeats) {
            ShowSeatEntity ss = showSeatMap.get(rs.getShowSeatSq());
            if (ss == null) continue;

            SeatReserveResponse response = roundSeatMapper.entityToReserveResponse(
                    rs,
                    ss,
                    hallName
            );

            responseList.add(response);
        }

        return responseList;
    }
}