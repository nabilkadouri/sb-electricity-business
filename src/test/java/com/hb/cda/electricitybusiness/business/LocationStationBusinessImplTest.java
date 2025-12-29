package com.hb.cda.electricitybusiness.business;

import com.hb.cda.electricitybusiness.business.impl.LocationStationImpl;
import com.hb.cda.electricitybusiness.model.LocationStation;
import com.hb.cda.electricitybusiness.repository.LocationStationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationStationBusinessImplTest {

    @Mock
    private LocationStationRepository locationStationRepository;

    @InjectMocks
    private LocationStationImpl locationStationBusiness;

    // TEST 1 : Création réussie
    @Test
    void createLocationStation_ShouldSaveAndReturnEntity() {

        LocationStation location = new LocationStation();
        location.setId(5L);
        location.setAddress("10 rue de Paris");
        location.setCity("Paris");
        location.setPostaleCode("75000");

        when(locationStationRepository.save(location)).thenReturn(location);

        LocationStation result = locationStationBusiness.createLocationStation(location);

        assertNotNull(result);
        assertEquals("Paris", result.getCity());
        verify(locationStationRepository, times(1)).save(location);
    }

    // TEST 2 : Vérification de la propagation d’erreur
    @Test
    void createLocationStation_ShouldThrowException_WhenSaveFails() {

        LocationStation location = new LocationStation();

        when(locationStationRepository.save(location))
                .thenThrow(new RuntimeException("Erreur DB"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> locationStationBusiness.createLocationStation(location)
        );

        assertEquals("Erreur DB", exception.getMessage());
    }
}
