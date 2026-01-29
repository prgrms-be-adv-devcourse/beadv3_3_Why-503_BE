package io.why503.performanceservice.domain.roundSeats.service.Impl;

import io.why503.performanceservice.domain.concerthall.model.entity.ConcertHallEntity;
import io.why503.performanceservice.domain.concerthall.repository.ConcertHallRepository;
import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.round.repository.RoundRepository;
import io.why503.performanceservice.domain.roundSeats.model.dto.request.RoundSeatRequest;
import io.why503.performanceservice.domain.roundSeats.model.dto.response.RoundSeatResponse;
import io.why503.performanceservice.domain.roundSeats.model.dto.response.SeatReserveResponse;
import io.why503.performanceservice.domain.roundSeats.model.entity.RoundSeatEntity;
import io.why503.performanceservice.domain.roundSeats.model.enums.RoundSeatStatus;
import io.why503.performanceservice.domain.roundSeats.repository.RoundSeatRepository;
import io.why503.performanceservice.domain.roundSeats.service.RoundSeatService;
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
    private final ConcertHallRepository concertHallRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserValidator userValidator;


    //회차 좌석 생성
    @Override
    @Transactional
    public RoundSeatResponse createRoundSeat(Long userSq, RoundSeatRequest request) {
        userValidator.validateEnterprise(userSq);

        //회차 정보 조회
        RoundEntity roundEntity = roundRepository.findById(request.roundSq())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다."));

        //회차 좌석 엔티티 생성
        RoundSeatEntity entity = roundSeatMapper.requestToEntity(request, roundEntity);
        //DB에 저장
        RoundSeatEntity savedEntity = roundSeatRepository.save(entity);

        return roundSeatMapper.entityToResponse(savedEntity);
    }

    //전체 좌석 조회
    @Override
    public List<RoundSeatResponse> getRoundSeatList(Long userSq,Long roundSq) {
        userValidator.validateEnterprise(userSq);
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
        userValidator.validateEnterprise(userSq);

        //변경할 좌석을 DB에서 찾음
        RoundSeatEntity entity = roundSeatRepository.findById(roundSeatSq)
                .orElseThrow(() -> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));
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
            throw new IllegalArgumentException("요청한 좌석 중 존재하지 않는 좌석이 있습니다.");
        }

        for (RoundSeatEntity seat : seats) {
            if (seat.getStatus() != RoundSeatStatus.AVAILABLE) {
                throw new IllegalArgumentException("이미 선택되었거나 예매 불가능한 좌석입니다: " + seat.getSq());
            }
        }
        // 같은 회차의 좌석들이므로 공연장 정보를 얻어올때 첫번째 좌석의 정보를 이용
        RoundSeatEntity firstSeat = seats.get(0);

        // 좌석을 통해 공연장 sq 찾기
        Long concertHallSq = firstSeat.getRound().getShow().getConcertHallSq();

        // 공연장 이름 조회
        String fixedConcertHallName = concertHallRepository.findById(concertHallSq)
                .map(ConcertHallEntity::getName)
                .orElseThrow(() -> new IllegalArgumentException("공연장 정보가 없습니다."));

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
                throw new IllegalArgumentException("연결된 공연 좌석 정보가 없습니다.");
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

            if (savedValue == null) {
                throw new IllegalArgumentException("선점 정보가 존재하지 않습니다. 다시 예매해주세요.");
            }

            String savedUserSq = String.valueOf(savedValue);
            if (!savedUserSq.equals(String.valueOf(userSq))) {
                throw new IllegalArgumentException("본인이 선점한 좌석만 결제할 수 있습니다.");
            }
        }

        List<RoundSeatEntity> seats = roundSeatRepository.findAllById(roundSeatSqs);

        for (RoundSeatEntity seat : seats) {
            seat.confirm();
            redisTemplate.delete("seat_owner:" + seat.getSq());
        }
    }

    private List<RoundSeatResponse> convertToDtoList(List<RoundSeatEntity> entities) {
        List<RoundSeatResponse> dtoList = new ArrayList<>();
        for (RoundSeatEntity entity : entities) {
            dtoList.add(roundSeatMapper.entityToResponse(entity));
        }
        return dtoList;
    }
}