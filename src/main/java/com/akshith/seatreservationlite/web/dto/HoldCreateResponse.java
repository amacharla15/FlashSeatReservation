package com.akshith.seatreservationlite.web.dto;

import java.time.OffsetDateTime;

public class HoldCreateResponse {
    private String holdToken;
    private String eventId;
    private String seatNo;
    private String status;
    private OffsetDateTime expiresAt;

    public HoldCreateResponse() {
    }

    public HoldCreateResponse(String holdToken, String eventId, String seatNo, String status, OffsetDateTime expiresAt) {
        this.holdToken = holdToken;
        this.eventId = eventId;
        this.seatNo = seatNo;
        this.status = status;
        this.expiresAt = expiresAt;
    }

    public String getHoldToken() {
        return holdToken;
    }

    public String getEventId() {
        return eventId;
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

    public void setHoldToken(String holdToken) {
        this.holdToken = holdToken;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
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