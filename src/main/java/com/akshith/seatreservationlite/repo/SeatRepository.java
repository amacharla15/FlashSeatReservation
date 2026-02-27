package com.akshith.seatreservationlite.repo;

import com.akshith.seatreservationlite.entity.SeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    Optional<SeatEntity> findByEventIdAndSeatNo(String eventId, String seatNo);
    java.util.List<SeatEntity> findByEventIdOrderBySeatNoAsc(String eventId);
}