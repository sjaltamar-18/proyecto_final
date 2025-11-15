package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.api.dto.BusDtos.*;
import com.unimag.edu.proyecto_final.service.BusService;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(BusController.class)
class BusControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    BusService busService;

    @Test
    void create_shouldReturn201() throws Exception  {
        BusCreateRequest request = new BusCreateRequest("ABC123", 40, "A/C, TV");
        BusResponse response = new BusResponse(1L, "ABC123", 40, "A/C, TV", "ACTIVE");

        when(busService.create(request)).thenReturn(response);

        mockMvc.perform(post("/api/buses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.plate").value("ABC123"))
                .andExpect(jsonPath("$.capacity").value(40))
                .andExpect(jsonPath("$.amenities").value("A/C, TV"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void get_shouldReturn200() throws Exception {
        BusResponse response = new BusResponse(5L, "XYZ789", 50, "USB, WIFI", "ACTIVE");

        when(busService.get(5L)).thenReturn(response);

        mockMvc.perform(get("/api/buses/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.plate").value("XYZ789"))
                .andExpect(jsonPath("$.capacity").value(50))
                .andExpect(jsonPath("$.amenities").value("USB, WIFI"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void listshouldReturn200() throws Exception {
        BusResponse bus = new BusResponse(2L, "GGG555", 30, "TV", "INACTIVE");
        Page<BusResponse> page = new PageImpl<>(List.of(bus));

        when(busService.list(PageRequest.of(0, 20))).thenReturn(page);

        mockMvc.perform(get("/api/buses?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].plate").value("GGG555"))
                .andExpect(jsonPath("$.content[0].status").value("INACTIVE"));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        BusUpdateRequest request = new BusUpdateRequest(60, "A/C, WIFI", "ACTIVE");
        BusResponse response = new BusResponse(10L, "AAA111", 60, "A/C, WIFI", "ACTIVE");

        when(busService.update(10L, request)).thenReturn(response);

        mockMvc.perform(put("/api/buses/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.capacity").value(60))
                .andExpect(jsonPath("$.amenities").value("A/C, WIFI"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void delete_shouldReturn204() throws Exception{
        doNothing().when(busService).delete(7L);

        mockMvc.perform(delete("/api/buses/7"))
                .andExpect(status().isNoContent());
    }
}