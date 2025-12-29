package com.hb.cda.electricitybusiness.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hb.cda.electricitybusiness.controller.dto.ChargingStationRequest;
import com.hb.cda.electricitybusiness.controller.dto.PictureDetailsDTO;
import com.hb.cda.electricitybusiness.enums.ChargingStationStatus;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.LocationStation;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.*;
import com.hb.cda.electricitybusiness.testutil.TestDataFactory;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.mail.host=dummy",
        "spring.mail.port=1025",
        "jwt.secret=test-secret",
        "jwt.expiration=3600000",
        "jwt.refresh.expiration=7200000"
})
class ChargingStationControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private ChargingStationRepository chargingStationRepository;
    @Autowired private LocationStationRepository locationStationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private TimeslotRepository timeslotRepository;

    @Autowired private EntityManager entityManager;

    private LocationStation savedLocation;
    private User savedUser;

    @BeforeEach
    void setup() {

        bookingRepository.deleteAll();
        timeslotRepository.deleteAll();
        chargingStationRepository.deleteAll();
        locationStationRepository.deleteAll();
        userRepository.deleteAll();

        // Nettoyage du contexte Hibernate
        entityManager.flush();
        entityManager.clear();

        savedUser = userRepository.save(TestDataFactory.createUser());
        savedLocation = locationStationRepository.save(TestDataFactory.createLocation());
    }


    //-------------------------------------------------------------------------

    @Test
    @Transactional
    void getAllChargingStations_ShouldReturn200() throws Exception {

        chargingStationRepository.save(
                TestDataFactory.createChargingStation(savedUser, savedLocation)
        );

        mockMvc.perform(get("/api/charging_stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nameStation").exists());
    }

    //-------------------------------------------------------------------------

    @Test
    @Transactional
    void getChargingStationById_ShouldReturn200() throws Exception {

        ChargingStation station = chargingStationRepository
                .save(TestDataFactory.createChargingStation(savedUser, savedLocation));

        mockMvc.perform(get("/api/charging_stations/" + station.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(station.getId()))
                .andExpect(jsonPath("$.nameStation").value(station.getNameStation()));
    }

    //-------------------------------------------------------------------------

    @Test
    @Transactional
    void createChargingStation_ShouldReturn201() throws Exception {

        ChargingStationRequest request = new ChargingStationRequest(
                "Station Test",
                "Une bonne borne",
                BigDecimal.valueOf(22),
                BigDecimal.valueOf(5),
                new PictureDetailsDTO("ddfdffd", "jddjdk",true),
                ChargingStationStatus.PENDING,

                true,
                savedLocation.getId(),
                savedUser.getId()
        );

        mockMvc.perform(post("/api/charging_stations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nameStation").value("Station Test"))
                .andExpect(jsonPath("$.status").value("En attente"))
                .andExpect(jsonPath("$.locationStation.id").value(savedLocation.getId()));

        assertThat(chargingStationRepository.count()).isEqualTo(1);
    }

    //-------------------------------------------------------------------------

    @Test
    @Transactional
    void updateChargingStation_ShouldReturn200() throws Exception {

        ChargingStation station =
                chargingStationRepository.save(TestDataFactory.createChargingStation(savedUser, savedLocation));

        ChargingStationRequest request = new ChargingStationRequest(
                "Station Modifiée",
                "Description update",
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(15),
                new PictureDetailsDTO("ddfdffd", "jddjdk",true),
                ChargingStationStatus.CONFIRMED,
                false,
                savedLocation.getId(),
                savedUser.getId()
        );

        mockMvc.perform(put("/api/charging_stations/" + station.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameStation").value("Station Modifiée"))
                .andExpect(jsonPath("$.isAvailable").value(false))
                .andExpect(jsonPath("$.status").value("Confirmée"));
    }

    //-------------------------------------------------------------------------

    @Test
    void deleteChargingStation_ShouldReturn204() throws Exception {

        ChargingStation station =
                chargingStationRepository.save(TestDataFactory.createChargingStation(savedUser, savedLocation));

        mockMvc.perform(delete("/api/charging_stations/" + station.getId()))
                .andExpect(status().isNoContent());

        long count = chargingStationRepository.count();
        System.out.println("COUNT after delete = " + count);

        assertThat(count).isZero();
    }
}