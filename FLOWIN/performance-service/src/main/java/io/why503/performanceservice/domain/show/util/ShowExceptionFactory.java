package io.why503.performanceservice.domain.show.util;

import io.why503.commonbase.exception.CustomException;
import io.why503.performanceservice.domain.show.util.exception.ShowBadRequest;
import io.why503.performanceservice.domain.show.util.exception.ShowConflict;
import io.why503.performanceservice.domain.show.util.exception.ShowForbidden;
import io.why503.performanceservice.domain.show.util.exception.ShowNotFound;


public final class ShowExceptionFactory {

    // 400 Bad Request
    public static CustomException showBadRequest(String message){
        return new ShowBadRequest(message);
    }
    public static CustomException showBadRequest(Throwable cause){
        return new ShowBadRequest(cause);
    }

    // 404 Not Found
    public static CustomException showNotFound(String message) {
        return new ShowNotFound(message);
    }
    public static CustomException showNotFound(Throwable cause) {
        return new ShowNotFound(cause);
    }

    // 403 Forbidden
    public static CustomException showForbidden(String message) {
        return new ShowForbidden(message);
    }
    public static CustomException showForbidden(Throwable cause) {
        return new ShowForbidden(cause);
    }

    // 409 Conflict
    public static CustomException showConflict(String message) {
        return new ShowConflict(message);
    }
    public static CustomException showConflict(Throwable cause) {
        return new ShowConflict(cause);
    }
}