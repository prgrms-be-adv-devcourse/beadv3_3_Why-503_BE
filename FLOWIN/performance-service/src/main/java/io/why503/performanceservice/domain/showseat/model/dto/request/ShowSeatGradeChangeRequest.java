package io.why503.performanceservice.domain.showseat.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ShowSeatGradeChangeRequest (
    @NotBlank String grade      // VIP, R, S, A ...
){ }
