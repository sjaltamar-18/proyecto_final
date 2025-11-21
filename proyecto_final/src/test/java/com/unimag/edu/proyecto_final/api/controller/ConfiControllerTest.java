package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.api.dto.ConfiDtos.*;
import com.unimag.edu.proyecto_final.service.ConfiService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ConfiController.class)

class ConfiControllerTest extends TestBase {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ConfiService  confiService;

    @Test
    void create_shouldReturn201() throws Exception {
        ConfigCreateRequest request = new ConfigCreateRequest("site.name", "My Website");
        ConfigResponse response = new ConfigResponse(1L, "site.name", "My Website");

        when(confiService.create(request)).thenReturn(response);

        mockMvc.perform(post("/api/config")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.key").value("site.name"))
                .andExpect(jsonPath("$.value").value("My Website"));
    }

    @Test
    void get_shouldReturn200() throws Exception {
        ConfigResponse response = new ConfigResponse(1L, "app.mode", "PROD");

        when(confiService.get("app.mode")).thenReturn(response);

        mockMvc.perform(get("/api/config/app.mode"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("app.mode"))
                .andExpect(jsonPath("$.value").value("PROD"));
    }

    @Test
    void list_shouldReturn200() throws Exception {
        ConfigResponse c1 = new ConfigResponse(1L, "email.sender", "noreply@test.com");
        Page<ConfigResponse> page = new PageImpl<>(List.of(c1));

        when(confiService.list(PageRequest.of(0, 20))).thenReturn(page);

        mockMvc.perform(get("/api/config?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].key").value("email.sender"));

    }

    @Test
    void update_shouldReturn200() throws Exception {

        ConfigUpdateRequest request = new ConfigUpdateRequest("ENABLED");
        ConfigResponse response = new ConfigResponse(5L, "maintenance.mode", "ENABLED");

        when(confiService.update("maintenance.mode", request)).thenReturn(response);

        mockMvc.perform(put("/api/config/maintenance.mode")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("maintenance.mode"))
                .andExpect(jsonPath("$.value").value("ENABLED"));
    }

    @Test
    void delete_shouldReturn204() throws Exception  {
        doNothing().when(confiService).delete("theme.color");

        mockMvc.perform(delete("/api/config/theme.color"))
                .andExpect(status().isNoContent());
    }
}