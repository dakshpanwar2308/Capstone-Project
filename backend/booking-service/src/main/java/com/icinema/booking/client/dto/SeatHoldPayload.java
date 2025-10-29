package com.icinema.booking.client.dto;

import java.util.List;

public record SeatHoldPayload(Long showId, List<String> seatNumbers, int holdSeconds) {
}
