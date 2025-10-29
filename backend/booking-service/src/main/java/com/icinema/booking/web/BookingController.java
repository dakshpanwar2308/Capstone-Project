package com.icinema.booking.web;

import com.icinema.booking.service.BookingService;
import com.icinema.booking.web.dto.BookingRequest;
import com.icinema.common.api.ApiResponse;
import com.icinema.common.dto.BookingDto;
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
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingDto>> create(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.ok(ApiResponse.created(bookingService.createBooking(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingDto>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getBooking(id)));
    }

    @GetMapping("/customer/{email}")
    public ResponseEntity<ApiResponse<List<BookingDto>>> byCustomer(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.bookingsForCustomer(email)));
    }
}
