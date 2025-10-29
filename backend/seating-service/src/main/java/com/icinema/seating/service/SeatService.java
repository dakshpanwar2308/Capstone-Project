package com.icinema.seating.service;

import com.icinema.common.dto.SeatDto;
import com.icinema.common.model.SeatStatus;
import com.icinema.seating.domain.Seat;
import com.icinema.seating.repository.SeatRepository;
import com.icinema.seating.web.dto.SeatHoldRequest;
import com.icinema.seating.web.dto.SeatHoldResponse;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class SeatService {

    private static final int DEFAULT_HOLD_SECONDS = 300;

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;

    public SeatService(SeatRepository seatRepository, SeatMapper seatMapper) {
        this.seatRepository = seatRepository;
        this.seatMapper = seatMapper;
    }

    @Transactional
    public List<SeatDto> seatsForShow(Long showId) {
        releaseExpired(showId);
        return seatRepository.findByShowIdOrderBySeatNumberAsc(showId).stream()
            .map(seatMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public SeatHoldResponse holdSeats(SeatHoldRequest request) {
        releaseExpired(request.showId());

        List<Seat> seats = request.seatNumbers().stream()
            .map(seatNumber -> seatRepository.findByShowIdAndSeatNumber(request.showId(), seatNumber)
                .orElseThrow(() -> new IllegalArgumentException("Seat not found: " + seatNumber)))
            .toList();

        boolean allAvailable = seats.stream()
            .allMatch(seat -> seat.getStatus() == SeatStatus.AVAILABLE);

        if (!allAvailable) {
            throw new IllegalArgumentException("One or more seats are not available");
        }

        String holdToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
            request.holdSeconds() <= 0 ? DEFAULT_HOLD_SECONDS : request.holdSeconds()
        );

        seats.forEach(seat -> {
            seat.setStatus(SeatStatus.HELD);
            seat.setHoldToken(holdToken);
            seat.setHoldExpiresAt(expiresAt);
        });

        var seatDtos = seats.stream().map(seatMapper::toDto).toList();
        return new SeatHoldResponse(holdToken, expiresAt, seatDtos);
    }

    @Transactional
    public void confirmHold(Long showId, String holdToken) {
        List<Seat> seats = seatRepository.findByShowIdAndHoldToken(showId, holdToken);
        if (seats.isEmpty()) {
            throw new IllegalArgumentException("No seats held for token");
        }
        seats.forEach(seat -> {
            seat.setStatus(SeatStatus.BOOKED);
            seat.setHoldToken(null);
            seat.setHoldExpiresAt(null);
        });
    }

    @Transactional
    public void releaseHold(Long showId, String holdToken) {
        List<Seat> seats = seatRepository.findByShowIdAndHoldToken(showId, holdToken);
        seats.forEach(seat -> {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setHoldToken(null);
            seat.setHoldExpiresAt(null);
        });
    }

    private void releaseExpired(Long showId) {
        seatRepository.releaseExpiredHolds(showId, LocalDateTime.now());
    }
}
