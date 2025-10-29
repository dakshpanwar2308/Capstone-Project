package com.icinema.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
public record PaymentRequest(
    @NotNull Long bookingId,
    @Positive double amount,
    @NotBlank String currency,
    @Email String receiptEmail,
    @NotBlank String cardToken
) {
}
