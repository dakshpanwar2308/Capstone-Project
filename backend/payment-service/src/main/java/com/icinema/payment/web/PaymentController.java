package com.icinema.payment.web;

import com.icinema.common.api.ApiResponse;
import com.icinema.common.dto.PaymentDto;
import com.icinema.common.dto.PaymentRequest;
import com.icinema.payment.service.PaymentProcessor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentProcessor paymentProcessor;

    public PaymentController(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<PaymentDto>> charge(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(paymentProcessor.charge(request)));
    }

    @PostMapping("/refund")
    public ResponseEntity<ApiResponse<PaymentDto>> refund(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(paymentProcessor.refund(request)));
    }
}
