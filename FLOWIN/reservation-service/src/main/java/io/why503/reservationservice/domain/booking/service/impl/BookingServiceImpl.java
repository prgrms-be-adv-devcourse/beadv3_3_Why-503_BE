package io.why503.reservationservice.domain.booking.service.impl;

import io.why503.reservationservice.domain.booking.mapper.BookingMapper;
import io.why503.reservationservice.domain.booking.model.dto.request.BookingCreateRequest;
import io.why503.reservationservice.domain.booking.model.dto.request.BookingDiscountRequest;
import io.why503.reservationservice.domain.booking.model.dto.request.BookingDiscountSeatRequest;
import io.why503.reservationservice.domain.booking.model.dto.response.BookingResponse;
import io.why503.reservationservice.domain.booking.model.entity.Booking;
import io.why503.reservationservice.domain.booking.model.entity.BookingSeat;
import io.why503.reservationservice.domain.booking.model.enums.BookingStatus;
import io.why503.reservationservice.domain.booking.model.enums.DiscountPolicy;
import io.why503.reservationservice.domain.booking.repository.BookingRepository;
import io.why503.reservationservice.domain.booking.service.BookingService;
import io.why503.reservationservice.domain.booking.util.BookingExceptionFactory;
import io.why503.reservationservice.global.client.PerformanceClient;
import io.why503.reservationservice.global.client.dto.request.SeatReserveRequest;
import io.why503.reservationservice.global.client.dto.response.RoundSeatResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import io.why503.reservationservice.domain.entry.service.EntryTokenValidator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.why503.reservationservice.domain.booking.listener.BookingPaidEvent;

/**
 * 예매 및 좌석 선점 데이터의 생명주기를 관리하는 서비스 구현체
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
    private final ApplicationEventPublisher eventPublisher; // 이벤트 발행기
    private final EntryTokenValidator entryTokenValidator;

    // 신규 예매 요청 시 좌석의 중복 점유를 방지하고 외부 서비스에 선점 상태 기록
    @Override
    @Transactional
    public BookingResponse createBooking(Long userSq, BookingCreateRequest request) {
        if (userSq == null || userSq <= 0) {
            throw BookingExceptionFactory.bookingBadRequest("사용자 정보가 유효하지 않습니다.");
        }
        if (request.roundSeatSqs() == null || request.roundSeatSqs().isEmpty()) {
            throw BookingExceptionFactory.bookingBadRequest("예매할 좌석 정보가 없습니다.");
        }

        // EntryToken 검증 (없으면 401/403)
        entryTokenValidator.validate(userSq, request.roundSq());
        List<Long> requestedSeats = request.roundSeatSqs();

        List<BookingStatus> activeStatuses = List.of(BookingStatus.PENDING, BookingStatus.PAID);
        List<Booking> conflictingBookings = bookingRepository.findConflictingBookings(requestedSeats, activeStatuses);

        if (!conflictingBookings.isEmpty()) {
            throw BookingExceptionFactory.bookingConflict("요청한 좌석 중 이미 선점된 좌석이 존재합니다.");
        }

        List<RoundSeatResponse> seatDetails;
        try {
            seatDetails = performanceClient.findRoundSeats(new SeatReserveRequest(requestedSeats));
        } catch (Exception e) {
            log.error("공연 정보 조회 실패: {}", e.getMessage());
            throw BookingExceptionFactory.bookingBadRequest("공연 정보를 불러오는 데 실패했습니다.");
        }

        if (seatDetails == null || seatDetails.isEmpty()) {
            throw BookingExceptionFactory.bookingBadRequest("유효하지 않은 좌석 정보입니다.");
        }

        String category = seatDetails.getFirst().category();
        String genre = seatDetails.getFirst().genre();

        try {
            performanceClient.reserveRoundSeats(userSq, new SeatReserveRequest(requestedSeats));
        } catch (Exception e) {
            log.error("공연 서비스 좌석 선점 요청 실패: {}", e.getMessage());
            throw BookingExceptionFactory.bookingBadRequest("좌석 선점에 실패했습니다.");
        }

        String orderId = "BOOKING-" + UUID.randomUUID();
        Booking booking = Booking.builder()
                .userSq(userSq)
                .orderId(orderId)
                .category(category)
                .genre(genre)
                .build();

        for (Long seatSq : requestedSeats) {
            booking.addBookingSeat(BookingSeat.builder()
                    .roundSeatSq(seatSq)
                    .discountPolicy(DiscountPolicy.NONE)
                    .build());
        }

        Booking savedBooking = bookingRepository.save(booking);
        log.info("예매 생성 및 좌석 선점 완료. 식별자: {}, 주문번호: {}", userSq, orderId);

        return bookingMapper.entityToResponse(savedBooking);
    }

    // 예매 상태와 권한을 확인한 후 각 좌석에 지정된 할인 정책을 일괄 갱신
    @Override
    @Transactional
    public BookingResponse applyDiscounts(Long userSq, Long bookingSq, BookingDiscountRequest request) {
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("존재하지 않는 예매입니다."));

        if (!booking.getUserSq().equals(userSq)) {
            throw BookingExceptionFactory.bookingForbidden("본인의 예매에만 할인을 적용할 수 있습니다.");
        }
        if (booking.getStatus() == BookingStatus.PAID) {
            throw BookingExceptionFactory.bookingConflict("이미 결제된 예매는 변경할 수 없습니다.");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw BookingExceptionFactory.bookingConflict("취소된 예매에는 할인을 적용할 수 없습니다.");
        }

        if (request.seats() != null) {
            for (BookingDiscountSeatRequest seatRequest : request.seats()) {
                booking.getBookingSeats().stream()
                        .filter(seat -> seat.getRoundSeatSq().equals(seatRequest.roundSeatSq()))
                        .findFirst()
                        .ifPresent(seat -> seat.changeDiscountPolicy(seatRequest.discountPolicy()));
            }
        }

        return bookingMapper.entityToResponse(booking);
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

    // 결제 전 단계의 예매를 철회하고 외부 서비스에 점유 중인 좌석의 해제를 요청
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

        if (roundSeatSqs != null && !roundSeatSqs.isEmpty()) {
            int totalSeatCount = booking.getBookingSeats().size();
            if (roundSeatSqs.size() != totalSeatCount) {
                throw BookingExceptionFactory.bookingBadRequest("결제 전 단계에서는 전체 취소만 가능합니다.");
            }
        }

        List<Long> seatsToCancel = booking.getBookingSeats().stream()
                .map(BookingSeat::getRoundSeatSq)
                .toList();

        if (!seatsToCancel.isEmpty()) {
            try {
                performanceClient.cancelRoundSeats(userSq, new SeatReserveRequest(seatsToCancel));
            } catch (Exception e) {
                log.error("공연 서비스 좌석 선점 해제 실패. 주문번호: {}, 사유: {}", booking.getOrderId(), e.getMessage());
                throw BookingExceptionFactory.bookingBadRequest("좌석 해제 중 오류가 발생했습니다.");
            }
        }
        bookingRepository.delete(booking);

        booking.cancel();

        return bookingMapper.entityToResponse(booking);
    }

    // 결제 유효 시간이 경과한 미결제 예매 건들을 선별하여 자동 무효화 및 좌석 복구 수행
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
                        .map(BookingSeat::getRoundSeatSq)
                        .toList();

                if (!seatsToCancel.isEmpty()) {
                    performanceClient.cancelRoundSeats(booking.getUserSq(), new SeatReserveRequest(seatsToCancel));
                }

                bookingRepository.delete(booking);

                cancelCount++;
            } catch (Exception e) {
                log.error("만료 예매 자동 취소 실패 (식별번호: {}): {}", booking.getSq(), e.getMessage());
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
            throw BookingExceptionFactory.bookingBadRequest("주문 번호는 필수입니다.");
        }

        Booking booking = bookingRepository.findByOrderId(orderId)
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("예매 내역이 존재하지 않습니다."));

        return bookingMapper.entityToResponse(booking);
    }

    // 결제 시스템으로부터 수신한 승인 결과를 예매 모델에 반영하여 확정 처리
    @Override
    @Transactional
    public void confirmPaid(Long userSq, Long bookingSq) {
        Booking booking = bookingRepository.findById(bookingSq)
                .orElseThrow(() -> BookingExceptionFactory.bookingNotFound("존재하지 않는 예매입니다."));

        if (!booking.getUserSq().equals(userSq)) {
            throw BookingExceptionFactory.bookingForbidden("본인의 예매만 확정할 수 있습니다.");
        }

        booking.paid();
        log.info("예매 결제 완료 확정. 주문번호: {}", booking.getOrderId());

        // 트랜젝션 커밋 이후 토큰 회수 하도록 이벤트 발행
        eventPublisher.publishEvent(new BookingPaidEvent(userSq));
    }

    // 환불 절차에 따라 지정된 좌석들의 예매 기록을 제거하고 판매 가능 상태로 복구
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
            performanceClient.cancelRoundSeats(userSq, new SeatReserveRequest(validSqs));
        } catch (Exception e) {
            log.error("환불 좌석 재고 해제 실패. 식별번호: {}, 좌석목록: {}, 사유: {}",
                    bookingSq, validSqs, e.getMessage());
            throw BookingExceptionFactory.bookingBadRequest("외부 서비스 재고 해제 중 오류가 발생했습니다.");
        }

        booking.removeSeats(validSqs);
        if (booking.getBookingSeats().isEmpty()) {
            booking.cancel();
        }
    }
}