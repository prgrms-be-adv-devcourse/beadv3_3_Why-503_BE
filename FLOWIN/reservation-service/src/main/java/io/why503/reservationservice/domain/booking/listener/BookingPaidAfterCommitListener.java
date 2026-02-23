package io.why503.reservationservice.domain.booking.listener;

import io.why503.reservationservice.domain.entry.service.EntryTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 결제 확정 이후(트랜잭션 커밋 이후) entry token을 회수하는 리스너
 *
 * - confirmPaid()에서 publish한 이벤트를 수신
 * - commit 이후에 entry token을 delete 해서 del 이벤트를 발생시킨다
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingPaidAfterCommitListener {

    private final EntryTokenService entryTokenService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPaid(BookingPaidEvent event) {

        Long userSq = event.userSq();

        // 결제 완료가 확정된 뒤에만 token 회수
        entryTokenService.revokeByUserSq(userSq);

        log.info("booking paid -> entry token revoke done. userSq={}", userSq);
    }
}