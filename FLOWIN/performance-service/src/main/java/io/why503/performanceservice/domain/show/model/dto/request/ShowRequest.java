package io.why503.performanceservice.domain.show.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ShowRequest(

        @NotBlank(message = "공연명은 필수.")
        String showName,

        @NotNull(message = "시작일시는 필수.")
        LocalDateTime showStartDt,

        @NotNull(message = "종료일시는 필수.")
        LocalDateTime showEndDt,

        @NotNull(message = "오픈일시는 필수.")
        LocalDateTime showOpenDt,

        @NotBlank(message = "러닝타임은 필수.")
        String showRunningTime,

        @NotBlank(message = "관람등급은 필수.")
        String showViewingAge,

        @NotNull(message = "카테고리는 필수.")
        String showCategory,

        @NotNull(message = "장르는 필수.")
        String showGenre,

        @NotBlank(message = "상태값은 필수.")
        String showStatus,

        @NotNull(message = "공연장은 필수.")
        Long hallSq
) {}

