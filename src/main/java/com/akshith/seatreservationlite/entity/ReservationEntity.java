package com.akshith.seatreservationlite.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "reservations")
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "seat_id", nullable = false)
    private Long seatId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "hold_token")
    private String holdToken;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "booked_at")
    private OffsetDateTime bookedAt;

    public ReservationEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public String getStatus() {
        return status;
    }

    public String getHoldToken() {
        return holdToken;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public OffsetDateTime getBookedAt() {
        return bookedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setHoldToken(String holdToken) {
        this.holdToken = holdToken;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setBookedAt(OffsetDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }
}