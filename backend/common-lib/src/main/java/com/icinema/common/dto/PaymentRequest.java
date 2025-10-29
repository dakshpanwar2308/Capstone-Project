package com.icinema.common.dto;

import com.icinema.common.model.CardType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
public record PaymentRequest(
    @NotNull Long bookingId,
    @Positive double amount,
    @NotBlank String currency,
    @Email String receiptEmail,
    @NotNull CardType cardType,
    @NotBlank @Pattern(regexp = "\\d{16}") String cardNumber,
    @NotBlank @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}") String expiry,
    @NotBlank @Pattern(regexp = "\\d{3}") String cvv
) {
}
