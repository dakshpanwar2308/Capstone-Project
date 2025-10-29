package com.icinema.seating.web.dto;

import com.icinema.common.dto.SeatDto;
import java.time.LocalDateTime;
import java.util.List;

public record SeatHoldResponse(
    String holdToken,
    LocalDateTime expiresAt,
    List<SeatDto> seats
) {
}
