package com.icinema.booking.client;

import com.icinema.common.api.ApiResponse;
import com.icinema.common.dto.SeatDto;
import com.icinema.booking.client.dto.ConfirmHoldPayload;
import com.icinema.booking.client.dto.SeatHoldPayload;
import com.icinema.booking.client.dto.SeatHoldResult;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "seating-service", url = "${clients.seating.url:http://localhost:8083}")
public interface SeatingClient {

    @GetMapping("/api/seats/show/{showId}")
    ApiResponse<List<SeatDto>> seatsForShow(@PathVariable("showId") Long showId);

    @PostMapping("/api/seats/hold")
    ApiResponse<SeatHoldResult> holdSeats(@RequestBody SeatHoldPayload payload);

    @PostMapping("/api/seats/confirm")
    ApiResponse<String> confirmHold(@RequestBody ConfirmHoldPayload payload);

    @PostMapping("/api/seats/release")
    ApiResponse<String> releaseHold(@RequestBody ConfirmHoldPayload payload);
}
