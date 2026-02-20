package io.why503.performanceservice.domain.roundSeat.service;

import io.why503.commonbase.exception.CustomException;
import io.why503.performanceservice.domain.hall.model.entity.HallEntity;
import io.why503.performanceservice.domain.hall.model.enums.HallStatus;
import io.why503.performanceservice.domain.hall.repository.HallRepository;
import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.round.model.enums.RoundStatus;
import io.why503.performanceservice.domain.round.repository.RoundRepository;
import io.why503.performanceservice.domain.roundSeat.model.entity.RoundSeatEntity;
import io.why503.performanceservice.domain.roundSeat.model.enums.RoundSeatStatus;
import io.why503.performanceservice.domain.roundSeat.repository.RoundSeatRepository;
import io.why503.performanceservice.domain.roundSeat.service.Impl.RoundSeatServiceImpl;
import io.why503.performanceservice.domain.seat.model.entity.SeatEntity;
import io.why503.performanceservice.domain.seat.repository.SeatRepository;
import io.why503.performanceservice.domain.show.model.entity.ShowEntity;
import io.why503.performanceservice.domain.show.model.enums.ShowCategory;
import io.why503.performanceservice.domain.show.model.enums.ShowGenre;
import io.why503.performanceservice.domain.show.model.enums.ShowStatus;
import io.why503.performanceservice.domain.show.repository.ShowRepository;
import io.why503.performanceservice.domain.showseat.model.entity.ShowSeatEntity;
import io.why503.performanceservice.domain.showseat.model.enums.ShowSeatGrade;
import io.why503.performanceservice.domain.showseat.repository.ShowSeatRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DataJpaTest
@Import({RoundSeatServiceImpl.class})
@ActiveProfiles("test")
// 동시성 테스트를 위해 기본 트랜잭션을 끄고 멀티스레드 충돌을 유도
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class RoundSeatServiceTest {

    @Autowired private RoundSeatService roundSeatService;
    @Autowired private RoundSeatRepository roundSeatRepository;

    @Autowired private HallRepository hallRepository;
    @Autowired private ShowRepository showRepository;
    @Autowired private RoundRepository roundRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private ShowSeatRepository showSeatRepository;

    @MockitoBean private RedisTemplate<String, Object> redisTemplate;
    @MockitoBean private io.why503.performanceservice.util.mapper.RoundSeatMapper roundSeatMapper;
    @MockitoBean private io.why503.performanceservice.global.validator.UserValidator userValidator;

    @MockitoBean private io.why503.performanceservice.domain.roundSeat.scheduler.RoundSeatScheduler roundSeatScheduler;

    private ValueOperations<String, Object> valueOperations;

    private RoundEntity savedRound;
    private Long savedShowSeatSq;
    private final Long userSq = 12345L;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        //가짜 Redis가 NullPointException을 뱉지 않도록 기본 행동 지시
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        HallEntity hall = HallEntity.builder()
                .name("서울 예술의전당")
                .post("06757")
                .basicAddr("서울 서초구")
                .detailAddr("남부순환로 2406")
                .status(String.valueOf(HallStatus.ACTIVE))
                .seatScale(1000)
                .structure("Opera House")
                .latitude(BigDecimal.valueOf(37.479))
                .longitude(BigDecimal.valueOf(127.011))
                .build();
        hallRepository.save(hall);

        SeatEntity seat = SeatEntity.builder()
                .hall(hall)
                .area("A")
                .numInArea(1)
                .num(1)
                .build();
        seatRepository.save(seat);

        ShowEntity show = ShowEntity.builder()
                .name("지킬 앤 하이드")
                .startDt(LocalDateTime.now().plusDays(10))
                .endDt(LocalDateTime.now().plusDays(20))
                .openDt(LocalDateTime.now().minusDays(1))
                .runningTime("160분")
                .viewingAge("15세 이상")
                .category(ShowCategory.MUSICAL)
                .genre(ShowGenre.CREATIVE)
                .status(ShowStatus.SCHEDULED)
                .hall(hall)
                .companySq(1L)
                .build();
        showRepository.save(show);

        ShowSeatEntity showSeat = new ShowSeatEntity(
                ShowSeatGrade.VIP,
                150000L,
                show,
                seat
        );
        showSeatRepository.save(showSeat);
        this.savedShowSeatSq = showSeat.getSq();

        savedRound = RoundEntity.builder()
                .show(show)
                .startDt(LocalDateTime.now().plusDays(10))
                .num(1)
                .casting("홍길동, 임꺽정")
                .status(RoundStatus.AVAILABLE)
                .build();
        roundRepository.save(savedRound);
    }

    @AfterEach
    void tearDown() {
        // 수동 트랜잭션 관리를 하므로, 다음 테스트에 영향이 없도록 DB를 비움
        roundSeatRepository.deleteAll();
        roundRepository.deleteAll();
        showSeatRepository.deleteAll();
        showRepository.deleteAll();
        seatRepository.deleteAll();
        hallRepository.deleteAll();
    }

    // 정상 선점 테스트
    @Test
    @DisplayName("좌석 선점 성공: DB는 RESERVED가 되고, 가짜 Redis에 저장이 지시된다.")
    void reserveSeats_Success() {
        Long seatId = createRoundSeat(RoundSeatStatus.AVAILABLE);

        roundSeatService.reserveSeats(userSq, List.of(seatId));

        // DB 상태 및 버전(낙관적 락) 확인
        RoundSeatEntity savedSeat = roundSeatRepository.findById(seatId).orElseThrow();
        assertThat(savedSeat.getStatus()).isEqualTo(RoundSeatStatus.RESERVED);
        assertThat(savedSeat.getVersion()).isEqualTo(1L);

        // 가짜 Redis에 set() 호출이 1번 잘 들어갔는지 행위를 검증
        String expectedKey = "seat_owner:" + seatId;
        verify(valueOperations, times(1)).set(eq(expectedKey), eq(String.valueOf(userSq)), any(Duration.class));
    }

    // 예외 테스트
    @Test
    @DisplayName("이미 선점된 좌석 요청 시 CustomException이 발생하고, Redis에는 아무 명령도 가지 않는다.")
    void reserveSeats_Fail_AlreadyReserved() {
        Long seatId = createRoundSeat(RoundSeatStatus.RESERVED);

        assertThatThrownBy(() -> roundSeatService.reserveSeats(userSq, List.of(seatId)))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("요청하신 좌석 중 이미 선점되었거나 예매 불가능한 좌석이 포함되어 있습니다.");

        // 예외가 터져서 롤백되었으니, Redis의 set 메서드는 단 한 번도 호출되지 않아야 함
        verify(valueOperations, never()).set(anyString(), anyString(), any(Duration.class));
    }

    // 결제 확정 테스트
    @Test
    @DisplayName("결제 확정 성공: DB는 SOLD가 되고, Redis 키 삭제 명령이 호출된다.")
    void confirmSeats_Success() {
        Long seatId = createRoundSeat(RoundSeatStatus.RESERVED);
        String key = "seat_owner:" + seatId;

        // 본인 검증 로직을 무사히 통과하도록 가짜 Redis가 유저 번호를 반환하게 세팅
        when(valueOperations.get(key)).thenReturn(String.valueOf(userSq));

        roundSeatService.confirmSeats(userSq, List.of(seatId));

        // DB 확정 확인
        RoundSeatEntity savedSeat = roundSeatRepository.findById(seatId).orElseThrow();
        assertThat(savedSeat.getStatus()).isEqualTo(RoundSeatStatus.SOLD);

        // 가짜 Redis에 delete() 호출이 정상적으로 갔는지 확인
        verify(redisTemplate, times(1)).delete(List.of(key));
    }

    private Long createRoundSeat(RoundSeatStatus status) {
        RoundSeatEntity seat = RoundSeatEntity.builder()
                .round(savedRound)
                .showSeatSq(savedShowSeatSq)
                .status(status)
                .statusDt(LocalDateTime.now())
                .version(0L)
                .build();
        return roundSeatRepository.save(seat).getSq();
    }

    //메인 동시성 검증 (낙관적 락 테스트)
    @Test
    @DisplayName("동시성 테스트: 10명이 동시에 1자리를 누르면 DB 낙관적 락에 의해 딱 1명만 성공해야 한다.")
    void reserveSeats_concurrent_10users() throws Exception {

        Long seatId = createRoundSeat(RoundSeatStatus.AVAILABLE);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            final long requestUser = i + 1;

            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await(); // 10명 일제히 대기 후 동시 출발

                    roundSeatService.reserveSeats(requestUser, List.of(seatId));
                    successCount.incrementAndGet();

                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();

        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }

        // 낙관적 락 동시성 방어 결과 검증
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(threadCount - 1);

        // 최종 DB 상태 검증
        RoundSeatEntity seat = roundSeatRepository.findById(seatId).orElseThrow();
        assertThat(seat.getStatus()).isEqualTo(RoundSeatStatus.RESERVED);
        assertThat(seat.getVersion()).isEqualTo(1L);

        // Redis 저장 명령(set)을 날린 스레드도 1번뿐임을 검증
        verify(valueOperations, times(1)).set(
                eq("seat_owner:" + seatId),
                anyString(),
                any(Duration.class)
        );
    }
}