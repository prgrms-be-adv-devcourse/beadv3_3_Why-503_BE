package io.why503.performanceservice.domain.roundSeat.service;

import io.why503.commonbase.exception.CustomException;
import io.why503.performanceservice.domain.hall.model.entity.HallEntity;
import io.why503.performanceservice.domain.hall.model.enums.HallStatus;
import io.why503.performanceservice.domain.hall.repository.HallRepository;
import io.why503.performanceservice.domain.round.model.entity.RoundEntity;
import io.why503.performanceservice.domain.round.model.enums.RoundStatus;
import io.why503.performanceservice.domain.round.repository.RoundRepository;
import io.why503.performanceservice.domain.roundSeat.model.dto.response.SeatReserveResponse;
import io.why503.performanceservice.domain.roundSeat.model.entity.RoundSeatEntity;
import io.why503.performanceservice.domain.roundSeat.model.enums.RoundSeatStatus;
import io.why503.performanceservice.domain.roundSeat.repository.RoundSeatRepository;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class RoundSeatServiceTest {

    @Autowired private RoundSeatService roundSeatService;
    @Autowired private RoundSeatRepository roundSeatRepository;

    // 데이터 셋업용 Repository
    @Autowired private HallRepository hallRepository;
    @Autowired private ShowRepository showRepository;
    @Autowired private RoundRepository roundRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private ShowSeatRepository showSeatRepository;

    @Autowired private RedisTemplate<String, Object> redisTemplate;

    private RoundEntity savedRound;
    private Long savedShowSeatSq;
    private final Long userSq = 12345L;

    @BeforeEach
    void setUp() {
        // 공연장 생성
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

        // 좌석(Seat) 생성
        SeatEntity seat = SeatEntity.builder()
                .hall(hall)
                .area("A")
                .numInArea(1)
                .num(1)
                .build();
        seatRepository.save(seat);

        // 공연(Show) 생성
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

        // 공연 좌석(ShowSeat) 생성
        ShowSeatEntity showSeat = new ShowSeatEntity(
                ShowSeatGrade.VIP,
                150000L,
                show,
                seat
        );
        showSeatRepository.save(showSeat);
        this.savedShowSeatSq = showSeat.getSq();

        // 회차(Round) 생성
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
        // Redis 전체 데이터 삭제
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    // 정상 선점 테스트
    @Test
    @DisplayName("좌석 선점 성공: DB는 RESERVED가 되고, Redis에 유저 정보가 저장된다.")
    void reserveSeats_Success() {
        // Given: 판매 가능(AVAILABLE)한 좌석 생성
        Long seatId = createRoundSeat(RoundSeatStatus.AVAILABLE);

        // When: 선점 요청
        List<SeatReserveResponse> responses = roundSeatService.reserveSeats(userSq, List.of(seatId));

        // Then
        // 응답 검증
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).roundSeatStatus()).isEqualTo("RESERVED");

        // DB 상태 확인
        RoundSeatEntity savedSeat = roundSeatRepository.findById(seatId).orElseThrow();
        assertThat(savedSeat.getStatus()).isEqualTo(RoundSeatStatus.RESERVED);
        assertThat(savedSeat.getVersion()).isEqualTo(1L); // 버전 증가 확인

        // Redis 저장 확인
        String key = "seat_owner:" + seatId;
        String redisUser = (String) redisTemplate.opsForValue().get(key);
        assertThat(redisUser).isEqualTo(String.valueOf(userSq));
    }

    // 동시성/중복 방지 테스트 (롤백 검증)
    @Test
    @DisplayName("이미 선점된 좌석 요청 시 CustomException이 발생하고, Redis 키는 생성되지 않아야 한다.")
    void reserveSeats_Fail_AlreadyReserved() {
        // Given: 이미 선점(RESERVED)된 좌석 생성
        Long seatId = createRoundSeat(RoundSeatStatus.RESERVED);

        // When & Then: 예외 발생 확인
        assertThatThrownBy(() -> roundSeatService.reserveSeats(userSq, List.of(seatId)))
                .isInstanceOf(CustomException.class) // CustomException으로 검증
                .hasMessageContaining("요청하신 좌석 중 이미 선점되었거나 예매 불가능한 좌석이 포함되어 있습니다."); // 실제 서비스 메시지와 일치

        // Then: Redis 확인 (롤백되어 키가 없어야 함)
        String key = "seat_owner:" + seatId;
        Boolean hasKey = redisTemplate.hasKey(key);
        assertThat(hasKey).isFalse();
    }


    // 판매 확정 테스트 (Commit 후 삭제 검증)
    @Test
    @DisplayName("결제 확정 성공: DB는 SOLD가 되고, Redis 키는 삭제된다.")
    void confirmSeats_Success() {
        // Given: 유저가 선점 중인 좌석 세팅
        Long seatId = createRoundSeat(RoundSeatStatus.RESERVED);
        String key = "seat_owner:" + seatId;
        redisTemplate.opsForValue().set(key, String.valueOf(userSq)); // Redis에 선점 정보 강제 주입

        // When: 결제 확정 요청
        roundSeatService.confirmSeats(userSq, List.of(seatId));

        // Then: DB 상태 확인
        RoundSeatEntity savedSeat = roundSeatRepository.findById(seatId).orElseThrow();
        assertThat(savedSeat.getStatus()).isEqualTo(RoundSeatStatus.SOLD);
    }

    private Long createRoundSeat(RoundSeatStatus status) {
        RoundSeatEntity seat = RoundSeatEntity.builder()
                .round(savedRound)             //미리 만들어둔 회차 정보와 연결
                .showSeatSq(savedShowSeatSq)   //미리 만들어둔 공연 좌석(등급/가격) 정보와 연결
                .status(status)                //파라미터로 받은 상태로 세팅
                .statusDt(LocalDateTime.now()) //상태 변경 시간은 지금으로 세팅
                .version(0L)                   //낙관적 락을 위한 초기 버전은 0으로 세팅
                .build();
        return roundSeatRepository.save(seat).getSq();
    }

    @Test
    @DisplayName("동시에 같은 좌석 선점 시 1명만 성공해야 한다.")
    void reserveSeats_concurrent_2users() throws Exception {

        Long seatId = createRoundSeat(RoundSeatStatus.AVAILABLE);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        for (int i = 0; i < 2; i++) {
            final long requestUser = i + 1;

            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

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
        executor.awaitTermination(5, TimeUnit.SECONDS);

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);

        RoundSeatEntity seat = roundSeatRepository.findById(seatId).orElseThrow();
        assertThat(seat.getStatus()).isEqualTo(RoundSeatStatus.RESERVED);
        assertThat(seat.getVersion()).isEqualTo(1L);

        String key = "seat_owner:" + seatId;
        assertThat(redisTemplate.hasKey(key)).isTrue();
    }

    @Test
    @DisplayName("동시성 테스트: 10명이 동시에 같은 좌석을 선점하면 딱 1명만 성공해야 한다.")
    void reserveSeats_concurrent_10users() throws Exception {

        // Given: 빈 좌석 생성
        Long seatId = createRoundSeat(RoundSeatStatus.AVAILABLE);

        // 딱 10명으로 설정 (DB 커넥션 풀 기본값이 10이므로 병목 없이 즉시 실행됨)
        int threadCount = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // When: 10명의 유저가 동시에 요청
        for (int i = 0; i < threadCount; i++) {
            final long requestUser = i + 1; // 유저 PK (1 ~ 10)

            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();     // 메인 스레드 대기

                    roundSeatService.reserveSeats(requestUser, List.of(seatId));
                    successCount.incrementAndGet(); // 성공하면 카운트업

                } catch (Exception e) {
                    // CustomException이 발생하면 실패 카운트업
                    failCount.incrementAndGet();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();

        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            executor.shutdownNow(); // 5초 기다렸는데도 안 끝나면 강제 종료
        }

        // Then: 철저한 검증
        // 딱 1명만 성공하고, 9명은 예외가 터져서 실패해야 한다.
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(threadCount - 1);

        // DB 상태는 1명이 가져갔으니 'RESERVED'가 되어야 한다.
        RoundSeatEntity seat = roundSeatRepository.findById(seatId).orElseThrow();
        assertThat(seat.getStatus()).isEqualTo(RoundSeatStatus.RESERVED);

        // 누군가 1번 업데이트 했으니 낙관적 락 버전은 1이어야 한다.
        assertThat(seat.getVersion()).isEqualTo(1L);

        // Redis에 1명의 정보가 잘 남아있어야 한다.
        String key = "seat_owner:" + seatId;
        assertThat(redisTemplate.hasKey(key)).isTrue();
    }

}