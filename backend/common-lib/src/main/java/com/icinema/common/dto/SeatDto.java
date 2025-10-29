package com.icinema.common.dto;

import com.icinema.common.model.SeatStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record SeatDto(
    Long id,
    @NotNull Long showId,
    @NotBlank String seatNumber,
    @NotNull SeatStatus status,
    double price
) {
}
