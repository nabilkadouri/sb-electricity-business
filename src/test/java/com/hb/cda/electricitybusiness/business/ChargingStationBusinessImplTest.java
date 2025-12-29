package com.hb.cda.electricitybusiness.business;

import com.hb.cda.electricitybusiness.business.exception.BusinessException;
import com.hb.cda.electricitybusiness.business.impl.ChargingStationBusinessImpl;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.LocationStation;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.ChargingStationRepository;
import com.hb.cda.electricitybusiness.repository.LocationStationRepository;
import com.hb.cda.electricitybusiness.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChargingStationBusinessImplTest {

    @Mock
    private ChargingStationRepository chargingStationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LocationStationRepository locationStationRepository;

    @InjectMocks
    private ChargingStationBusinessImpl chargingStationBusiness;

    // TEST 1 : Création réussie
    @Test
    void createChargingStation_ShouldCreateSuccessfully_WhenUserAndLocationExist() {

        User user = new User();
        user.setId(1L);

        LocationStation location = new LocationStation();
        location.setId(10L);

        ChargingStation station = new ChargingStation();
        station.setUser(user);
        station.setLocationStation(location);

        // Mock des dépendances
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(locationStationRepository.findById(10L)).thenReturn(Optional.of(location));
        when(chargingStationRepository.save(station)).thenReturn(station);

        ChargingStation result = chargingStationBusiness.createChargingStation(station);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(location, result.getLocationStation());
        verify(chargingStationRepository, times(1)).save(station);
    }

    // TEST 2 : User introuvable
    @Test
    void createChargingStation_ShouldThrowException_WhenUserNotFound() {

        User user = new User();
        user.setId(1L);

        LocationStation location = new LocationStation();
        location.setId(10L);

        ChargingStation station = new ChargingStation();
        station.setUser(user);
        station.setLocationStation(location);

        // User non trouvé
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> chargingStationBusiness.createChargingStation(station)
        );

        assertEquals("Utilisateur non trouvé avec l'ID: 1", exception.getMessage());
    }

    // TEST 3 : Location introuvable
    @Test
    void createChargingStation_ShouldThrowException_WhenLocationNotFound() {

        User user = new User();
        user.setId(1L);

        LocationStation location = new LocationStation();
        location.setId(10L);

        ChargingStation station = new ChargingStation();
        station.setUser(user);
        station.setLocationStation(location);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(locationStationRepository.findById(10L)).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> chargingStationBusiness.createChargingStation(station)
        );

        assertEquals("Adresse de la borne non trouvé avec l'ID: 10", exception.getMessage());
    }
}