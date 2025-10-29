package com.icinema.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
public record MovieDto(
    Long id,
    @NotBlank String title,
    @NotBlank String genre,
    @NotBlank String language,
    @Positive int durationMinutes,
    double rating,
    String posterUrl,
    String synopsis,
    @NotNull LocalDate releaseDate
) {
}
