package com.icinema.booking.service;

import com.icinema.booking.domain.Booking;
import com.icinema.common.dto.BookingDto;
import com.icinema.common.dto.PaymentDto;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingDto toDto(Booking booking, PaymentDto payment) {
        if (booking == null) {
            return null;
        }
        List<String> seats = booking.getSeatNumbers() == null
            ? List.of()
            : List.copyOf(booking.getSeatNumbers());
        return new BookingDto(
            booking.getId(),
            booking.getShowId(),
            booking.getCustomerName(),
            booking.getCustomerEmail(),
            booking.getCreatedAt(),
            booking.getTotalAmount(),
            booking.getStatus(),
            seats,
            payment
        );
    }
}
