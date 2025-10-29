package com.icinema.seating.repository;

import com.icinema.common.model.SeatStatus;
import com.icinema.seating.domain.Seat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowIdOrderBySeatNumberAsc(Long showId);

    Optional<Seat> findByShowIdAndSeatNumber(Long showId, String seatNumber);

    List<Seat> findByShowIdAndStatus(Long showId, SeatStatus status);

    List<Seat> findByShowIdAndHoldToken(Long showId, String holdToken);

    @Modifying
    @Query("update Seat s set s.status = 'AVAILABLE', s.holdToken = null, s.holdExpiresAt = null where s.showId = :showId and s.holdExpiresAt < :now and s.status = 'HELD'")
    int releaseExpiredHolds(@Param("showId") Long showId, @Param("now") LocalDateTime now);
}
