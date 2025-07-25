package com.hb.cda.electricitybusiness.business;

import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.dto.BookingRequest;
import com.hb.cda.electricitybusiness.dto.BookingResponse;

import java.util.List;


public interface BookingBusiness {

    BookingResponse createBooking(BookingRequest bookingRequest) throws BusinessException;

    BookingResponse getBookingById(Long Id) throws  BusinessException;

    List<BookingResponse> getAllBookings() throws BusinessException;

    List<BookingResponse> getAllBookingByUserId(Long userId) throws BusinessException;

    List<BookingResponse> getAllBookingByChargingStationId(Long chargingStationId) throws BusinessException;

    void deleteBooking(Long id) throws BusinessException;
}

