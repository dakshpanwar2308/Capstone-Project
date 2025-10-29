package com.icinema.theatre.repository;

import com.icinema.theatre.domain.Theatre;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    List<Theatre> findByCityIgnoreCase(String city);
}
