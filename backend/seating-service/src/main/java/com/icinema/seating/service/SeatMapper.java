package com.icinema.seating.service;

import com.icinema.common.dto.SeatDto;
import com.icinema.seating.domain.Seat;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    public SeatDto toDto(Seat seat) {
        if (seat == null) {
            return null;
        }
        return new SeatDto(
            seat.getId(),
            seat.getShowId(),
            seat.getSeatNumber(),
            seat.getStatus(),
            seat.getPrice()
        );
    }
}
