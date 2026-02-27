package com.akshith.seatreservationlite.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class HoldCreateRequest {
    @NotBlank
    private String eventId;

    @NotBlank
    private String seatNo;

    @Min(15)
    @Max(600)
    private Integer ttlSeconds;

    public HoldCreateRequest() {
    }

    public String getEventId() {
        return eventId;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public Integer getTtlSeconds() {
        return ttlSeconds;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public void setTtlSeconds(Integer ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }
}