package io.why503.paymentservice.domain.booking.service;

import io.why503.paymentservice.domain.booking.mapper.BookingMapper;
import io.why503.paymentservice.domain.booking.model.dto.BookingRequest;
import io.why503.paymentservice.domain.booking.model.dto.BookingResponse;
import io.why503.paymentservice.domain.booking.model.entity.Booking;
import io.why503.paymentservice.domain.booking.model.entity.Ticket;
import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
import io.why503.paymentservice.domain.booking.model.vo.TicketStatus;
import io.why503.paymentservice.domain.booking.repository.BookingRepository;
import io.why503.paymentservice.domain.booking.repository.TicketRepository;
import io.why503.paymentservice.global.client.AccountClient;
import io.why503.paymentservice.global.client.PerformanceClient;
import io.why503.paymentservice.global.client.dto.AccountResponse;
import io.why503.paymentservice.global.client.dto.RoundSeatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 예매 서비스 (Core Business Logic)
 * - 담당: 예매 생성(스냅샷), 조회, 취소(전체/부분), 확정(결제 승인 및 상태 동기화)
 * - MSA 통신: AccountService(유저 확인), PerformanceService(좌석 선점/해제/판매확정)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final BookingMapper bookingMapper;
    private final AccountClient accountClient;
    private final PerformanceClient performanceClient;

    /**
     * [1] 예매 생성 (Pre-Booking)
     * - 공연 서비스에서 '진짜 정보'를 가져와 티켓을 생성하고(스냅샷),
     * - 포인트를 검증하여 결제 전 임시 저장(PENDING) 상태를 만듭니다.
     */
    @Transactional
    public BookingResponse createBooking(BookingRequest bookingRequest, Long userSq) {
        log.info(">>> [Step 1] 예매 생성 시작 | UserSq={}", userSq);

        // 1. 회원 정보 조회 (유효한 회원인지 확인)
        AccountResponse accountResponse = accountClient.getAccount(userSq);
        if (accountResponse == null) {
            throw new IllegalArgumentException("존재하지 않는 회원입니다.");
        }

        // 2. 좌석 선점 요청을 위해 ID 추출
        List<Long> roundSeatIds = bookingRequest.getTickets().stream()
                .map(ticketRequest -> ticketRequest.getRoundSeatSq()) // 람다식 사용
                .collect(Collectors.toList());

        log.info(">>> [Step 2] ShowService에 좌석 선점 요청 | IDs={}", roundSeatIds);

        // 3. 공연 서비스 호출: 좌석 선점 + 최신 정보(가격, 이름 등) 수신
        List<RoundSeatResponse> reservedSeats = performanceClient.reserveRoundSeats(roundSeatIds);

        if (reservedSeats.size() != roundSeatIds.size()) {
            throw new IllegalStateException("좌석 선점 실패: 요청한 좌석 중 일부를 확보하지 못했습니다.");
        }

        // 4. 엔티티 생성 (DB 저장 준비)
        Booking booking = Booking.builder()
                .userSq(userSq)
                .bookingStatus(BookingStatus.PENDING) // 초기 상태: 결제 대기
                .build();

        int totalTicketPrice = 0;

        // 5. 티켓 스냅샷 생성 (공연장 정보가 변해도 예매 내역은 변하지 않도록 박제)
        for (RoundSeatResponse seatInfo : reservedSeats) {
            Ticket ticket = Ticket.builder()
                    .roundSeatSq(seatInfo.getRoundSeatSq()) // ID 연결
                    // [공연 정보 스냅샷]
                    .showName(seatInfo.getShowName())
                    .concertHallName(seatInfo.getConcertHallName())
                    .roundDate(seatInfo.getRoundDate())
                    // [좌석 정보 스냅샷]
                    .grade(seatInfo.getGrade())
                    .seatArea(seatInfo.getSeatArea())
                    .areaSeatNumber(seatInfo.getAreaSeatNumber())
                    // [가격 정보 스냅샷]
                    .originalPrice(seatInfo.getPrice())
                    .finalPrice(seatInfo.getPrice())
                    .ticketStatus(TicketStatus.RESERVED)
                    .build();

            booking.addTicket(ticket); // 연관관계 설정
            totalTicketPrice += seatInfo.getPrice();
        }

        // 6. 금액 설정
        booking.setBookingAmount(totalTicketPrice);
        booking.setTotalAmount(totalTicketPrice);

        // 7. 포인트 검증 및 적용
        // [수정 요청 반영] 삼항 연산자 제거 -> if문 사용
        int requestedPoint = 0;
        if (bookingRequest.getUsedPoint() != null) {
            requestedPoint = bookingRequest.getUsedPoint();
        }

        if (accountResponse.getPoint() < requestedPoint) {
            throw new IllegalArgumentException(String.format("보유 포인트 부족 (보유: %d / 요청: %d)", accountResponse.getPoint(), requestedPoint));
        }

        booking.applyPoints(requestedPoint); // 최종 결제액(pgAmount) 계산됨

        // 8. 저장
        Booking savedBooking = bookingRepository.save(booking);
        log.info(">>> [Step 4] 예매 데이터 저장 완료 | BookingSq={}, PG결제액={}", savedBooking.getBookingSq(), savedBooking.getPgAmount());

        return bookingMapper.EntityToResponse(savedBooking);
    }

    /**
     * [2] 예매 확정
     * - 인수를 3개(bookingSq, paymentKey, paymentMethod)로 변경합니다.
     */
    @Transactional
    public void confirmBooking(Long bookingSq, String paymentKey, String paymentMethod) { // 인수 3개로 수정
        Booking booking = findBookingThrow(bookingSq);
        log.info(">>> [Biz] 예매 확정 처리 시작 | BookingSq={}, Method={}", bookingSq, paymentMethod);

        // 1. 내부 상태 변경 (전달받은 결제 수단 사용)
        booking.confirm(paymentKey, paymentMethod);

        // 2. 공연 서비스에 "판매 완료(SOLD)" 알림 전송
        List<Long> seatIds = booking.getTickets().stream()
                .map(ticket -> ticket.getRoundSeatSq()) // 람다식 사용 규칙 준수
                .collect(Collectors.toList());

        try {
            performanceClient.confirmRoundSeats(seatIds);
        } catch (Exception e) {
            log.error(">>> [ShowService] 좌석 확정 실패 | Reason={}", e.getMessage());
            throw new IllegalStateException("좌석 확정 처리에 실패했습니다.");
        }
    }

    /**
     * [3] 예매 전체 취소
     * - 결제 전(PENDING): 데이터 완전 삭제 (Hard Delete)
     * - 결제 후(CONFIRMED): 취소 상태로 변경 (Soft Delete) 및 환불 처리
     */
    @Transactional
    public void cancelBooking(Long bookingSq) {
        Booking booking = findBookingThrow(bookingSq);
        BookingStatus status = booking.getBookingStatus();
        log.info(">>> [Biz] 예매 전체 취소 요청 | BookingSq={}, Status={}", bookingSq, status);

        // 1. 외부 서비스 좌석 점유 해제
        releaseSeatsInShowService(booking.getTickets());

        // 2. 상태별 처리
        if (status == BookingStatus.PENDING) {
            bookingRepository.delete(booking); // Hard Delete
            log.info(">>> [Biz] 미결제 예매 삭제 완료");
        } else if (status == BookingStatus.CONFIRMED || status == BookingStatus.PARTIAL_CANCEL) {
            booking.cancel("사용자 요청에 의한 전체 취소"); // Soft Delete
            log.info(">>> [Biz] 결제 예매 취소 처리 완료");
        } else if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("이미 취소된 예매입니다.");
        }
    }

    /**
     * [4] 티켓 개별(부분) 취소
     * - 티켓 하나만 취소하며, 남은 티켓이 없으면 전체 취소로 자동 전환됩니다.
     */
    @Transactional
    public void cancelTicket(Long bookingSq, Long ticketSq) {
        Booking booking = findBookingThrow(bookingSq);
        BookingStatus status = booking.getBookingStatus();
        log.info(">>> [Biz] 티켓 부분 취소 요청 | BookingSq={}, TicketSq={}", bookingSq, ticketSq);

        // 1. 대상 티켓 찾기
        Ticket targetTicket = booking.getTickets().stream()
                .filter(t -> t.getTicketSq().equals(ticketSq))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 티켓입니다."));

        // 2. 외부 서비스 좌석 해제 (단건)
        List<Long> seatIdList = new ArrayList<>();
        seatIdList.add(targetTicket.getRoundSeatSq());

        try {
            performanceClient.cancelRoundSeats(seatIdList);
        } catch (Exception e) {
            log.error(">>> [ShowService] 부분 좌석 해제 실패 | ID={}", targetTicket.getRoundSeatSq());
            // 실패해도 내부 상태는 취소 처리 진행 (필요시 주석 해제하여 예외 던짐)
            // throw new IllegalStateException("외부 서비스 연동 실패");
        }

        // 3. 상태별 처리
        if (status == BookingStatus.PENDING) {
            booking.deleteTicket(ticketSq); // 선점 취소
            if (booking.getTickets().isEmpty()) {
                bookingRepository.delete(booking); // 티켓 없으면 예매 자체 삭제
            }
        } else if (status == BookingStatus.CONFIRMED || status == BookingStatus.PARTIAL_CANCEL) {
            booking.cancelTicket(ticketSq, "사용자 요청에 의한 개별 취소"); // 금액 재계산 포함됨
        } else {
            throw new IllegalStateException("취소 불가능한 상태입니다.");
        }
    }

    /**
     * [5] 스케줄러용 자동 취소
     * - 결제 대기(PENDING) 상태로 10분이 지난 건들을 삭제합니다.
     */
    @Transactional
    public int cancelExpiredBookings() {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(10);
        List<Booking> expiredBookings = bookingRepository.findExpired(BookingStatus.PENDING, expirationTime);

        if (expiredBookings.isEmpty()) return 0;

        int delCount = 0;
        for (Booking booking : expiredBookings) {
            try {
                releaseSeatsInShowService(booking.getTickets());
                bookingRepository.delete(booking);
                delCount++;
            } catch (Exception e) {
                log.error(">>> [Err] 자동 취소 실패 | BookingSq={}", booking.getBookingSq());
            }
        }
        return delCount;
    }

    /**
     * [6] 예매 단건 조회
     */
    public BookingResponse getBooking(Long bookingSq) {
        return bookingMapper.EntityToResponse(findBookingThrow(bookingSq));
    }

    /**
     * [7] 사용자별 예매 목록 조회
     */
    public List<BookingResponse> getBookingsByUser(Long userSq) {
        return bookingRepository.findByUserSq(userSq).stream()
                .map(booking -> bookingMapper.EntityToResponse(booking)) // 람다식
                .collect(Collectors.toList());
    }

    /**
     * [8] QR 입장 처리
     */
    @Transactional
    public void enterTicket(String ticketUuid) {
        Ticket ticket = ticketRepository.findByTicketUuid(ticketUuid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 티켓입니다."));
        ticket.use();
        log.info(">>> [입장 완료] TicketUuid={}", ticket.getTicketUuid());
    }

    // --- Private Helper Methods ---

    private Booking findBookingThrow(Long bookingSq) {
        return bookingRepository.findByBookingSq(bookingSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다. (ID=" + bookingSq + ")"));
    }

    private void releaseSeatsInShowService(List<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) return;

        List<Long> seatIds = tickets.stream()
                .map(ticket -> ticket.getRoundSeatSq()) // 람다식
                .collect(Collectors.toList());

        try {
            performanceClient.cancelRoundSeats(seatIds);
        } catch (Exception e) {
            log.error(">>> [ShowService] 좌석 해제 요청 실패 (보상 트랜잭션 필요) | IDs={}", seatIds);
        }
    }
}


// 테스트 코드
//package io.why503.paymentservice.domain.booking.service;
//
//import io.why503.paymentservice.domain.booking.mapper.BookingMapper;
//import io.why503.paymentservice.domain.booking.model.dto.BookingRequest;
//import io.why503.paymentservice.domain.booking.model.dto.BookingResponse;
//import io.why503.paymentservice.domain.booking.model.entity.Booking;
//import io.why503.paymentservice.domain.booking.model.entity.Ticket;
//import io.why503.paymentservice.domain.booking.model.vo.BookingStatus;
//import io.why503.paymentservice.domain.booking.model.vo.TicketStatus;
//import io.why503.paymentservice.domain.booking.repository.BookingRepository;
//import io.why503.paymentservice.domain.booking.repository.TicketRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class BookingService {
//
//    private final BookingRepository bookingRepository;
//    private final TicketRepository ticketRepository;
//    private final BookingMapper bookingMapper;
//
//    // Client들은 없으므로 사용하지 않음
//    // private final AccountClient accountClient;
//    // private final PerformanceClient performanceClient;
//
//    /**
//     * [1] 예매 생성 (테스트용 - 외부 통신 제거)
//     */
//    @Transactional
//    public BookingResponse createBooking(BookingRequest bookingRequest, Long userSq) {
//        log.info(">>> [TEST MODE] 예매 생성 시작 | UserSq={}", userSq);
//
//        // 1. 회원 정보 조회 -> (가짜 통과)
//        log.info(">>> [TEST MODE] 회원 검증 패스");
//
//        // 2. 공연 서비스 호출 -> (가짜 데이터 생성)
//        log.info(">>> [TEST MODE] 좌석 선점 패스 (가짜 데이터 생성)");
//
//        // 요청한 티켓 수만큼 가짜 좌석 정보 생성
//        int requestCount = bookingRequest.getTickets().size();
//        int ticketPrice = 10000; // 테스트 가격 고정
//
//        // 3. 엔티티 생성
//        Booking booking = Booking.builder()
//                .userSq(userSq)
//                .bookingStatus(BookingStatus.PENDING)
//                .build();
//
//        int totalTicketPrice = 0;
//
//        // 4. 가짜 티켓 생성
//        for (int i = 0; i < requestCount; i++) {
//            Long seatId = bookingRequest.getTickets().get(i).getRoundSeatSq();
//
//            Ticket ticket = Ticket.builder()
//                    .roundSeatSq(seatId)
//                    .showName("테스트 뮤지컬")
//                    .concertHallName("서울 예술의전당")
//                    .roundDate(LocalDateTime.now().plusDays(7))
//                    .grade("VIP")
//                    .seatArea("A구역")
//                    .areaSeatNumber(10 + i)
//                    .originalPrice(ticketPrice)
//                    .finalPrice(ticketPrice)
//                    .ticketStatus(TicketStatus.RESERVED)
//                    .build();
//
//            booking.addTicket(ticket);
//            totalTicketPrice += ticketPrice;
//        }
//
//        // 5. 금액 설정
//        booking.setBookingAmount(totalTicketPrice);
//        booking.setTotalAmount(totalTicketPrice);
//
//        // 포인트 사용 (있으면 차감)
//        int requestedPoint = bookingRequest.getUsedPoint() != null ? bookingRequest.getUsedPoint() : 0;
//        booking.applyPoints(requestedPoint);
//
//        // 6. 저장
//        Booking savedBooking = bookingRepository.save(booking);
//        log.info(">>> [TEST MODE] 예매 저장 완료 | BookingSq={}, PG결제액={}", savedBooking.getBookingSq(), savedBooking.getPgAmount());
//
//        return bookingMapper.EntityToResponse(savedBooking);
//    }
//
//    /**
//     * [2] 예매 확정 (테스트용 - 외부 통신 제거)
//     */
//    @Transactional
//    public void confirmBooking(Long bookingSq, String paymentKey, String paymentMethod) {
//        Booking booking = findBookingThrow(bookingSq);
//        log.info(">>> [TEST MODE] 예매 확정 처리 | BookingSq={}, Method={}", bookingSq, paymentMethod);
//
//        // 1. 내부 상태 변경
//        booking.confirm(paymentKey, paymentMethod);
//
//        // 2. 공연 서비스 통보 -> (생략)
//        log.info(">>> [TEST MODE] 공연 서비스 통보 생략 (성공 처리)");
//    }
//
//    /**
//     * [3] 예매 취소 (테스트용)
//     */
//    @Transactional
//    public void cancelBooking(Long bookingSq) {
//        Booking booking = findBookingThrow(bookingSq);
//        log.info(">>> [TEST MODE] 예매 취소 요청");
//
//        // 외부 서비스 통신 생략하고 상태만 변경
//        if (booking.getBookingStatus() == BookingStatus.PENDING) {
//            bookingRepository.delete(booking);
//        } else {
//            booking.cancel("테스트 취소");
//        }
//    }
//
//    /**
//     * [4] 티켓 개별 취소 (테스트용)
//     */
//    @Transactional
//    public void cancelTicket(Long bookingSq, Long ticketSq) {
//        Booking booking = findBookingThrow(bookingSq);
//        // 로직 생략하고 상태만 변경
//        booking.cancelTicket(ticketSq, "테스트 개별 취소");
//    }
//
//    // [기타 조회 메서드는 그대로 사용 가능]
//    public BookingResponse getBooking(Long bookingSq) {
//        return bookingMapper.EntityToResponse(findBookingThrow(bookingSq));
//    }
//
//    public List<BookingResponse> getBookingsByUser(Long userSq) {
//        return bookingRepository.findByUserSq(userSq).stream()
//                .map(bookingMapper::EntityToResponse)
//                .toList();
//    }
//
//    // 헬퍼 메서드
//    private Booking findBookingThrow(Long bookingSq) {
//        return bookingRepository.findByBookingSq(bookingSq)
//                .orElseThrow(() -> new IllegalArgumentException("예매 정보 없음: " + bookingSq));
//    }
//
//    // 사용하지 않는 메서드 임시 더미 처리
//    public int cancelExpiredBookings() { return 0; }
//    public void enterTicket(String uuid) {}
//}