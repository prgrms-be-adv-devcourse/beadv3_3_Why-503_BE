package io.why503.paymentservice.domain.booking.repo;

import io.why503.paymentservice.domain.booking.model.ett.Ticket;
import io.why503.paymentservice.domain.booking.model.vo.TicketStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface TicketRepo extends JpaRepository<Ticket, Long> {

    // @Query 문자열 없이, 메서드 이름만으로 JPA가 알아서 SQL을 만들어줍니다.
    // 해석: "ShowingSeatSq가 일치하고(AND), TicketStatus가 목록 안에 포함되는(In) 데이터가 존재하는가(Exists)?"
    boolean isSold(Long showingSeatSq, Collection<TicketStat> statuses);

}