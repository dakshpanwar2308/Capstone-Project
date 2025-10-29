package com.icinema.common.dto;

import jakarta.validation.constraints.NotBlank;
public record TheatreDto(
    Long id,
    @NotBlank String name,
    @NotBlank String city,
    String address,
    String contactNumber
) {
}
