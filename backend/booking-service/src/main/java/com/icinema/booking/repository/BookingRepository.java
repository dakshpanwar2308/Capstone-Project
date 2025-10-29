package com.icinema.booking.repository;

import com.icinema.booking.domain.Booking;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerEmailOrderByCreatedAtDesc(String customerEmail);
}
