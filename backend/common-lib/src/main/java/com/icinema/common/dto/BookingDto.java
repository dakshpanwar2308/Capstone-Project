package com.icinema.common.dto;

import com.icinema.common.model.BookingStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
public record BookingDto(
    Long id,
    @NotNull Long showId,
    @NotBlank String customerName,
    @Email String customerEmail,
    LocalDateTime createdAt,
    @Positive double totalAmount,
    BookingStatus status,
    @NotNull List<String> seatNumbers,
    PaymentDto payment
) {
}
