package io.why503.performanceservice.global.exception.impl;

import io.why503.commonbase.exception.CustomException;
import io.why503.commonbase.exception.PathFinder;
import io.why503.commonbase.exception.account.domain.AccountAccountException;
import io.why503.commonbase.exception.account.domain.AccountAuthException;
import io.why503.commonbase.exception.account.domain.AccountCompanyException;
import io.why503.performanceservice.domain.hall.util.HallExceptionFactory;
import io.why503.performanceservice.domain.round.util.RoundExceptionFactory;
import io.why503.performanceservice.domain.roundSeat.util.RoundSeatExceptionFactory;
import io.why503.performanceservice.domain.seat.util.SeatExceptionFactory;
import io.why503.performanceservice.domain.show.util.ShowExceptionFactory;
import io.why503.performanceservice.domain.showseat.util.ShowSeatExceptionFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class PathFinderImpl implements PathFinder {
    @Override
    public CustomException findPath(HttpServletRequest request, String message, HttpStatus status) {
        String s = request.getRequestURI().split("/")[1];
        return switch (s) {
            case "hall" -> HallExceptionFactory.hallBadRequest(message);
            case "shows" -> ShowExceptionFactory.showBadRequest(message);
            case "seat" -> SeatExceptionFactory.seatBadRequest(message);
            case "showSeat" -> ShowSeatExceptionFactory.showSeatBadRequest(message);
            case "round" -> RoundExceptionFactory.roundBadRequest(message);
            case "roundSeat" -> RoundSeatExceptionFactory.roundSeatBadRequest(message);
            default -> null;
        };
    }
}
