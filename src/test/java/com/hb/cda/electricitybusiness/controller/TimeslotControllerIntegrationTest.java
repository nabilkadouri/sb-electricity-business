package com.hb.cda.electricitybusiness.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hb.cda.electricitybusiness.controller.dto.TimeslotRequest;
import com.hb.cda.electricitybusiness.enums.DayOfWeek;
import com.hb.cda.electricitybusiness.model.ChargingStation;
import com.hb.cda.electricitybusiness.model.LocationStation;
import com.hb.cda.electricitybusiness.model.Timeslot;
import com.hb.cda.electricitybusiness.model.User;
import com.hb.cda.electricitybusiness.repository.*;
import com.hb.cda.electricitybusiness.testutil.TestDataFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class TimeslotControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Autowired UserRepository userRepository;
    @Autowired LocationStationRepository locationStationRepository;
    @Autowired ChargingStationRepository chargingStationRepository;
    @Autowired TimeslotRepository timeslotRepository;

    @Autowired EntityManager entityManager;

    private User savedUser;
    private LocationStation savedLocation;
    private ChargingStation savedStation;

    @BeforeEach
    void setup() {
        System.out.println("🔥 CONTEXTE CHARGÉ, SETUP LANCÉ");

        try {
            System.out.println("➡️ SETUP START");
        } catch (Exception e) {
            e.printStackTrace();
        }
        timeslotRepository.deleteAll();
        chargingStationRepository.deleteAll();
        locationStationRepository.deleteAll();
        userRepository.deleteAll();

        savedUser = userRepository.save(TestDataFactory.createUser());
        savedLocation = locationStationRepository.save(TestDataFactory.createLocation());
        savedStation = chargingStationRepository.save(
                TestDataFactory.createChargingStation(savedUser, savedLocation)
        );
        System.out.println("✔️ SETUP FINI AVEC SUCCÈS");
    }

    // ----------------------------------------------------------------------

    @Test
    void getAllTimeslots_ShouldReturn200() throws Exception {
        System.out.println("💥 TEST LANCÉ !");
        Timeslot t = new Timeslot();
        t.setDayOfWeek(DayOfWeek.MONDAY);
        t.setStartTime(LocalTime.of(8, 0));
        t.setEndTime(LocalTime.of(9, 0));
        t.setChargingStation(savedStation);
        timeslotRepository.save(t);

        mockMvc.perform(get("/api/timeslots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"));
    }

    // ----------------------------------------------------------------------

    @Test
    void getTimeslotById_ShouldReturn200() throws Exception {
        Timeslot t = new Timeslot();
        t.setDayOfWeek(DayOfWeek.TUESDAY);
        t.setStartTime(LocalTime.of(10, 0));
        t.setEndTime(LocalTime.of(11, 0));
        t.setChargingStation(savedStation);
        t = timeslotRepository.save(t);

        mockMvc.perform(get("/api/timeslots/" + t.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(t.getId()))
                .andExpect(jsonPath("$.dayOfWeek").value("TUESDAY"));
    }

    // ----------------------------------------------------------------------

    @Test
    void createTimeslot_ShouldReturn201() throws Exception {

        TimeslotRequest request = new TimeslotRequest(
                DayOfWeek.WEDNESDAY,
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                savedStation.getId()
        );

        mockMvc.perform(post("/api/timeslots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dayOfWeek").value("WEDNESDAY"))
                .andExpect(jsonPath("$.chargingStationId").value(savedStation.getId()));

        assertThat(timeslotRepository.count()).isEqualTo(1);
    }

    // ----------------------------------------------------------------------

    @Test
    void createMultipleTimeslots_ShouldReturn201() throws Exception {

        TimeslotRequest t1 = new TimeslotRequest(
                DayOfWeek.THURSDAY,
                LocalTime.of(9,0),
                LocalTime.of(10,0),
                savedStation.getId()
        );

        TimeslotRequest t2 = new TimeslotRequest(
                DayOfWeek.THURSDAY,
                LocalTime.of(10,0),
                LocalTime.of(11,0),
                savedStation.getId()
        );

        List<TimeslotRequest> list = List.of(t1, t2);

        mockMvc.perform(post("/api/timeslots/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(list)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].dayOfWeek").value("THURSDAY"))
                .andExpect(jsonPath("$[1].dayOfWeek").value("THURSDAY"));

        assertThat(timeslotRepository.count()).isEqualTo(2);
    }

    // ----------------------------------------------------------------------

    @Test
    void updateTimeslot_ShouldReturn200() throws Exception {
        Timeslot t = new Timeslot();
        t.setDayOfWeek(DayOfWeek.FRIDAY);
        t.setStartTime(LocalTime.of(8,0));
        t.setEndTime(LocalTime.of(9,0));
        t.setChargingStation(savedStation);
        t = timeslotRepository.save(t);

        TimeslotRequest update = new TimeslotRequest(
                DayOfWeek.SATURDAY,
                LocalTime.of(18, 0),
                LocalTime.of(19, 0),
                savedStation.getId()
        );

        mockMvc.perform(put("/api/timeslots/" + t.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayOfWeek").value("SATURDAY"))
                .andExpect(jsonPath("$.startTime").value("18:00:00"));
    }

    // ----------------------------------------------------------------------

    @Test
    void deleteTimeslot_ShouldReturn204() throws Exception {

        Timeslot t = new Timeslot();
        t.setDayOfWeek(DayOfWeek.MONDAY);
        t.setStartTime(LocalTime.of(10,0));
        t.setEndTime(LocalTime.of(11,0));
        t.setChargingStation(savedStation);
        t = timeslotRepository.save(t);

        mockMvc.perform(delete("/api/timeslots/" + t.getId()))
                .andExpect(status().isNoContent());

        entityManager.flush();
        entityManager.clear();

        assertThat(timeslotRepository.count()).isZero();
    }
}