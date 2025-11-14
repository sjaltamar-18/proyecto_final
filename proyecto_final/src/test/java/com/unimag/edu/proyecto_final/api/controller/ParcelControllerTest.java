package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.api.dto.ParcelDtos.*;
import com.unimag.edu.proyecto_final.service.ParcelService;
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
@WebMvcTest(ParcelController.class)

class ParcelControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ParcelService parcelService;

    @Test
    void create_shouldReturn201() throws Exception {
        ParcelCreateRequest request = new ParcelCreateRequest(
                "P001", "Carlos", "3001234567",
                "Maria", "3119876543",
                1L, 2L, 15000.0
        );

        ParcelResponse response = new ParcelResponse(
                1L, "P001", "Carlos", "3001234567",
                "Maria", "3119876543",
                1L, 2L, 15000.0,
                "PENDING", null, null
        );

        when(parcelService.create(any(ParcelCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/parcels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.code").value("P001"));
    }

    @Test
    void getById_shouldReturn200() throws Exception {
        ParcelResponse response = new ParcelResponse(
                1L, "P001", "Carlos", "3001234567",
                "Maria", "3119876543",
                1L, 2L, 15000.0,
                "PENDING", null, null
        );

        when(parcelService.get(1L)).thenReturn(response);

        mockMvc.perform(get("/api/parcels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("P001"));

    }

    @Test
    void getByCode_shouldReturn200() throws Exception {
        ParcelResponse response = new ParcelResponse(
                1L, "P001", "Carlos", "3001234567",
                "Maria", "3119876543",
                1L, 2L, 15000.0,
                "PENDING", null, null
        );

        when(parcelService.getByCode("P001"))
                .thenReturn(response);

        mockMvc.perform(get("/api/parcels/code/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senderName").value("Carlos"));
    }

    @Test
    void getByStatus_shouldReturn200() throws Exception {

        ParcelResponse response = new ParcelResponse(
                1L, "P001", "Carlos", "3001234567",
                "Maria", "3119876543",
                1L, 2L, 15000.0,
                "PENDING", null, null
        );

        Page<ParcelResponse> page = new PageImpl<>(
                List.of(response),
                PageRequest.of(0, 10),
                1
        );

       when(parcelService.listByStatus(eq("PENDING"), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/parcels/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("P001"));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        ParcelUpdateRequest request = new ParcelUpdateRequest("DELIVERED");

        ParcelResponse response = new ParcelResponse(
                1L, "P001", "Carlos", "3001234567",
                "Maria", "3119876543",
                1L, 2L, 15000.0,
                "DELIVERED", null, null
        );

       when(parcelService.update(eq(1L), any(ParcelUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/parcels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
   doNothing().when(parcelService).delete(1L);

        mockMvc.perform(delete("/api/parcels/1"))
                .andExpect(status().isNoContent());
    }
}