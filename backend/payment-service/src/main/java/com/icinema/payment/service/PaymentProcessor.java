package com.icinema.payment.service;

import com.icinema.common.dto.PaymentDto;
import com.icinema.common.dto.PaymentRequest;
import com.icinema.common.model.PaymentStatus;
import com.icinema.payment.domain.Payment;
import com.icinema.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PaymentProcessor {

    private final PaymentRepository paymentRepository;

    public PaymentProcessor(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentDto charge(PaymentRequest request) {
        PaymentStatus status = simulateStatus(request.cardToken());
        Payment payment = new Payment();
        payment.setBookingId(request.bookingId());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());
        payment.setStatus(status);
        payment.setReceiptEmail(request.receiptEmail());
        payment.setProviderReference(status == PaymentStatus.SUCCEEDED ? UUID.randomUUID().toString() : null);
        Payment saved = paymentRepository.save(payment);
        return toDto(saved);
    }

    @Transactional
    public PaymentDto refund(PaymentRequest request) {
        Payment payment = paymentRepository.findByBookingId(request.bookingId())
            .orElseThrow(() -> new IllegalArgumentException("Payment not found for booking: " + request.bookingId()));
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setProviderReference("refund-" + UUID.randomUUID());
        Payment saved = paymentRepository.save(payment);
        return toDto(saved);
    }

    private PaymentStatus simulateStatus(String cardToken) {
        if (cardToken == null || cardToken.isBlank()) {
            return PaymentStatus.FAILED;
        }
        return cardToken.startsWith("fail") ? PaymentStatus.FAILED : PaymentStatus.SUCCEEDED;
    }

    private PaymentDto toDto(Payment payment) {
        return new PaymentDto(
            payment.getId(),
            payment.getBookingId(),
            payment.getAmount(),
            payment.getStatus(),
            payment.getProviderReference(),
            payment.getProcessedAt(),
            payment.getReceiptEmail()
        );
    }
}
