package com.icinema.common.dto;

import com.icinema.common.model.BookingStatus;
import com.icinema.common.model.CardType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
public record BookingDto(
    Long id,
    @NotNull Long showId,
    @NotBlank String customerName,
    @Email String customerEmail,
    LocalDateTime createdAt,
    @Positive double totalAmount,
    @PositiveOrZero double seatTotal,
    @PositiveOrZero double convenienceFee,
    @PositiveOrZero double gstAmount,
    @PositiveOrZero double discountAmount,
    BookingStatus status,
    @NotNull List<String> seatNumbers,
    CardType cardType,
    PaymentDto payment
) {
}
