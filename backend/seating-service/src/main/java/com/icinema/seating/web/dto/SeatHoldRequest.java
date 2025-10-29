package com.icinema.seating.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SeatHoldRequest(
    @NotNull Long showId,
    @NotEmpty List<String> seatNumbers,
    @Min(30) int holdSeconds
) {
}
