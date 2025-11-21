package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.api.dto.BaggageDtos.*;
import com.unimag.edu.proyecto_final.service.BaggageService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(BaggageController.class)
class BaggageControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    BaggageService baggageService;


    @Test
    void register_shouldReturn201() throws Exception {
        BaggageCreateRequest request =
                new BaggageCreateRequest(8L, 22.5, new BigDecimal("15.0"), "TAG987");

        BaggageResponse response =
                new BaggageResponse(1L, 8L, 22.5, new BigDecimal("15.0"), "TAG987");

        when(baggageService.register(request)).thenReturn(response);

        mockMvc.perform(post("/api/baggages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ticketId").value(8))
                .andExpect(jsonPath("$.weightKg").value(22.5))
                .andExpect(jsonPath("$.fee").value(new BigDecimal("15.0")))
                .andExpect(jsonPath("$.tagCode").value("TAG987"));
    }

    @Test
    void get_shouldReturn200() throws Exception  {
        BaggageResponse response =
                new BaggageResponse(2L, 3L, 10.0, new BigDecimal("5.5"), "TAG111");

        when(baggageService.get(2L)).thenReturn(response);

        mockMvc.perform(get("/api/baggages/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.ticketId").value(3))
                .andExpect(jsonPath("$.weightKg").value(10.0))
                .andExpect(jsonPath("$.fee").value(new BigDecimal("5.5")))
                .andExpect(jsonPath("$.tagCode").value("TAG111"));
    }

    @Test
    void getTickets_shouldReturn200() throws Exception  {
        BaggageResponse bag =
                new BaggageResponse(4L, 99L, 18.0, new BigDecimal("12.0"), "TAG777");

        Page<BaggageResponse> page =
                new PageImpl<>(List.of(bag));

        when(baggageService.listByTicket(99L, PageRequest.of(0, 20)))
                .thenReturn(page);

        mockMvc.perform(get("/api/baggages/ticket/99?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].ticketId").value(99))
                .andExpect(jsonPath("$.content[0].tagCode").value("TAG777"));
    }

    @Test
    void update_shouldReturn200() throws Exception  {

        BaggageUpdateRequest request =
                new BaggageUpdateRequest(25.0, new BigDecimal("18.0"));

        BaggageResponse response =
                new BaggageResponse(7L, 44L, 25.0, new BigDecimal("18.0"), "TAG555");

        when(baggageService.update(7L, request)).thenReturn(response);

        mockMvc.perform(put("/api/baggages/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.weightKg").value(25.0))
                .andExpect(jsonPath("$.fee").value(18.0))
                .andExpect(jsonPath("$.tagCode").value("TAG555"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(baggageService).delete(5L);

        mockMvc.perform(delete("/api/baggages/5"))
                .andExpect(status().isNoContent());
    }
}