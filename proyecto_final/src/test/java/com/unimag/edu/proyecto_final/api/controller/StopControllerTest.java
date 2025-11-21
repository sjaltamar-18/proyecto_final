package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.api.dto.StopDtos.*;
import com.unimag.edu.proyecto_final.service.StopService;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(StopController.class)
class StopControllerTest  extends TestBase{

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    StopService stopService;


    @Test
    void createStop_shouldReturn201() throws Exception {

        StopCreateRequest request = new StopCreateRequest(
                1L,"stop1",1,10.123,-21.344
        );

        StopResponse response = new StopResponse(
                10L,1L,"stop1",1,10.123,-21.344);

        when(stopService.create(eq(1L),any())).thenReturn(response);

        mockMvc.perform(post("/api/stops/route/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("stop1"));
    }

    @Test
    void getStop_shouldReturn200() throws Exception {
        StopResponse response = new StopResponse(
                10L,1L,"stop1",1,10.123,-21.344
        );
        when(stopService.get(10L)).thenReturn(response);

        mockMvc.perform(get("/api/stops/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("stop1"));
    }

    @Test
    void updateStop_shouldReturn200() throws Exception {
        StopUpdateRequest request = new StopUpdateRequest(
                "Parada Nueva", 2, 9.555, -73.222
        );

        StopResponse updated = new StopResponse(
                10L, 2L, "Parada Nueva", 2, 9.555, -73.222
        );

        when(stopService.update(eq(10L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/stops/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Parada Nueva"))
                .andExpect(jsonPath("$.order").value(2));
    }

    @Test
    void deleteStop_shouldReturn200() throws Exception {

        mockMvc.perform(delete("/api/stops/10"))
                .andExpect(status().isNoContent());

        Mockito.verify(stopService).delete(10L);
    }
}