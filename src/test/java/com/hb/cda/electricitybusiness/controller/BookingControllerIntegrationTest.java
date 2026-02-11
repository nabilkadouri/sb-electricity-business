package com.hb.cda.electricitybusiness.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hb.cda.electricitybusiness.controller.dto.BookingRequest;
import com.hb.cda.electricitybusiness.enums.ChargingStationStatus;
import com.hb.cda.electricitybusiness.enums.DayOfWeek;
import com.hb.cda.electricitybusiness.enums.PaymentMethod;
import com.hb.cda.electricitybusiness.model.*;
import com.hb.cda.electricitybusiness.repository.*;
import com.hb.cda.electricitybusiness.security.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChargingStationRepository chargingStationRepository;

    @Autowired
    private LocationStationRepository locationStationRepository;

    @Autowired
    private TimeslotRepository timeslotRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private User owner;
    private User renter;
    private ChargingStation station;
    private String renterToken;

    @BeforeEach
    void setUp() {
        // Nettoyage (ordre respectant les clés étrangères)
        timeslotRepository.deleteAll();
        chargingStationRepository.deleteAll();
        locationStationRepository.deleteAll();
        userRepository.deleteAll();

        // Création Propriétaire
        owner = new User();
        owner.setFirstName("Proprio");
        owner.setName("Bailleur");
        owner.setEmail("owner@test.com");
        owner.setPassword(passwordEncoder.encode("password"));
        owner.setRoles("ROLE_USER");
        owner.setAddress("10 rue du Port");
        owner.setCity("Nantes");
        owner.setPostaleCode("44000");
        userRepository.save(owner);

        // Création Locataire
        renter = new User();
        renter.setFirstName("Locataire");
        renter.setName("Client");
        renter.setEmail("renter@test.com");
        renter.setPassword(passwordEncoder.encode("password"));
        renter.setRoles("ROLE_USER");
        renter.setAddress("20 avenue des Fleurs");
        renter.setCity("Angers");
        renter.setPostaleCode("49000");
        userRepository.save(renter);

        renterToken = jwtUtil.generateToken(renter);

        // Emplacement
        LocationStation location = new LocationStation();
        location.setAddress("50 rue de la Paix");
        location.setCity("Paris");
        location.setPostaleCode("75001");
        locationStationRepository.save(location);

        // Borne
        station = new ChargingStation();
        station.setNameStation("Bornix 3000");
        station.setPower(new BigDecimal("22.0"));
        station.setPricePerHour(new BigDecimal("4.50"));
        station.setIsAvailable(true);
        station.setStatus(ChargingStationStatus.CONFIRMED);
        station.setUser(owner);
        station.setLocationStation(location);
        station.setCreatedAt(LocalDateTime.now());
        chargingStationRepository.save(station);

        // Créneaux horaires
        for (DayOfWeek day : DayOfWeek.values()) {
            Timeslot slot = new Timeslot();
            slot.setChargingStation(station);
            slot.setDayOfWeek(day);
            slot.setStartTime(LocalTime.of(0, 0));
            slot.setEndTime(LocalTime.of(23, 59));
            timeslotRepository.save(slot);
        }
    }

    @Test
    void createBooking_ShouldReturnCreated() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(1)
                .withHour(14).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(2);

        BookingRequest request = new BookingRequest();
        request.setChargingStationId(station.getId());
        request.setUserId(renter.getId());
        request.setStartDate(start);
        request.setEndDate(end);
        request.setPaymentType(PaymentMethod.CB);

        // 4.50 * 2h = 9.00. Frais 2.5% = 0.225. Total = 9.225 -> arrondi 9.23
        double expectedTotal = 9.23;

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + renterToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("En attente"))
                .andExpect(jsonPath("$.totalAmount").value(expectedTotal));
    }
}

