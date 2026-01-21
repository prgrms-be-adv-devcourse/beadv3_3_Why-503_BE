package io.why503.performanceservice.domain.roundSeats.service;


import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.round.repository.RoundRepository;
import io.why503.performanceservice.domain.roundSeats.client.PaymentClient;
import io.why503.performanceservice.domain.roundSeats.model.dto.*;
import io.why503.performanceservice.domain.roundSeats.model.entity.RoundSeatEntity;
import io.why503.performanceservice.domain.roundSeats.model.mapper.RoundSeatMapper;
import io.why503.performanceservice.domain.roundSeats.repository.RoundSeatRepository;
import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.repository.ShowSeatRepository;
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

    //Redis 작업을 위한 템플릿 주입
    private final RedisTemplate<String, Object> redisTemplate;

    //회차 좌석 생성
    @Transactional
    public RoundSeatResponse createRoundSeat(RoundSeatRequest request){
        // FK 연동을 위해 RoundEntity 조회
        RoundEntity roundEntity = roundRepository.findById(request.roundSq())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회차입니다."));

        // 조회한 roundEntity를 Mapper에 전달
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
    public RoundSeatResponse patchRoundSeatStatus(Long roundSeatSq, RoundSeatStatus newStatus){
        RoundSeatEntity entity = roundSeatRepository.findById(roundSeatSq)
                //존재하지 않는 데이터 조회시
                .orElseThrow(()-> new IllegalArgumentException("해당 좌석을 찾을 수 없습니다."));

        entity.updateStatus(newStatus);
        return roundSeatMapper.entityToDto(entity);

    }

    //좌석 선점
    // 유저 식별자(userSq) 파라미터 추가 (Redis 저장을 위함)
    @Transactional
    public List<SeatReserveResponse> reserveSeats(Long userSq, List<Long> roundSeatSqs) {
        List<SeatReserveResponse> responseList = new ArrayList<>();

        // 요청된 모든 회차 좌석 조회
        List<RoundSeatEntity> seats = roundSeatRepository.findAllById(roundSeatSqs);

        if (seats.size() != roundSeatSqs.size()) {
            throw new IllegalArgumentException("요청한 좌석 중 존재하지 않는 좌석이 있습니다.");
        }

        // 공연 좌석 ID만 추출
        List<Long> showSeatIds = seats.stream()
                //seat이 들어오면 getShowSeatSq를 호출
                .map(seat -> seat.getShowSeatSq())
                .toList();

        // 공연 좌석 정보 한 번에 조회
        List<ShowSeatEntity> showSeats = showSeatRepository.findAllById(showSeatIds);

        //
        // Key: 공연좌석ID, Value: 공연좌석객체
        Map<Long, ShowSeatEntity> showSeatMap = showSeats.stream()
                .collect(Collectors.toMap(
                        showSeat -> showSeat.getShowSeatSq(),
                        showSeat -> showSeat
                ));

        // 반복문 처리
        for (RoundSeatEntity roundSeat : seats) {
            // 상태 변경 (낙관적 락 작동)
            roundSeat.reserve();

            // Redis에 소유권 등록 (좌석번호:유저번호)
            // 시간 제한 로직은 결제 쪽에서 담당하므로 여기선 소유권만 기록
            String key = "seat_owner:" + roundSeat.getRoundSeatSq();
            redisTemplate.opsForValue().set(key, String.valueOf(userSq));

            // Map에서 공연 좌석 정보 가져오기
            ShowSeatEntity showSeat = showSeatMap.get(roundSeat.getShowSeatSq());

            if (showSeat == null) {
                throw new IllegalArgumentException("연결된 공연 좌석 정보가 없습니다.");
            }

            // 응답 객체 생성
            responseList.add(SeatReserveResponse.builder()
                    .roundSeatSq(roundSeat.getRoundSeatSq())
                    .roundSeatStatus(roundSeat.getRoundSeatStatus().name())
                    .price(showSeat.getPrice())
                    .grade(showSeat.getGrade().name())
                    .seatArea(showSeat.getSeat().getSeatArea())
                    .areaSeatNumber(showSeat.getSeat().getAreaSeatNo())
                    .build());
        }

        paymentClient.createBooking(userSq, new PaymentRequest(userSq, roundSeatSqs));

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