package io.why503.reservationservice.Domain.Booking.Sv;

import io.why503.reservationservice.Domain.Booking.Model.Ett.Ticket;     // ★ model.ett
import io.why503.reservationservice.Domain.Booking.Model.Ett.Ticketing;  // ★ model.ett
import io.why503.reservationservice.Domain.Concert.Model.Ett.Discount;
import io.why503.reservationservice.Domain.Showing.Model.Ett.ShowingSeat; // ★ model.ett
import io.why503.reservationservice.Domain.Booking.Repo.TicketingRepo;
import io.why503.reservationservice.Domain.Showing.Repo.ShowingSeatRepo;
import io.why503.reservationservice.Domain.Concert.Repo.DiscountRepo; // 필요 시 생성
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingSv {

    private final TicketingRepo ticketingRepo;
    private final ShowingSeatRepo showingSeatRepo;
    private final DiscountRepo discountRepo;

    /**
     * [기능 1] 직접 좌석 선택 (Direct Selection)
     * 예: "A구역 5번 좌석(ID:105) 주세요"
     */
    @Transactional
    public Long createDirectBooking(Long userSq, Long showingSeatSq) {
        // 1. 좌석 조회 (Lock)
        ShowingSeat seat = showingSeatRepo.findByIdWithLock(showingSeatSq)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));

        // 2. 예매 진행
        return processBooking(userSq, seat);
    }

    /**
     * [기능 2] 빠른 좌석 선택 (Quick Selection / Random)
     * 예: "SS석 아무거나 하나 주세요" -> 시스템이 랜덤 배정
     */
    @Transactional
    public Long createQuickBooking(Long userSq, Long showingSq, String grade) {
        // 1. 해당 등급의 빈 좌석 목록 조회
        List<ShowingSeat> availableSeats = showingSeatRepo.findAvailableSeats(showingSq, grade);

        if (availableSeats.isEmpty()) {
            throw new IllegalStateException(grade + "석은 모두 매진되었습니다. (선택 실패)");
        }

        // 2. 랜덤으로 하나 뽑기
        Random random = new Random();
        ShowingSeat selectedSeat = availableSeats.get(random.nextInt(availableSeats.size()));

        // 3. 예매 진행 (주의: 실무에선 여기서 다시 락을 거는 것이 안전함)
        return processBooking(userSq, selectedSeat);
    }

    // [내부 공통 로직] 예매 생성 및 대기 상태 저장
    private Long processBooking(Long userSq, ShowingSeat seat) {
        // 1. 좌석 상태 검증 (0: 판매가능 상태만 예약 가능)
        // (Entity에 getShowingSeatStat() Getter가 있다고 가정)
        if (seat.getShowingSeatStat() != 0) {
            throw new IllegalStateException("이미 선택된 좌석입니다.");
        }

        // 2. 가격 조회 (DB 관계: ShowingSeat -> ShowSeat -> SeatClass -> Price)
        int price = seat.getShowSeat().getSeatClass().getSeatPrice();

        // 3. 좌석 선점 처리 (상태 1: HOLD)
        // (Entity에 추가했던 편의 메서드 사용)
        seat.changeStatus(1);

        // 4. 예매(Ticketing) 생성 (상태 0: 입금대기)
        Ticketing ticketing = Ticketing.builder()
                .userSq(userSq)
                .ticketingPay(price)
                .ticketingStat(0) // PENDING
                .build();

        // 5. 티켓(Ticket) 생성 (상태 0: 결제대기)
        Ticket ticket = Ticket.builder()
                .ticketing(ticketing)
                .showingSeat(seat)
                .ticketNo(UUID.randomUUID().toString())
                .ticketRealPrice(price) // 정가
                .ticketDis(0)       // 👈 이 줄을 추가하세요! (할인금액 0원)
                .ticketPrice(price)     // 최종가(할인 전)
                .ticketStat(0)          // PENDING
                .build();

        // 6. 저장
        ticketing.getTickets().add(ticket);
        ticketingRepo.save(ticketing);

        // 결제 및 할인쿠폰 적용 페이지로 넘길 ID 반환
        return ticketing.getTicketingSq();
    }

    @Transactional
    public Map<String, Object> applyCoupon(Long bookingId, Long couponId) {
        // 1. 예매 내역 조회
        Ticketing ticketing = ticketingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        // 2. 쿠폰 정보 조회
        Discount discount = discountRepo.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));

        // 3. 가격 계산 (티켓 1장 기준)
        Ticket ticket = ticketing.getTickets().get(0);
        int originalPrice = ticket.getTicketRealPrice();

        // ★ Discount 엔티티의 필드명에 맞춰 수정 (value, name)
        int discountValue = discount.getValue();
        int finalPrice = Math.max(0, originalPrice - discountValue);

        // 4. 엔티티 데이터 업데이트
        ticket.applyDiscount(discountValue, finalPrice);
        ticketing.updateTotalPay(finalPrice);

        // 5. 결과 반환 (이슈 명세 내용 반영)
        Map<String, Object> result = new HashMap<>();
        result.put("originalPrice", originalPrice);
        result.put("discountName", discount.getName()); // ★ getName()으로 수정
        result.put("discountAmount", discountValue);
        result.put("finalPrice", finalPrice);

        return result;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPaymentInfo(Long bookingId) {
        Ticketing ticketing = ticketingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        Ticket ticket = ticketing.getTickets().get(0);
        ShowingSeat showingSeat = ticket.getShowingSeat();

        // 최종 결제 명세서 작성
        Map<String, Object> paymentInfo = new HashMap<>();
        paymentInfo.put("bookingId", ticketing.getTicketingSq());
        paymentInfo.put("showName", "2026 월드투어"); // 실제로는 show 엔티티에서 가져옴
        paymentInfo.put("seatInfo", showingSeat.getShowSeat().getSeat().getSeatArea() + "구역 " +
                showingSeat.getShowSeat().getSeat().getAreaSeatNo() + "번");
        paymentInfo.put("originalPrice", ticket.getTicketRealPrice());
        paymentInfo.put("discountAmount", ticket.getTicketDis());
        paymentInfo.put("finalTotalPrice", ticketing.getTicketingPay()); // 최종 결제할 금액

        return paymentInfo;
    }

    @Transactional
    public void confirmBooking(Long bookingId) {
        Ticketing ticketing = ticketingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매입니다."));

        // 1. 예매 상태 완료(1)로 변경
        ticketing.changeStatus(1);

        for (Ticket ticket : ticketing.getTickets()) {
            // 2. 티켓 상태 완료(1)로 변경
            ticket.changeStatus(1);

            // 3. 좌석 상태 판매완료(2)로 변경
            ticket.getShowingSeat().changeStatus(2);
        }
    }

    /**
     * [기능] 예매 취소 (Soft Delete)
     * - 예매 내역(Ticketing) 상태 -> 2 (취소됨)
     * - 좌석(ShowingSeat) 상태 -> 0 (다시 판매 가능)
     */
    @Transactional
    public void cancelBooking(Long bookingId) {
        // 1. 예매 내역 조회
        Ticketing ticketing = ticketingRepo.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매 내역입니다."));

        // 2. 이미 '결제 완료(1)'된 건은 단순 취소 불가 (환불 로직 필요)
        if (ticketing.getTicketingStat() == 1) {
            throw new IllegalStateException("이미 결제가 완료된 티켓은 환불 절차를 이용해주세요.");
        }

        // 3. 이미 '취소(2)'된 건인지 체크
        if (ticketing.getTicketingStat() == 2) {
            throw new IllegalStateException("이미 취소된 내역입니다.");
        }

        // 4. 예매 상태를 '취소(2)'로 변경 (이력 남기기)
        ticketing.changeStatus(2);

        // 5. 연관된 티켓과 좌석 상태 변경
        for (Ticket ticket : ticketing.getTickets()) {
            ticket.changeStatus(2); // 티켓도 취소 처리

            // ★ 핵심: 좌석은 다시 누군가 예매할 수 있게 '0'으로 원복!
            ticket.getShowingSeat().changeStatus(0);
        }
    }
}