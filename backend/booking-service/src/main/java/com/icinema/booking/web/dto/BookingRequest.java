package com.icinema.booking.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record BookingRequest(
    @NotNull Long showId,
    @NotEmpty List<String> seatNumbers,
    @NotBlank String customerName,
    @Email String customerEmail,
    @NotBlank String currency,
    @NotBlank String cardToken
) {
}
