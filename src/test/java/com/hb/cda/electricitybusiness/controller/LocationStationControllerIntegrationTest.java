package com.hb.cda.electricitybusiness.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hb.cda.electricitybusiness.business.LocationStationBusiness;
import com.hb.cda.electricitybusiness.controller.dto.LocationStationRequest;
import com.hb.cda.electricitybusiness.controller.dto.LocationStationResponse;
import com.hb.cda.electricitybusiness.controller.dto.mapper.LocationStationMapper;
import com.hb.cda.electricitybusiness.model.LocationStation;
import com.hb.cda.electricitybusiness.security.jwt.JwtRequestFilter;
import com.hb.cda.electricitybusiness.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationStationController.class)
@AutoConfigureMockMvc(addFilters = false)
class LocationStationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LocationStationBusiness locationStationBusiness;

    @MockBean
    private LocationStationMapper locationStationMapper;

    @MockBean private JwtUtil jwtUtil;
    @MockBean private JwtRequestFilter jwtRequestFilter;



    // ============================================================
    // TEST CREATE
    // ============================================================
    @Test
    void createLocationStation_ShouldReturn201() throws Exception {

        LocationStationRequest request = new LocationStationRequest(
                "Test Location",
                "10 avenue de France",
                "75013",
                "Paris",
                48.8,
                2.35
        );

        LocationStation entity = new LocationStation();
        entity.setId(1L);
        entity.setCity("Paris");
        entity.setAddress("10 avenue de France");

        LocationStationResponse response = new LocationStationResponse(
                1L, "Test Location", "10 avenue de France", "75013",
                "Paris", 48.8, 2.35
        );

        when(locationStationMapper.convertToEntity(any())).thenReturn(entity);
        when(locationStationBusiness.createLocationStation(any())).thenReturn(entity);
        when(locationStationMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(post("/api/location_stations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.city").value("Paris"));
    }



    // ============================================================
    // TEST GET ALL
    // ============================================================
    @Test
    void getAllLocationStation_ShouldReturnList() throws Exception {

        LocationStation entity = new LocationStation();
        entity.setId(1L);
        entity.setCity("Paris");

        LocationStationResponse response = new LocationStationResponse(
                1L, "Loc 1", "Rue X", "75013",
                "Paris", 48.8, 2.35
        );

        when(locationStationBusiness.getAllLocationStation()).thenReturn(List.of(entity));
        when(locationStationMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(get("/api/location_stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].city").value("Paris"));
    }



    // ============================================================
    // TEST GET BY ID
    // ============================================================
    @Test
    void getLocationStationById_ShouldReturn200() throws Exception {

        LocationStation entity = new LocationStation();
        entity.setId(1L);
        entity.setCity("Paris");

        LocationStationResponse response = new LocationStationResponse(
                1L, "Loc 1", "Rue X", "75013",
                "Paris", 48.8, 2.35
        );

        when(locationStationBusiness.getLocationStationById(1L)).thenReturn(entity);
        when(locationStationMapper.toResponse(entity)).thenReturn(response);

        mockMvc.perform(get("/api/location_stations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Paris"));
    }



    // ============================================================
    // TEST UPDATE
    // ============================================================
    @Test
    void updateLocationStation_ShouldReturn200() throws Exception {

        LocationStationRequest request = new LocationStationRequest(
                "Updated Name", "New street", "75001",
                "Paris", 48.85, 2.35
        );

        LocationStation entity = new LocationStation();
        entity.setId(1L);
        entity.setCity("Paris");

        LocationStationResponse response = new LocationStationResponse(
                1L, "Updated Name", "New street", "75001",
                "Paris", 48.85, 2.35
        );

        when(locationStationMapper.convertToEntity(any())).thenReturn(entity);
        when(locationStationBusiness.updateLocationStation(eq(1L), any())).thenReturn(entity);
        when(locationStationMapper.toResponse(any())).thenReturn(response);

        mockMvc.perform(put("/api/location_stations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.city").value("Paris"))
                .andExpect(jsonPath("$.locationName").value("Updated Name"));
    }



    // ============================================================
    // TEST DELETE
    // ============================================================
    @Test
    void deleteLocationStation_ShouldReturn204() throws Exception {

        doNothing().when(locationStationBusiness).deleteLocationsStation(1L);

        mockMvc.perform(delete("/api/location_stations/1"))
                .andExpect(status().isNoContent());
    }
}
