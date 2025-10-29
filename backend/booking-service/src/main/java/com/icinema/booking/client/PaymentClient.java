package com.icinema.booking.client;

import com.icinema.common.api.ApiResponse;
import com.icinema.common.dto.PaymentDto;
import com.icinema.common.dto.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${clients.payment.url:http://localhost:8085}")
public interface PaymentClient {

    @PostMapping("/api/payments/charge")
    ApiResponse<PaymentDto> charge(@RequestBody PaymentRequest request);

    @PostMapping("/api/payments/refund")
    ApiResponse<PaymentDto> refund(@RequestBody PaymentRequest request);
}
