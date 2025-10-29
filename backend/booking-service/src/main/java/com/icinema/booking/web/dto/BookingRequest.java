package com.icinema.booking.web.dto;

import com.icinema.common.model.CardType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record BookingRequest(
    @NotNull Long showId,
    @NotEmpty List<String> seatNumbers,
    @NotBlank String customerName,
    @Email String customerEmail,
    @NotBlank String currency,
    @NotNull CardType cardType,
    @NotBlank @Pattern(regexp = "\\d{16}") String cardNumber,
    @NotBlank @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}") String expiry,
    @NotBlank @Pattern(regexp = "\\d{3}") String cvv
) {
}
