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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
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
    void createBus_shouldReturn201() throws Exception {
        BusCreateRequest request = new BusCreateRequest("AAA111", 40, "AC,TV");

        BusResponse response = new BusResponse(
                1L,
                "AAA111",
                40,
                "AC,TV",
                "ACTIVE",
                LocalDate.now().plusMonths(2),
                LocalDate.now().plusMonths(3)
        );

        when(busService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/buses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.plate").value("AAA111"))
                .andExpect(jsonPath("$.capacity").value(40))
                .andExpect(jsonPath("$.amenities").value("AC,TV"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.soatExp").exists())
                .andExpect(jsonPath("$.revisionExp").exists());
    }


    @Test
    void getBus_shouldReturn200() throws Exception {
        BusResponse response = new BusResponse(
                5L,
                "BBB222",
                30,
                "AC",
                "ACTIVE",
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(20)
        );

        when(busService.get(5L)).thenReturn(response);

        mockMvc.perform(get("/api/buses/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.plate").value("BBB222"))
                .andExpect(jsonPath("$.soatExp").exists())
                .andExpect(jsonPath("$.revisionExp").exists());
    }

    @Test
    void listBuses_shouldReturn200() throws Exception {

        BusResponse b1 = new BusResponse(
                1L,
                "CCC333",
                50,
                "TV",
                "ACTIVE",
                LocalDate.now().plusDays(15),
                LocalDate.now().plusDays(30)
        );

        Page<BusResponse> page = new PageImpl<>(List.of(b1));

        when(busService.list(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/buses?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].plate").value("CCC333"))
                .andExpect(jsonPath("$.content[0].soatExp").exists())
                .andExpect(jsonPath("$.content[0].revisionExp").exists());
    }



    @Test
    void updateBus_shouldReturn200() throws Exception {
        BusUpdateRequest request = new BusUpdateRequest(45, "WiFi,AC", "ACTIVE");

        BusResponse updated = new BusResponse(
                2L,
                "DDD444",
                45,
                "WiFi,AC",
                "ACTIVE",
                LocalDate.now().plusMonths(1),
                LocalDate.now().plusMonths(3)
        );

        when(busService.update(eq(2L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/buses/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.capacity").value(45))
                .andExpect(jsonPath("$.amenities").value("WiFi,AC"))
                .andExpect(jsonPath("$.soatExp").exists())
                .andExpect(jsonPath("$.revisionExp").exists());
    }


    @Test
    void deleteBus_shouldReturn204() throws Exception {
        doNothing().when(busService).delete(10L);

        mockMvc.perform(delete("/api/buses/10"))
                .andExpect(status().isNoContent());
    }
}