package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.api.dto.FareRuleDtos.*;
import com.unimag.edu.proyecto_final.security.jwt.JwtService;
import com.unimag.edu.proyecto_final.service.FareRuleService;
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
@WebMvcTest(FareRuleController.class)
class FareRuleControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    FareRuleService fareRuleService;


    @MockitoBean
    JwtService jwtService;

    @Test
    void create_shouldReturn201() throws Exception {
        FareRuleCreateRequest request = new FareRuleCreateRequest(
                10L, 1L, 2L, 50.0, "STUDENT=10%", true
        );

        FareRuleResponse response = new FareRuleResponse(
                1L, 10L, 1L, 2L, 50.0, "STUDENT=10%", true
        );

        when(fareRuleService.create(any(FareRuleCreateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/fare-rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.basePrice").value(50.0));
    }

    @Test
    void get_shouldReturn200() throws Exception {
        FareRuleResponse response = new FareRuleResponse(
                2L, 20L, 3L, 4L, 80.0, "NONE", false
        );

        when(fareRuleService.get(2L)).thenReturn(response);

        mockMvc.perform(get("/api/fare-rules/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.routeId").value(20));

    }

    @Test
    void listByRoute_shouldReturn200() throws Exception {
        FareRuleResponse rule = new FareRuleResponse(
                3L, 5L, 1L, 2L, 70.0, "SENIOR=15%", true
        );

        Page<FareRuleResponse> page = new PageImpl<>(
                List.of(rule),
                PageRequest.of(0, 10),
                1
        );

        when(fareRuleService.listByRoute(eq(5L), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/fare-rules/route/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(3));
    }

    @Test
    void update_shouldReturn200()  throws Exception{
        FareRuleUpdateRequest request = new FareRuleUpdateRequest(
                90.0, "NONE", false
        );

        FareRuleResponse updated = new FareRuleResponse(
                1L, 10L, 1L, 2L, 90.0, "NONE", false
        );

        when(fareRuleService.update(eq(1L), any(FareRuleUpdateRequest.class)))
                .thenReturn(updated);

        mockMvc.perform(put("/api/fare-rules/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.basePrice").value(90.0))
                .andExpect(jsonPath("$.dynamicPricing").value(false));
    }

    @Test
    void delete_shouldReturn204()  throws Exception{
        doNothing().when(fareRuleService).delete(1L);

        mockMvc.perform(delete("/api/fare-rules/1"))
                .andExpect(status().isNoContent());
    }
}