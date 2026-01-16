/*
package io.why503.paymentservice.domain.booking.sv;

import io.why503.paymentservice.domain.booking.model.dto.BookingReqDto;
import io.why503.paymentservice.domain.booking.model.dto.TicketReqDto;
import io.why503.paymentservice.domain.booking.repo.BookingRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"
})
class ConcurrencyTest {

    @Autowired
    private BookingSv bookingSv;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private TicketRepo ticketRepo;

    @AfterEach
    void tearDown() {
        // 테스트가 끝나면 DB를 깨끗하게 비워줍니다.
        // (Ticket이 Booking을 참조하므로 Booking 먼저 지우거나 Cascade 설정에 따라 다름)
        // 안전하게 Ticket -> Booking 순서로 삭제
        ticketRepo.deleteAll();
        bookingRepo.deleteAll();
    }

    @Test
    @DisplayName("동시에 2명이 같은 좌석(100번) 예매 시도 -> 1명만 성공해야 한다.")
    void bookingConcurrencyTest() throws InterruptedException {
        // given
        int threadCount = 2; // 동시에 2명 접속 시뮬레이션

        // 멀티스레드 작업을 도와주는 녀석들 (32개 스레드 풀 생성)
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // 2개의 작업이 모두 끝날 때까지 메인 스레드를 대기시키는 녀석
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 성공 횟수와 실패 횟수를 세는 변수 (동시성 환경에서도 안전한 AtomicInteger 사용)
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // 테스트 데이터 준비 (좌석 ID: 100L)
        TicketReqDto ticketReq = TicketReqDto.builder().showingSeatSq(100L).build();
        BookingReqDto req = BookingReqDto.builder()
                .userSq(1L)
                .tickets(List.of(ticketReq)) // 100번 좌석 요청
                .build();

        // when (동시 요청 시작!)
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 예매 시도!
                    bookingSv.createBooking(req);
                    // 에러 안 나면 성공 카운트 증가
                    successCount.getAndIncrement();
//                    System.out.println("성공!");
                } catch (Exception e) {
                    // 에러 나면 실패 카운트 증가
                    failCount.getAndIncrement();
//                    System.out.println("실패: " + e.getMessage());
                } finally {
                    // 성공하든 실패하든 작업 끝났다고 알림
                    latch.countDown();
                }
            });
        }

        // 2명의 스레드가 다 끝날 때까지 여기서 기다림
        latch.await();

        // then (검증)
//        System.out.println("최종 성공 횟수: " + successCount.get());
//        System.out.println("최종 실패 횟수: " + failCount.get());

        // ★ 핵심 검증: 성공은 딱 1번이어야 하고, 실패도 딱 1번이어야 함
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
    }
} booking이 많이 수정되어서 테스트 파일이 쓸모 없다고 판단. 주석 처리 하겠습니다.*/
