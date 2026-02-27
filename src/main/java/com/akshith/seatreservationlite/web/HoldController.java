package com.akshith.seatreservationlite.web;

import com.akshith.seatreservationlite.service.HoldService;
import com.akshith.seatreservationlite.web.dto.BookingConfirmRequest;
import com.akshith.seatreservationlite.web.dto.BookingConfirmResponse;
import com.akshith.seatreservationlite.web.dto.HoldCreateRequest;
import com.akshith.seatreservationlite.web.dto.HoldCreateResponse;
import com.akshith.seatreservationlite.web.dto.SeatStatusListResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class HoldController {
    private final HoldService holdService;

    public HoldController(HoldService holdService) {
        this.holdService = holdService;
    }

    @PostMapping("/holds")
    public ResponseEntity<HoldCreateResponse> createHold(@Valid @RequestBody HoldCreateRequest req) {
        HoldCreateResponse resp = holdService.createHold(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/bookings/confirm")
    public ResponseEntity<BookingConfirmResponse> confirmBooking(@Valid @RequestBody BookingConfirmRequest req) {
        BookingConfirmResponse resp = holdService.confirmBooking(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @DeleteMapping("/holds/{holdToken}")
    public ResponseEntity<Void> releaseHold(@PathVariable String holdToken) {
        holdService.releaseHold(holdToken);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/events/{eventId}/seats")
    public ResponseEntity<SeatStatusListResponse> listSeats(@PathVariable String eventId) {
        SeatStatusListResponse resp = holdService.listSeatStatus(eventId);
        return ResponseEntity.ok(resp);
    }
}