package com.icinema.common.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
public record ShowDto(
    Long id,
    @NotNull Long movieId,
    @NotNull Long theatreId,
    @FutureOrPresent LocalDateTime startTime,
    double basePrice,
    String screen
) {
}
