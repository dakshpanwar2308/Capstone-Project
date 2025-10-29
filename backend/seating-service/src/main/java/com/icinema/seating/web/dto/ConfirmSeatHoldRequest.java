package com.icinema.seating.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConfirmSeatHoldRequest(
    @NotNull Long showId,
    @NotBlank String holdToken
) {
}
