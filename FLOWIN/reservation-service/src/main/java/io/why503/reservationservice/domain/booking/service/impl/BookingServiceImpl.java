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
import io.why503.reservationservice.global.client.PerformanceClient;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 예매 및 좌석 선점 데이터의 생명주기를 관리하는 서비스
 * - 공연 서비스와 연동하여 좌석 상태 동기화 및 예매 무효화 처리 수행
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final EntityManager entityManager;
    private final PerformanceClient performanceClient;

    // 신규 예매 요청 시 중복 점유를 방지하고 외부 서비스에 좌석 선점 기록
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

        List<BookingStatus> activeStatuses = List.of(BookingStatus.PENDING, BookingStatus.PAID);
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(requestedSeats, activeStatuses);

        if (!conflictingBookings.isEmpty()) {
            throw BookingExceptionFactory.bookingConflict("요청한 좌석 중 이미 선점된 좌석이 존재합니다.");
        }

        try {
            performanceClient.reserveRoundSeats(userSq, requestedSeats);
        } catch (Exception e) {
            log.error("공연 서비스 좌석 선점 요청 실패: {}", e.getMessage());
            throw BookingExceptionFactory.bookingBadRequest("좌석 선점에 실패했습니다.");
        }

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

    // 결제 전 단계에서의 예매 철회 및 외부 점유 해제 전파
    @Override
    @Transactional
    public BookingResponse cancelBooking(Long userSq, Long bookingSq, List<Long> roundSeatSqs, String reason) {
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("존재하지 않는 예매입니다."));

        if (!booking.getUserSq().equals(userSq)) {
            throw BookingExceptionFactory.bookingForbidden("본인의 예매만 취소할 수 있습니다.");
        }

        if (booking.getStatus() == BookingStatus.PAID) {
            throw BookingExceptionFactory.bookingConflict("이미 결제된 예매입니다.");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw BookingExceptionFactory.bookingConflict("이미 취소된 예매입니다.");
        }

        // 선점 단계에서는 데이터 정합성을 위해 일괄 취소만 허용
        if (roundSeatSqs != null && !roundSeatSqs.isEmpty()) {
            int totalSeatCount = booking.getBookingSeats().size();
            if (roundSeatSqs.size() != totalSeatCount) {
                throw BookingExceptionFactory.bookingBadRequest("결제 전 단계에서는 전체 취소만 가능합니다.");
            }
        }

        booking.cancel();

        List<Long> seatsToCancel = booking.getBookingSeats().stream()
                .map((seat) -> seat.getRoundSeatSq())
                .toList();

        if (!seatsToCancel.isEmpty()) {
            try {
                performanceClient.cancelRoundSeats(seatsToCancel);
            } catch (Exception e) {
                log.error("공연 서비스 좌석 선점 해제 실패. OrderId: {}, Error: {}", booking.getOrderId(), e.getMessage());
                throw BookingExceptionFactory.bookingBadRequest("좌석 해제 중 오류가 발생했습니다.");
            }
        }

        return bookingMapper.entityToResponse(booking);
    }

    // 미결제 상태로 유지된 선점 좌석들을 주기적으로 회수
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

                List<Long> seatsToCancel = booking.getBookingSeats().stream()
                        .map(seat -> seat.getRoundSeatSq())
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
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("예매 내역이 존재하지 않습니다."));

        return bookingMapper.entityToResponse(booking);
    }

    // 결제 시스템의 승인 결과를 도메인 모델에 반영
    @Override
    @Transactional
    public void confirmPaid(Long userSq, Long bookingSq) {
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("존재하지 않는 예매입니다."));

        if (!booking.getUserSq().equals(userSq)) {
            throw BookingExceptionFactory.bookingForbidden("본인의 예매만 확정할 수 있습니다.");
        }

        booking.paid();
        log.info("예매 결제 완료 확정. BookingSQ: {}, OrderId: {}", booking.getSq(), booking.getOrderId());
    }

    // 환불 진행 시 대상 좌석들의 점유 상태를 최종 해제
    @Override
    @Transactional
    public void refundSeats(Long userSq, Long bookingSq, List<Long> roundSeatSqs) {
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("환불할 예매 내역이 없습니다."));

        if (!booking.getUserSq().equals(userSq)) {
            throw BookingExceptionFactory.bookingForbidden("본인의 예매 재고만 해제할 수 있습니다.");
        }

        List<Long> validSqs = new ArrayList<>();

        for (BookingSeat seat : booking.getBookingSeats()) {
            Long seatSq = seat.getRoundSeatSq();
            if (roundSeatSqs.contains(seatSq)) {
                validSqs.add(seatSq);
            }
        }

        if (validSqs.isEmpty()) {
            throw BookingExceptionFactory.bookingBadRequest("요청한 좌석이 예매 정보와 일치하지 않습니다.");
        }

        try {
            performanceClient.cancelRoundSeats(validSqs);
        } catch (Exception e) {
            log.error("환불 좌석 재고 해제 실패. BookingSQ: {}, Seats: {}, Error: {}",
                    bookingSq, validSqs, e.getMessage());
            throw BookingExceptionFactory.bookingBadRequest("외부 서비스 재고 해제 중 오류가 발생했습니다.");
        }

        booking.removeSeats(validSqs);
        if (booking.getBookingSeats().isEmpty()) {
            booking.cancel();
        }
    }


}