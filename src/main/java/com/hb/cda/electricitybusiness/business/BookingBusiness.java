package com.hb.cda.electricitybusiness.business;

import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.controller.dto.BookingRequest;
import com.hb.cda.electricitybusiness.controller.dto.BookingResponse;
import com.hb.cda.electricitybusiness.controller.dto.BookingStatusUpdateRequest;
import com.hb.cda.electricitybusiness.model.Booking;

import java.util.List;


public interface BookingBusiness {

    BookingResponse createBooking(BookingRequest bookingRequest) throws BusinessException;

    BookingResponse getBookingById(Long Id) throws  BusinessException;

    Booking updateBookingStatus(Long bookingId, BookingStatusUpdateRequest request) throws BusinessException;

    List<BookingResponse> getAllBookings() throws BusinessException;

    List<BookingResponse> getAllBookingByUserId(Long userId) throws BusinessException;

    List<BookingResponse> getAllBookingByChargingStationId(Long chargingStationId) throws BusinessException;

    void deleteBooking(Long id) throws BusinessException;
}

