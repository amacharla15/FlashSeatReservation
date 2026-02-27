package com.akshith.seatreservationlite.repo;

import com.akshith.seatreservationlite.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<EventEntity, String> {
}