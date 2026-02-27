package com.akshith.seatreservationlite.web.dto;

import java.time.OffsetDateTime;

public class SeatStatus {
    private String seatNo;
    private String status;
    private OffsetDateTime expiresAt;

    public SeatStatus() {
    }

    public SeatStatus(String seatNo, String status, OffsetDateTime expiresAt) {
        this.seatNo = seatNo;
        this.status = status;
        this.expiresAt = expiresAt;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}