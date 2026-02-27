package com.akshith.seatreservationlite.web.dto;

import java.time.OffsetDateTime;

public class BookingConfirmResponse {
    private String bookingId;
    private String eventId;
    private String seatNo;
    private String status;
    private OffsetDateTime bookedAt;

    public BookingConfirmResponse() {
    }

    public BookingConfirmResponse(String bookingId, String eventId, String seatNo, String status, OffsetDateTime bookedAt) {
        this.bookingId = bookingId;
        this.eventId = eventId;
        this.seatNo = seatNo;
        this.status = status;
        this.bookedAt = bookedAt;
    }

    public String getBookingId() {
        return bookingId;
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

    public OffsetDateTime getBookedAt() {
        return bookedAt;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
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

    public void setBookedAt(OffsetDateTime bookedAt) {
        this.bookedAt = bookedAt;
    }
}