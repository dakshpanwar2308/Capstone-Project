package com.icinema.common.dto;

import com.icinema.common.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
public record PaymentDto(
    Long id,
    @NotNull Long bookingId,
    @Positive double amount,
    PaymentStatus status,
    String providerReference,
    LocalDateTime processedAt,
    String receiptEmail
) {
}
