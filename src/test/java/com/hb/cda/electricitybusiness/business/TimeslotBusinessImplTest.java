package com.hb.cda.electricitybusiness.business;

import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.business.impl.TimeslotBusinessImpl;
import com.hb.cda.electricitybusiness.controller.dto.TimeslotRequest;
import com.hb.cda.electricitybusiness.controller.dto.TimeslotResponse;
import com.hb.cda.electricitybusiness.controller.dto.mapper.TimeslotMapper;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.Timeslot;
import com.hb.cda.electricitybusiness.repository.ChargingStationRepository;
import com.hb.cda.electricitybusiness.repository.TimeslotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeslotBusinessImplTest {

    @Mock
    private TimeslotRepository timeslotRepository;

    @Mock
    private ChargingStationRepository chargingStationRepository;

    @Mock
    private TimeslotMapper timeslotMapper;

    @InjectMocks
    private TimeslotBusinessImpl timeslotBusiness;

    // TEST 1 : Erreur liste vide → BusinessException
    @Test
    void createMultipleTimeslots_ShouldThrowException_WhenListIsEmpty() {
        List<TimeslotRequest> requests = Collections.emptyList();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> timeslotBusiness.createMultipleTimeslots(requests)
        );

        assertEquals("La liste des créneaux ne peut pas être vide.", exception.getMessage());
    }

    // TEST 2 : Création multiple OK
    @Test
    void createMultipleTimeslots_ShouldSaveTimeslots_WhenDataIsValid() {

        TimeslotRequest request = new TimeslotRequest();
        request.setChargingStationId(10L);
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(12, 0));

        ChargingStation station = new ChargingStation();
        station.setId(10L);

        Timeslot entity = new Timeslot();
        entity.setId(1L);
        entity.setChargingStation(station);

        TimeslotResponse response = new TimeslotResponse();
        response.setId(1L);

        when(chargingStationRepository.findById(10L)).thenReturn(Optional.of(station));
        when(timeslotMapper.convertToEntity(any())).thenReturn(entity);
        when(timeslotRepository.saveAll(any())).thenReturn(List.of(entity));
        when(timeslotMapper.toResponseList(any())).thenReturn(List.of(response));

        List<TimeslotResponse> result = timeslotBusiness.createMultipleTimeslots(List.of(request));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(timeslotRepository, times(1)).saveAll(any());
    }

    // TEST 3 : createTimeslot → Charge station non trouvée
    @Test
    void createTimeslot_ShouldThrowException_WhenChargingStationNotFound() {

        TimeslotRequest request = new TimeslotRequest();
        request.setChargingStationId(99L);

        when(chargingStationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> timeslotBusiness.createTimeslot(request));
    }

    // TEST 4 : createTimeslot → Création correcte
    @Test
    void createTimeslot_ShouldSaveAndReturnResponse_WhenValid() {

        TimeslotRequest request = new TimeslotRequest();
        request.setChargingStationId(10L);

        ChargingStation station = new ChargingStation();
        station.setId(10L);

        Timeslot entity = new Timeslot();
        entity.setId(3L);

        TimeslotResponse response = new TimeslotResponse();
        response.setId(3L);

        when(chargingStationRepository.findById(10L)).thenReturn(Optional.of(station));
        when(timeslotMapper.convertToEntity(request)).thenReturn(entity);
        when(timeslotRepository.save(entity)).thenReturn(entity);
        when(timeslotMapper.toResponse(entity)).thenReturn(response);

        TimeslotResponse result = timeslotBusiness.createTimeslot(request);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        verify(timeslotRepository, times(1)).save(entity);
    }
}
