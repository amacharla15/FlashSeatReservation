package com.akshith.seatreservationlite.repo;

import com.akshith.seatreservationlite.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    boolean existsByEventIdAndSeatIdAndStatus(String eventId, Long seatId, String status);
    Optional<ReservationEntity> findFirstByEventIdAndSeatIdAndStatus(String eventId, Long seatId, String status);
    Optional<ReservationEntity> findByHoldToken(String holdToken);

}