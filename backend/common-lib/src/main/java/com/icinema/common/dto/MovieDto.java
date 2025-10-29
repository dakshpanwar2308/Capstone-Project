package com.icinema.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
public record MovieDto(
    Long id,
    @NotBlank String title,
    @NotBlank String genre,
    @NotBlank String language,
    @Positive int durationMinutes,
    double rating,
    @NotBlank String censorRating,
    @PositiveOrZero long ratingCount,
    String posterUrl,
    String synopsis,
    @NotNull LocalDate releaseDate
) {
}
