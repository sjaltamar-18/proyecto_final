package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.api.dto.IncidentDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.enumera.EntityType;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Type;
import com.unimag.edu.proyecto_final.security.jwt.JwtService;
import com.unimag.edu.proyecto_final.service.IncidentService;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(IncidentController.class)

class IncidentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    IncidentService incidentService;

    @MockitoBean
    JwtService jwtService;

    @Test
    void createIncident_shouldReturn201() throws Exception {
        IncidentCreateRequest request = new IncidentCreateRequest(
                EntityType.TRIP, 5L, Type.VEHICLE, "Motor noise"
        );

        IncidentResponse response = new IncidentResponse(
                1L, EntityType.TRIP, 5L, Type.VEHICLE, "Motor noise", LocalDateTime.now()
        );

        when(incidentService.create(any(IncidentCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("VEHICLE"));

    }

    @Test
    void getIncidentById_shoulReturn200() throws Exception {
        IncidentResponse response = new IncidentResponse(
                1L, EntityType.TRIP, 5L, Type.VEHICLE, "Motor noise", LocalDateTime.now()
        );

        when(incidentService.get(1L)).thenReturn(response);

        mockMvc.perform(get("/api/incidents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entityId").value(5));
    }

    @Test
    void listIncidents_shouldReturn200() throws Exception {
        IncidentResponse incident = new IncidentResponse(
                1L, EntityType.TRIP, 5L, Type.VEHICLE, "Note", LocalDateTime.now()
        );

        Page<IncidentResponse> page = new PageImpl<>(
                List.of(incident),
                PageRequest.of(0, 10),
                1
        );

        when(incidentService.list(any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void listByType_shouldReturn200() throws Exception {
        IncidentResponse response = new IncidentResponse(
                2L, EntityType.TRIP, 7L, Type.VEHICLE, "Crash", LocalDateTime.now()
        );

        when(incidentService.listByType("ACCIDENT"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/incidents/type/ACCIDENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("VEHICLE"));
    }

    @Test
    void listByEntity_shouldReturn200() throws Exception {
        IncidentResponse response = new IncidentResponse(
                3L, EntityType.TICKET, 10L, Type.SECURITY, "User complaint", LocalDateTime.now()
        );

        when(incidentService.listByEntity("TICKET", 10L))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/incidents/entity")
                        .param("entityType", "TICKET")
                        .param("entityId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entityId").value(10));
    }

    @Test
    void listByRange_shouldReturn200() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now();

        IncidentResponse response = new IncidentResponse(
                4L, EntityType.TRIP, 2L, Type.VEHICLE, "Oil leak", LocalDateTime.now().minusDays(1)
        );

        when(incidentService.listBetweenDates(any(), any()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/incidents/range")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(4));
    }

    @Test
    void updateIncident_shouldReturn200() throws Exception {
        IncidentUpdateRequest request = new IncidentUpdateRequest("Updated note", "MECHANICAL");

        IncidentResponse response = new IncidentResponse(
                1L, EntityType.TRIP, 5L, Type.VEHICLE, "Updated note", LocalDateTime.now()
        );

        when(incidentService.update(eq(1L), any(IncidentUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/incidents/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.note").value("Updated note"));
    }

    @Test
    void deleteIncident_shouldReturn204() throws Exception {
        doNothing().when(incidentService).delete(1L);

        mockMvc.perform(delete("/api/incidents/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void cleanup_shouldReturn200()  throws Exception {
        when(incidentService.deleteOlderThan(7))
                .thenReturn(5);

        mockMvc.perform(delete("/api/incidents/cleanup")
                        .param("days", "7"))
                .andExpect(status().isOk())
                .andExpect(content().string("5incidents deleted successfully"));

    }
}