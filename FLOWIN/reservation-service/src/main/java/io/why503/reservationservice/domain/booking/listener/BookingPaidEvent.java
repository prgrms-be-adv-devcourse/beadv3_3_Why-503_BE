package io.why503.reservationservice.domain.booking.listener;
/**
 * 예매 결제 완료 이벤트
 *
 * - confirmPaid()에서 발행
 * - AFTER_COMMIT 리스너에서 수신
 * - entry token revokeByUserSq(userSq) 호출 트리거용
 */
public record BookingPaidEvent(
        Long userSq
) { }