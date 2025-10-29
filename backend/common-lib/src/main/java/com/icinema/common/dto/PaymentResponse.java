package com.icinema.common.dto;

import com.icinema.common.model.PaymentStatus;
public record PaymentResponse(
    PaymentStatus status,
    String providerReference,
    String message
) {
}
