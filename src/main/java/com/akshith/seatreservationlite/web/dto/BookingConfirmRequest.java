package com.akshith.seatreservationlite.web.dto;

import jakarta.validation.constraints.NotBlank;

public class BookingConfirmRequest {
    @NotBlank
    private String holdToken;

    private String customerEmail;

    public BookingConfirmRequest() {
    }

    public String getHoldToken() {
        return holdToken;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setHoldToken(String holdToken) {
        this.holdToken = holdToken;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}