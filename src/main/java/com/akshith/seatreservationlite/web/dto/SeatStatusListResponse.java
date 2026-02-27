package com.akshith.seatreservationlite.web.dto;

import java.util.List;

public class SeatStatusListResponse {
    private String eventId;
    private List<SeatStatus> seats;

    public SeatStatusListResponse() {
    }

    public SeatStatusListResponse(String eventId, List<SeatStatus> seats) {
        this.eventId = eventId;
        this.seats = seats;
    }

    public String getEventId() {
        return eventId;
    }

    public List<SeatStatus> getSeats() {
        return seats;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setSeats(List<SeatStatus> seats) {
        this.seats = seats;
    }
}