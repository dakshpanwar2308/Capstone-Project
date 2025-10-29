package com.icinema.movies.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record RatingRequest(
    @NotNull
    @DecimalMin(value = "1.0")
    @DecimalMax(value = "5.0")
    Double rating
) {
}
