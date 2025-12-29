package com.hb.cda.electricitybusiness.controller;

import com.hb.cda.electricitybusiness.business.BookingBusiness;
import com.hb.cda.electricitybusiness.controller.dto.BookingRequest;
import com.hb.cda.electricitybusiness.controller.dto.BookingResponse;
import com.hb.cda.electricitybusiness.business.impl.BookingBusinessImpl;
import com.hb.cda.electricitybusiness.controller.dto.BookingStatusUpdateRequest;
import com.hb.cda.electricitybusiness.controller.dto.mapper.BookingMapper;
import com.hb.cda.electricitybusiness.model.Booking;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/bookings")
public class BookingController {

    private final BookingBusiness bookingBusiness;
    private final BookingMapper bookingMapper;
    private BookingBusinessImpl bookingService;

    public BookingController(BookingBusinessImpl bookingService, BookingBusiness bookingBusiness, BookingMapper bookingMapper) {
        this.bookingService = bookingService;
        this.bookingBusiness = bookingBusiness;
        this.bookingMapper = bookingMapper;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return new ResponseEntity<>(booking, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserId(@PathVariable Long userId) {
        List<BookingResponse> bookings = bookingService.getAllBookingByUserId(userId);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingResponse newBooking = bookingService.createBooking(request);
        return new ResponseEntity<>(newBooking, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody BookingStatusUpdateRequest request
    ) {
        Booking updatedBooking = bookingBusiness.updateBookingStatus(id, request);
        BookingResponse response = bookingMapper.ToResponse(updatedBooking);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
