package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.api.dto.AssignmentDtos;
import com.unimag.edu.proyecto_final.api.dto.AssignmentDtos.*;
import com.unimag.edu.proyecto_final.service.AssignmentService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AssignmentController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class AssignmentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AssignmentService service;
    @Autowired
    private AssignmentService assignmentService;


    @Test
    void assignTrip_debe_retornar_200_y_assignmentResponse() throws Exception {

        Long tripId = 1L;


        AssignmentDtos.AssignmentCreateRequest request =
                new AssignmentDtos.AssignmentCreateRequest(
                        999L,
                        10L,
                        20L,
                        true
                );

        // Respuesta esperada del servicio
        AssignmentDtos.AssignmentResponse response =
                new AssignmentDtos.AssignmentResponse(
                        100L,
                        tripId,
                        10L,
                        20L,
                        true,
                        LocalDateTime.now()
                );

        AssignmentDtos.AssignmentCreateRequest fixedRequest =
                new AssignmentDtos.AssignmentCreateRequest(
                        tripId,
                        10L,
                        20L,
                        true
                );

        when(assignmentService.assign(tripId, fixedRequest))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/trips/{id}/assign", tripId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.tripId").value(1))
                .andExpect(jsonPath("$.driverId").value(10))
                .andExpect(jsonPath("$.dispatcherId").value(20))
                .andExpect(jsonPath("$.checklistOk").value(true));
    }

    @Test
    void getAssignment_shouldReturn200() throws Exception {

        AssignmentResponse response = new AssignmentResponse(1L,10L,5L,3L,true,
                LocalDateTime.now());

        when(service.get(1L)).thenReturn(response);

        mockMvc.perform(get("/api/assignments/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void listAssignments_shouldReturn200() throws Exception {
        AssignmentResponse item = new AssignmentResponse(1L,10L,5L,3L,true,
                LocalDateTime.now());

        Page<AssignmentResponse> page = new PageImpl<>(List.of(item), PageRequest.of(0, 1), 1);

        when(service.list(any())).thenReturn(page);

        mockMvc.perform(get("/api/assignments?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    void updateAssignment_shouldReturn200() throws Exception {
        AssignmentUpdateRequest request =
                new AssignmentUpdateRequest(true);

        AssignmentResponse response =
                new AssignmentResponse(1L, 10L, 5L, 3L, true,
                        LocalDateTime.now());

        when(service.update(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/assignments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteAssignment_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/assignments/1"))
                .andExpect(status().isNoContent());
    }
}