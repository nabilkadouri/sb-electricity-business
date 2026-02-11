package com.hb.cda.electricitybusiness.business;

import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.business.impl.BookingBusinessImpl;
import com.hb.cda.electricitybusiness.controller.dto.BookingRequest;
import com.hb.cda.electricitybusiness.controller.dto.BookingResponse;
import com.hb.cda.electricitybusiness.controller.dto.mapper.BookingMapper;
import com.hb.cda.electricitybusiness.enums.DayOfWeek;
import com.hb.cda.electricitybusiness.messaging.MailService;
import com.hb.cda.electricitybusiness.model.Booking;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.Timeslot;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.BookingRepository;
import com.hb.cda.electricitybusiness.repository.ChargingStationRepository;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList; // Utiliser une liste modifiable
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingBusinessImplTest {

    @Mock private UserRepository userRepository;
    @Mock private ChargingStationRepository chargingStationRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private BookingMapper bookingMapper;
    @Mock private MailService mailService;

    @InjectMocks
    private BookingBusinessImpl bookingBusiness;

    private User user;
    private ChargingStation station;
    private BookingRequest request;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        station = new ChargingStation();
        station.setId(10L);
        station.setIsAvailable(true);
        station.setPricePerHour(BigDecimal.valueOf(5));
        station.setNameStation("Borne Test");

        Timeslot timeslot = new Timeslot();
        timeslot.setDayOfWeek(DayOfWeek.MONDAY);
        timeslot.setStartTime(LocalTime.of(0, 0));
        timeslot.setEndTime(LocalTime.of(23, 59));

        station.setTimeslots(List.of(timeslot));
        station.setBookings(new ArrayList<>());

        request = new BookingRequest();
        request.setUserId(1L);
        request.setChargingStationId(10L);
        request.setStartDate(LocalDateTime.of(2025, 1, 20, 10, 0)); // Un lundi
        request.setEndDate(LocalDateTime.of(2025, 1, 20, 12, 0));
    }

    @Test
    void createBooking_ShouldCreate_WhenDataIsValid() {
        Booking entity = new Booking();
        entity.setUser(user);
        entity.setChargingStation(station);
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());

        BookingResponse expectedResponse = new BookingResponse();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(chargingStationRepository.findById(10L)).thenReturn(Optional.of(station));
        when(bookingMapper.convertToEntity(any())).thenReturn(entity);
        when(bookingRepository.save(any())).thenReturn(entity);
        when(bookingMapper.ToResponse(any())).thenReturn(expectedResponse);

        BookingResponse response = bookingBusiness.createBooking(request);

        assertNotNull(response);
        verify(bookingRepository, times(1)).save(any());
        verify(mailService, atLeastOnce()).sendReservationCreatedEmail(any(), any(), any(), any(), any());
    }

    @Test
    void createBooking_ShouldThrow_WhenOverlappingExists() {
        Booking existingBooking = new Booking();
        existingBooking.setStartDate(LocalDateTime.of(2025, 1, 20, 11, 0));
        existingBooking.setEndDate(LocalDateTime.of(2025, 1, 20, 13, 0));

        station.setBookings(List.of(existingBooking));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(chargingStationRepository.findById(10L)).thenReturn(Optional.of(station));

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            bookingBusiness.createBooking(request);
        });

        assertTrue(ex.getMessage().contains("déjà réservé"));
    }
}