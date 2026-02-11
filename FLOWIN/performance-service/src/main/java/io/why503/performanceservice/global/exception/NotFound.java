package io.why503.performanceservice.global.exception;

import io.why503.commonbase.exception.performance.PerformanceException;
import org.springframework.http.HttpStatus;

public class NotFound extends PerformanceException {
  public NotFound(String message) {
    super(message, "404", HttpStatus.NOT_FOUND);
  }

  public NotFound(Throwable cause) {
    super(cause, "404", HttpStatus.NOT_FOUND);
  }
}