package io.why503.performanceservice.domain.roundSeat.service.Impl;

import io.why503.commonbase.exception.CustomException;
import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.round.model.enums.RoundStatus;
import io.why503.performanceservice.domain.round.repository.RoundRepository;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
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
        userValidator.validateEnterprise(userSq, RoundSeatExceptionFactory.roundSeatForbidden("기업 또는 관리자만 회차 좌석 등록이 가능합니다."));

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
    public List<RoundSeatResponse> getRoundSeatList(Long userSq, Long roundSq) {
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
        userValidator.validateEnterprise(userSq, RoundSeatExceptionFactory.roundSeatForbidden("기업 또는 관리자만 회차 좌석 상태 변경이 가능합니다."));

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

    //좌석 선점 Redis사용, 낙관적락
    @Override
    @Transactional
    public List<SeatReserveResponse> reserveSeats(Long userSq, List<Long> roundSeatSqs) {
        List<SeatReserveResponse> responseList = new ArrayList<>();

        if (roundSeatSqs == null || roundSeatSqs.isEmpty()) {
            return responseList;
        }

        // 롤백 시 삭제할 Redis Key 목록 미리 생성
        List<String> redisKeys = roundSeatSqs.stream()
                .map(sq -> "seat_owner:" + sq)
                .toList();


        int updatedCount = roundSeatRepository.updateStatusBulk(
                roundSeatSqs,
                RoundSeatStatus.RESERVED,   // RESERVED로 상태 변환
                RoundSeatStatus.AVAILABLE,  // 현재 상태가 AVAILABLE(빈 좌석)일 때만
                LocalDateTime.now()
        );

        //동시성 검증
        if (updatedCount != roundSeatSqs.size()) {
            // 여기서 에러가 터지면 트랜잭션 롤백
            throw RoundSeatExceptionFactory.roundSeatConflict("요청하신 좌석 중 이미 선점되었거나 예매 불가능한 좌석이 포함되어 있습니다.");
        }

        // 트랜잭션이 성공적으로 완전히 끝났을 때(Commit) 실행할 작업 예약
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                // DB 업데이트가 성공(Commit)해 저장된 직후에만 실행
                for (String key : redisKeys) {
                    redisTemplate.opsForValue().set(key, String.valueOf(userSq), Duration.ofMinutes(10));
                }
                log.info("DB 커밋 완료. 유저의 정보를 Redis에 저장했습니다.");
            }
        });

        // 클라이언트에게 돌려줄 응답 데이터 생성, 방금 업데이트한 좌석 정보 재조회
        List<RoundSeatEntity> seats = roundSeatRepository.findAllById(roundSeatSqs);

        if (!seats.isEmpty()) {
            RoundSeatEntity firstSeat = seats.get(0);
            String fixedConcertHallName = firstSeat.getRound().getShow().getHall().getName();

            List<Long> showSeatSqs = seats.stream().map(RoundSeatEntity::getShowSeatSq).toList();
            List<ShowSeatEntity> showSeatEntities = showSeatRepository.findAllById(showSeatSqs);

            Map<Long, ShowSeatEntity> showSeatMap = new HashMap<>();
            showSeatEntities.forEach(ss -> showSeatMap.put(ss.getSq(), ss));

            for (RoundSeatEntity seat : seats) {
                ShowSeatEntity showSeat = showSeatMap.get(seat.getShowSeatSq());
                if (showSeat == null) {
                    throw RoundSeatExceptionFactory.roundSeatConflict("좌석 등급 정보가 존재하지 않습니다.");
                }
                responseList.add(roundSeatMapper.entityToReserveResponse(seat, showSeat, fixedConcertHallName));
            }
        }

        return responseList;

    }

    //선점 해제 ,redis락 삭제
    @Override
    @Transactional
    public void releaseSeats(Long userSq, List<Long> roundSeatSqs) {
        if (roundSeatSqs == null || roundSeatSqs.isEmpty()) return;

        checkOwner(userSq, roundSeatSqs); // 본인 확인

        List<String> redisKeys = roundSeatSqs.stream().map(sq -> "seat_owner:" + sq).toList();

        // [안전장치] 커밋 성공 시 Redis 삭제
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("[Release] 해제 완료. Redis 정보를 삭제합니다.");
                redisTemplate.delete(redisKeys);
            }
        });

        // [DB] Bulk Update (RESERVED -> AVAILABLE)
        roundSeatRepository.updateStatusBulk(
                roundSeatSqs,
                RoundSeatStatus.AVAILABLE,
                RoundSeatStatus.RESERVED,
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional
    public void confirmSeats(Long userSq, List<Long> roundSeatSqs) {
        if (roundSeatSqs == null || roundSeatSqs.isEmpty()) return;

        checkOwner(userSq, roundSeatSqs); // 본인 확인

        List<String> redisKeys = roundSeatSqs.stream().map(sq -> "seat_owner:" + sq).toList();

        // [안전장치] 커밋 성공 시 Redis 삭제
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("[Confirm] 결제 확정. Redis 정보를 삭제합니다.");
                redisTemplate.delete(redisKeys);
            }
        });

        // [DB] Bulk Update (RESERVED -> SOLD)
        int updatedCount = roundSeatRepository.updateStatusBulk(
                roundSeatSqs,
                RoundSeatStatus.SOLD,
                RoundSeatStatus.RESERVED,
                LocalDateTime.now()
        );

        if (updatedCount != roundSeatSqs.size()) {
            throw RoundSeatExceptionFactory.roundSeatConflict("결제 가능한 상태(선점됨)가 아닙니다.");
        }
    }

    // Redis 본인 확인
    private void checkOwner(Long userSq, List<Long> roundSeatSqs) {
        for (Long seatId : roundSeatSqs) {
            String key = "seat_owner:" + seatId;
            Object savedValue = redisTemplate.opsForValue().get(key);

            if (savedValue == null) {
                throw RoundSeatExceptionFactory.roundSeatBadRequest("선점 가능 시간이 만료되었거나 유효하지 않은 예약입니다.");
            }
            String savedUserSq = String.valueOf(savedValue).replace("\"", "");
            if (!savedUserSq.equals(String.valueOf(userSq))) {
                throw RoundSeatExceptionFactory.roundSeatForbidden("본인이 선점한 좌석만 처리할 수 있습니다.");
            }
        }
    }

    // 요청된 좌석 목록에 대해 공연장 정보와 좌석 등급별 가격 데이터를 결합하여 상세 정보 추출
    @Override
    public List<SeatReserveResponse> getRoundSeatDetails(List<Long> roundSeatSqs) {
        // 전달된 식별자 목록이 비어있는 경우 즉시 빈 결과 반환
        if (roundSeatSqs == null || roundSeatSqs.isEmpty()) {
            return Collections.emptyList();
        }

        // 데이터베이스에서 해당 식별자에 대응하는 회차별 좌석 실데이터 확인
        List<RoundSeatEntity> roundSeats = roundSeatRepository.findAllById(roundSeatSqs);
        if (roundSeats.isEmpty()) {
            return Collections.emptyList();
        }

        // 각 좌석의 등급 정보와 가격을 일괄 조회하기 위해 원천 좌석 식별자 수집
        List<Long> showSeatSqs = new ArrayList<>();
        for (RoundSeatEntity rs : roundSeats) {
            showSeatSqs.add(rs.getShowSeatSq());
        }

        // 수집된 식별자를 기반으로 좌석 등급 데이터를 일괄 조회하여 매핑 구조 생성
        List<ShowSeatEntity> showSeats = showSeatRepository.findAllById(showSeatSqs);
        Map<Long, ShowSeatEntity> showSeatMap = new HashMap<>();
        for (ShowSeatEntity ss : showSeats) {
            showSeatMap.put(ss.getSq(), ss);
        }

        // 동일 회차 내 좌석들이므로 첫 번째 좌석을 기준으로 연관된 공연장 명칭 추출
        RoundSeatEntity firstSeat = roundSeats.get(0);
        String hallName = firstSeat.getRound().getShow().getHall().getName();

        List<SeatReserveResponse> responseList = new ArrayList<>();

        // 회차별 좌석과 등급별 가격 정보를 병합하여 최종 응답 객체 구성
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


    @Override
    @Transactional
    public void cleanupExpiredReservations() {
        // 기준 시간: 현재 시간으로부터 10분 전
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(10);
        LocalDateTime now = LocalDateTime.now();

        try {
            // DB 쿼리로 10분 지난 RESERVED 좌석들을 AVAILABLE 로 변경
            int releasedCount = roundSeatRepository.releaseExpiredSeats(
                    threshold,
                    now,
                    RoundSeatStatus.AVAILABLE,
                    RoundSeatStatus.RESERVED
            );
            if (releasedCount > 0) {
                log.info("[Scheduler] 만료된 좌석 {}건 자동 해제(DB 기준) 완료", releasedCount);
            }
        } catch (Exception e) {
            log.error("[Scheduler] 만료 좌석 해제 중 오류 발생", e);
        }
    }
}