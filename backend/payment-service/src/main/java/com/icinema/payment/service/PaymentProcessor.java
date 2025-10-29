package com.icinema.payment.service;

import com.icinema.common.dto.PaymentDto;
import com.icinema.common.dto.PaymentRequest;
import com.icinema.common.model.CardType;
import com.icinema.common.model.PaymentStatus;
import com.icinema.payment.domain.Payment;
import com.icinema.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PaymentProcessor {

    private static final DateTimeFormatter EXPIRY_FORMAT = DateTimeFormatter.ofPattern("MM/yy");

    private final PaymentRepository paymentRepository;

    public PaymentProcessor(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public PaymentDto charge(PaymentRequest request) {
        validateCardDetails(request);
        PaymentStatus status = simulateStatus(request.cardType(), request.cardNumber());
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

    private void validateCardDetails(PaymentRequest request) {
        if (request.cardNumber() == null || !request.cardNumber().matches("\\d{16}")) {
            throw new IllegalArgumentException("Card number must be 16 digits.");
        }
        if (request.cvv() == null || !request.cvv().matches("\\d{3}")) {
            throw new IllegalArgumentException("CVV must be 3 digits.");
        }
        try {
            YearMonth expiry = YearMonth.parse(request.expiry(), EXPIRY_FORMAT);
            if (expiry.isBefore(YearMonth.now())) {
                throw new IllegalArgumentException("Card has expired.");
            }
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Invalid expiry format. Use MM/YY.");
        }
    }

    private PaymentStatus simulateStatus(CardType cardType, String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) {
            return PaymentStatus.FAILED;
        }
        if (cardType == CardType.CREDIT && cardNumber.endsWith("0")) {
            return PaymentStatus.FAILED;
        }
        if (cardType == CardType.DEBIT && cardNumber.endsWith("9")) {
            return PaymentStatus.FAILED;
        }
        return PaymentStatus.SUCCEEDED;
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
