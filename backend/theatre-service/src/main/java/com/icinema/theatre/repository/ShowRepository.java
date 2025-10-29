package com.icinema.theatre.repository;

import com.icinema.theatre.domain.Show;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByTheatreId(Long theatreId);
    List<Show> findByMovieId(Long movieId);
    List<Show> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
