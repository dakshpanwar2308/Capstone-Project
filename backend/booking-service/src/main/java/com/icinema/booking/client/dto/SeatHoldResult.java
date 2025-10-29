package com.icinema.booking.client.dto;

import com.icinema.common.dto.SeatDto;
import java.time.LocalDateTime;
import java.util.List;

public record SeatHoldResult(String holdToken, LocalDateTime expiresAt, List<SeatDto> seats) {
}
