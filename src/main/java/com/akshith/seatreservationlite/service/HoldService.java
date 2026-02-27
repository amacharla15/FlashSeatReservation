package com.akshith.seatreservationlite.service;

import com.akshith.seatreservationlite.entity.ReservationEntity;
import com.akshith.seatreservationlite.entity.SeatEntity;
import com.akshith.seatreservationlite.repo.EventRepository;
import com.akshith.seatreservationlite.repo.ReservationRepository;
import com.akshith.seatreservationlite.repo.SeatRepository;
import com.akshith.seatreservationlite.web.dto.BookingConfirmRequest;
import com.akshith.seatreservationlite.web.dto.BookingConfirmResponse;
import com.akshith.seatreservationlite.web.dto.HoldCreateRequest;
import com.akshith.seatreservationlite.web.dto.HoldCreateResponse;
import com.akshith.seatreservationlite.web.dto.SeatStatus;
import com.akshith.seatreservationlite.web.dto.SeatStatusListResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
public class HoldService {
    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public HoldService(EventRepository eventRepository, SeatRepository seatRepository, ReservationRepository reservationRepository) {
        this.eventRepository = eventRepository;
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public HoldCreateResponse createHold(HoldCreateRequest req) {
        String eventId = req.getEventId();
        String seatNo = req.getSeatNo();

        if (!eventRepository.existsById(eventId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }

        SeatEntity seat = seatRepository.findByEventIdAndSeatNo(eventId, seatNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

        Long seatId = seat.getId();

        if (reservationRepository.existsByEventIdAndSeatIdAndStatus(eventId, seatId, "BOOKED")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat already booked");
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        Optional<ReservationEntity> existingHeldOpt = reservationRepository.findFirstByEventIdAndSeatIdAndStatus(eventId, seatId, "HELD");
        if (existingHeldOpt.isPresent()) {
            ReservationEntity existingHeld = existingHeldOpt.get();
            OffsetDateTime expiresAt = existingHeld.getExpiresAt();
            if (expiresAt != null && expiresAt.isAfter(now)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat already held");
            }
            reservationRepository.delete(existingHeld);
            reservationRepository.flush();
        }

        int ttl = req.getTtlSeconds() == null ? 120 : req.getTtlSeconds();
        OffsetDateTime expiresAt = now.plusSeconds(ttl);
        String token = UUID.randomUUID().toString();

        ReservationEntity r = new ReservationEntity();
        r.setEventId(eventId);
        r.setSeatId(seatId);
        r.setStatus("HELD");
        r.setHoldToken(token);
        r.setExpiresAt(expiresAt);
        r.setBookedAt(null);

        try {
            reservationRepository.saveAndFlush(r);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat not available");
        }

        return new HoldCreateResponse(token, eventId, seatNo, "HELD", expiresAt);
    }

    @Transactional
    public BookingConfirmResponse confirmBooking(BookingConfirmRequest req) {
        String token = req.getHoldToken();

        ReservationEntity r = reservationRepository.findByHoldToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hold token not found"));

        if (!"HELD".equals(r.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hold already confirmed or invalid");
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        if (r.getExpiresAt() == null || !r.getExpiresAt().isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.GONE, "Hold expired");
        }

        r.setStatus("BOOKED");
        r.setBookedAt(now);
        r.setHoldToken(null);
        r.setExpiresAt(null);

        reservationRepository.saveAndFlush(r);

        String bookingId = String.valueOf(r.getId());

        String eventId = r.getEventId();
        Long seatId = r.getSeatId();
        SeatEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seat not found"));

        return new BookingConfirmResponse(bookingId, eventId, seat.getSeatNo(), "BOOKED", now);
    }

    @Transactional
    public void releaseHold(String holdToken) {
        ReservationEntity r = reservationRepository.findByHoldToken(holdToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hold token not found"));

        if (!"HELD".equals(r.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Hold already confirmed or invalid");
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        if (r.getExpiresAt() == null || !r.getExpiresAt().isAfter(now)) {
            throw new ResponseStatusException(HttpStatus.GONE, "Hold expired");
        }

        reservationRepository.delete(r);
        reservationRepository.flush();
    }
    @Transactional(readOnly = true)
    public SeatStatusListResponse listSeatStatus(String eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
        }

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        java.util.List<SeatEntity> seats = seatRepository.findByEventIdOrderBySeatNoAsc(eventId);
        java.util.List<com.akshith.seatreservationlite.web.dto.SeatStatus> out = new java.util.ArrayList<>();

        for (int i = 0; i < seats.size(); i++) {
            SeatEntity s = seats.get(i);

            boolean booked = reservationRepository.existsByEventIdAndSeatIdAndStatus(eventId, s.getId(), "BOOKED");
            if (booked) {
                out.add(new com.akshith.seatreservationlite.web.dto.SeatStatus(s.getSeatNo(), "BOOKED", null));
                continue;
            }

            java.util.Optional<ReservationEntity> heldOpt = reservationRepository.findFirstByEventIdAndSeatIdAndStatus(eventId, s.getId(), "HELD");
            if (heldOpt.isPresent()) {
                ReservationEntity held = heldOpt.get();
                OffsetDateTime exp = held.getExpiresAt();
                if (exp != null && exp.isAfter(now)) {
                    out.add(new com.akshith.seatreservationlite.web.dto.SeatStatus(s.getSeatNo(), "HELD", exp));
                    continue;
                }
            }

            out.add(new com.akshith.seatreservationlite.web.dto.SeatStatus(s.getSeatNo(), "AVAILABLE", null));
        }

        return new com.akshith.seatreservationlite.web.dto.SeatStatusListResponse(eventId, out);
    }
}