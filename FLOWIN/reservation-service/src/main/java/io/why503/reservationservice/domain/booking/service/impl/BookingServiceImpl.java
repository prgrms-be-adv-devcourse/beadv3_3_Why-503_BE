package io.why503.reservationservice.domain.booking.service.impl;

import io.why503.reservationservice.domain.booking.mapper.BookingMapper;
import io.why503.reservationservice.domain.booking.model.dto.request.BookingCreateRequest;
import io.why503.reservationservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.reservationservice.domain.booking.model.entity.Booking;
import io.why503.reservationservice.domain.booking.model.entity.BookingSeat;
import io.why503.reservationservice.domain.booking.model.enums.BookingStatus;
import io.why503.reservationservice.domain.booking.repository.BookingRepository;
import io.why503.reservationservice.domain.booking.service.BookingService;
import io.why503.reservationservice.domain.booking.util.BookingExceptionFactory;
import io.why503.reservationservice.global.client.PerformanceClient; // Client import 추가
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 좌석 선점 및 예매 데이터의 생명주기를 관리하는 서비스
 * - PerformanceClient를 통해 외부 서비스(공연)와 좌석 상태를 동기화함
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final EntityManager entityManager;
    private final PerformanceClient performanceClient; // 외부 통신 클라이언트 주입

    /**
     * 예매 생성 (좌석 선점)
     */
    @Override
    @Transactional
    public BookingResponse createBooking(Long userSq, BookingCreateRequest request) {
        if (userSq == null || userSq <= 0) {
            throw BookingExceptionFactory.bookingBadRequest("사용자 정보가 유효하지 않습니다.");
        }
        if (request.roundSeatSqs() == null || request.roundSeatSqs().isEmpty()) {
            throw BookingExceptionFactory.bookingBadRequest("예매할 좌석 정보가 없습니다.");
        }

        List<Long> requestedSeats = request.roundSeatSqs();

        // 1. [내부 검증] 이미 선점된 좌석인지 확인 (DB 레벨 방어)
        List<BookingStatus> activeStatuses = List.of(BookingStatus.PENDING, BookingStatus.PAID);
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(requestedSeats, activeStatuses);

        if (!conflictingBookings.isEmpty()) {
            throw BookingExceptionFactory.bookingConflict("요청한 좌석 중 이미 선점된 좌석이 존재합니다.");
        }

        // 2. [외부 연동] 공연 서비스에 좌석 선점 요청 (상태 동기화)
        // 실패 시 FeignException 발생 -> 트랜잭션 롤백
        try {
            performanceClient.reserveRoundSeats(userSq, requestedSeats);
        } catch (Exception e) {
            log.error("공연 서비스 좌석 선점 요청 실패: {}", e.getMessage());
            throw BookingExceptionFactory.bookingBadRequest("좌석 선점에 실패했습니다. (타인이 이미 선점했거나 시스템 오류)");
        }

        // 3. [저장] 예매 정보 생성 및 저장
        String orderId = "BOOKING-" + UUID.randomUUID();
        Booking booking = Booking.builder()
                .userSq(userSq)
                .orderId(orderId)
                .build();

        for (Long seatSq : requestedSeats) {
            booking.addBookingSeat(BookingSeat.builder().roundSeatSq(seatSq).build());
        }

        Booking savedBooking = bookingRepository.save(booking);
        log.info("예매 생성 및 좌석 선점 완료. OrderId: {}, UserSq: {}", orderId, userSq);

        return bookingMapper.entityToResponse(savedBooking);
    }

    // (조회 메서드들은 변경 없음 - 생략 가능하지만 전체 맥락을 위해 유지하거나 생략 표시)
    @Override
    public BookingResponse findBooking(Long userSq, Long bookingSq) {
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("존재하지 않는 예매입니다."));
        if (!booking.getUserSq().equals(userSq)) {
            throw BookingExceptionFactory.bookingForbidden("본인의 예매 내역만 조회할 수 있습니다.");
        }
        return bookingMapper.entityToResponse(booking);
    }

    @Override
    public List<BookingResponse> findBookingsByUser(Long userSq) {
        if (userSq == null) throw BookingExceptionFactory.bookingBadRequest("사용자 정보는 필수입니다.");
        return bookingRepository.findAllByUserSqOrderByCreatedDtDesc(userSq).stream()
                .map(booking -> bookingMapper.entityToResponse(booking))
                .toList();
    }

    /**
     * 예매 취소 (결제 전 선점 취소)
     * - [땅땅땅 규칙] 결제 전(PENDING) 상태에서는 부분 취소가 불가능하며 전체 취소만 허용한다.
     */
    @Override
    @Transactional
    public BookingResponse cancelBooking(Long userSq, Long bookingSq, List<Long> roundSeatSqs, String reason) {
        // 1. 예매 내역 조회
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("존재하지 않는 예매입니다."));

        // 2. 소유권 검증
        if (!booking.getUserSq().equals(userSq)) {
            throw BookingExceptionFactory.bookingForbidden("본인의 예매만 취소할 수 있습니다.");
        }

        // 3. 상태 검증 (이미 결제된 경우 차단)
        if (booking.getStatus() == BookingStatus.PAID) {
            throw BookingExceptionFactory.bookingConflict("이미 결제된 예매입니다. 결제 서비스의 환불 API를 이용해주세요.");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw BookingExceptionFactory.bookingConflict("이미 취소된 예매입니다.");
        }

        // 4. [규칙 반영] 결제 전 부분 취소 시도 차단
        // roundSeatSqs가 비어있지 않은데, 전체 좌석 수와 다르면 부분 취소 시도로 간주
        if (roundSeatSqs != null && !roundSeatSqs.isEmpty()) {
            int totalSeatCount = booking.getBookingSeats().size();
            if (roundSeatSqs.size() != totalSeatCount) {
                throw BookingExceptionFactory.bookingBadRequest("결제 전 선점 단계에서는 부분 취소가 불가능합니다. 전체 취소 후 다시 예매해주세요.");
            }
        }

        // 5. 전체 취소 처리 (데이터 삭제가 아닌 상태 변경)
        booking.cancel(); // BookingStatus -> CANCELLED

        // 6. 외부 서비스 연동 (공연 서비스 좌석 해제)
        // 메서드 참조 금지 규칙에 따라 람다 사용
        List<Long> seatsToCancel = booking.getBookingSeats().stream()
                .map((seat) -> seat.getRoundSeatSq())
                .toList();

        if (!seatsToCancel.isEmpty()) {
            try {
                performanceClient.cancelRoundSeats(seatsToCancel);
            } catch (Exception e) {
                // 취소 전파 실패 시 로그를 남기고, 보상 트랜잭션 정책에 따라 처리
                log.error("공연 서비스 좌석 선점 해제 실패. OrderId: {}, Error: {}", booking.getOrderId(), e.getMessage());
                // 시스템 무결성을 위해 외부 연동 실패 시 사용자에게 알림
                throw BookingExceptionFactory.bookingBadRequest("좌석 해제 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            }
        }

        return bookingMapper.entityToResponse(booking);
    }

    /**
     * 만료 예매 자동 취소
     */
    @Override
    @Transactional
    public int cancelExpiredBookings(int expirationMinutes) {
        LocalDateTime criteriaDt = LocalDateTime.now().minusMinutes(expirationMinutes);

        List<Booking> expiredBookings = entityManager.createQuery(
                        "SELECT b FROM Booking b WHERE b.status = :status AND b.createdDt < :criteriaDt", Booking.class)
                .setParameter("status", BookingStatus.PENDING)
                .setParameter("criteriaDt", criteriaDt)
                .getResultList();

        int cancelCount = 0;
        for (Booking booking : expiredBookings) {
            try {
                booking.cancel();

                // [외부 연동] 해당 예매의 모든 좌석 해제 요청
                List<Long> seatsToCancel = booking.getBookingSeats().stream()
                        .map(BookingSeat::getRoundSeatSq)
                        .toList();

                if (!seatsToCancel.isEmpty()) {
                    performanceClient.cancelRoundSeats(seatsToCancel);
                }

                cancelCount++;
            } catch (Exception e) {
                log.error("만료 예매 자동 취소 실패 (ID: {}): {}", booking.getSq(), e.getMessage());
            }
        }

        if (cancelCount > 0) log.info("만료된 예매 {}건 자동 취소 및 좌석 해제 완료.", cancelCount);
        return cancelCount;
    }

    @Override
    public Booking findByOrderId(String orderId) {
        if (orderId == null) throw BookingExceptionFactory.bookingBadRequest("주문 번호는 필수입니다.");
        return bookingRepository.findByOrderId(orderId).orElse(null);
    }

    @Override
    public BookingResponse findBookingByOrderId(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw BookingExceptionFactory.bookingBadRequest("주문 번호(OrderId)는 필수입니다.");
        }

        Booking booking = bookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("해당 주문 번호의 예매 내역이 존재하지 않습니다. OrderId: " + orderId));

        return bookingMapper.entityToResponse(booking);
    }

    @Override
    @Transactional
    public void confirmPaid(Long userSq, Long bookingSq) {
        // 1. 예매 조회 및 존재 여부 검증 (해피 패스 금지)
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("존재하지 않는 예매입니다."));

        // 2. 권한 검증 (내 예매가 맞는지)
        if (!booking.getUserSq().equals(userSq)) {
            throw BookingExceptionFactory.bookingForbidden("본인의 예매만 확정할 수 있습니다.");
        }

        // 3. 엔티티의 paid() 호출 (내부에서 PENDING 상태인지 검증함)
        booking.paid(); //

        // 4. 명시적 로그 (추적용)
        log.info("예매 결제 완료 확정. BookingSQ: {}, OrderId: {}", booking.getSq(), booking.getOrderId());

        // @Transactional에 의해 변경 감지(Dirty Checking)로 업데이트됨
    }

    @Override
    @Transactional
    public void refundSeats(Long userSq, Long bookingSq, List<Long> roundSeatSqs) {
        // 1. 예매 조회 및 소유권 검증 (해피 패스 금지)
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("환불할 예매 내역이 없습니다."));

        if (!booking.getUserSq().equals(userSq)) {
            throw BookingExceptionFactory.bookingForbidden("본인의 예매 재고만 해제할 수 있습니다.");
        }

        // 2. [규칙 준수] 메서드 참조(::) 금지, 람다 사용
        // Booking 내부에 해당 좌석들이 실제로 포함되어 있는지 검증 (데이터 정합성)
        List<Long> validSqs = booking.getBookingSeats().stream()
                .map((seat) -> seat.getRoundSeatSq())
                .filter((sq) -> roundSeatSqs.contains(sq))
                .toList();

        if (validSqs.isEmpty()) {
            throw BookingExceptionFactory.bookingBadRequest("요청한 좌석이 예매 정보와 일치하지 않습니다.");
        }

        // 3. 공연 서비스(Performance)에 좌석 해제 요청 (재고 방출의 마침표)
        try {
            performanceClient.cancelRoundSeats(validSqs);
        } catch (Exception e) {
            log.error("환불 좌석 재고 해제 실패. BookingSQ: {}, Seats: {}, Error: {}",
                    bookingSq, validSqs, e.getMessage());
            // 환불은 이미 Payment에서 성공했으므로, 여기서는 실패 시 별도 로그/재시도 큐가 필요함
            throw BookingExceptionFactory.bookingBadRequest("외부 서비스 재고 해제 중 오류가 발생했습니다.");
        }

        // 4. 모든 좌석이 환불된 경우 Booking 상태를 CANCELLED로 변경
        // (부분 환불 시에는 상태 유지 또는 PARTIAL_CANCEL 등의 정책에 따름)
        if (validSqs.size() == booking.getBookingSeats().size()) {
            booking.cancel();
        }
    }
}