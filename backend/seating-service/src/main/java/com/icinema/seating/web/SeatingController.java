package com.icinema.seating.web;

import com.icinema.common.api.ApiResponse;
import com.icinema.common.dto.SeatDto;
import com.icinema.seating.service.SeatService;
import com.icinema.seating.web.dto.ConfirmSeatHoldRequest;
import com.icinema.seating.web.dto.SeatHoldRequest;
import com.icinema.seating.web.dto.SeatHoldResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seats")
public class SeatingController {

    private final SeatService seatService;

    public SeatingController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/show/{showId}")
    public ResponseEntity<ApiResponse<List<SeatDto>>> seats(@PathVariable Long showId) {
        return ResponseEntity.ok(ApiResponse.ok(seatService.seatsForShow(showId)));
    }

    @PostMapping("/hold")
    public ResponseEntity<ApiResponse<SeatHoldResponse>> hold(@Valid @RequestBody SeatHoldRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(seatService.holdSeats(request)));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<String>> confirm(@Valid @RequestBody ConfirmSeatHoldRequest request) {
        seatService.confirmHold(request.showId(), request.holdToken());
        return ResponseEntity.ok(ApiResponse.ok("Seats confirmed"));
    }

    @PostMapping("/release")
    public ResponseEntity<ApiResponse<String>> release(@Valid @RequestBody ConfirmSeatHoldRequest request) {
        seatService.releaseHold(request.showId(), request.holdToken());
        return ResponseEntity.ok(ApiResponse.ok("Seats released"));
    }
}
