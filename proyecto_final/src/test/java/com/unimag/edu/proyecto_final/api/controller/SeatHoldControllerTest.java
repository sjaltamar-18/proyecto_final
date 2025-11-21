package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.api.dto.SeatHoldDtos.*;
import com.unimag.edu.proyecto_final.security.jwt.JwtService;
import com.unimag.edu.proyecto_final.service.SeatHoldService;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
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
@WebMvcTest(SeatHoldController.class)

class SeatHoldControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SeatHoldService service;
    @Autowired
    private SeatHoldService seatHoldService;

    @MockitoBean
    JwtService jwtService;


    @Test
    void createSeatHold_shouldReturn201() throws Exception {

        SeatHoldCreateRequest request = new SeatHoldCreateRequest(
                1L,"A1",2L, 3L, 4L
        );

        SeatHoldResponse response = new SeatHoldResponse(
                2L,1L,"A1",2L, LocalDateTime.now(),"HOLD",3L, 4L
        );
        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/seat-holds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void getSeatHoldById_shouldReturn200() throws Exception {
        SeatHoldResponse response = new SeatHoldResponse(
                5L,1L,"A1",2L, LocalDateTime.now(),"HOLD",3L, 4L
        );
        when(service.get(5L)).thenReturn(response);

        mockMvc.perform(get("/api/seat-holds/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatNumber").value("A1"));

    }


    @Test
    void getSeatHoldsByUserId_shouldReturn200() throws Exception {

        List<SeatHoldResponse> list = List.of(
                new SeatHoldResponse(
                        1L,1L,"A1",2L, LocalDateTime.now(),"HOLD",3L, 4L
                )
        );
        when(service.listByUser(2L)).thenReturn(list);

        mockMvc.perform(get("/api/seat-holds/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seatNumber").value("A1"));
    }

    @Test
    void updateSeatHold_shouldReturn200() throws Exception {
        SeatHoldUpdateRequest request = new SeatHoldUpdateRequest(
                "Expired"
        );
        SeatHoldResponse response = new SeatHoldResponse(
                1L,1L,"A1",2L, LocalDateTime.now(),"EXPIRED",3L, 4L
        );

        when(service.update(eq(1L), any())).thenReturn(response);

        mockMvc.perform(patch("/api/seat-holds/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EXPIRED"));
    }

    @Test
    void expireHolds_shouldReturn200() throws Exception {

        when(service.expireHolds()).thenReturn(3);

        mockMvc.perform(post("/api/seat-holds/expire"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));

    }

    @Test
    void cleanOldExpired_shouldReturn200() throws Exception {
        when(service.cleanOldExpired()).thenReturn(3);

        mockMvc.perform(delete("/api/seat-holds/clean"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    void deleteSeatHold_shouldReturn204() throws Exception {
        doNothing().when(service).delete(9L);

        mockMvc.perform(delete("/api/seat-holds/9"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getActiveSeatHoldsByTripId_shouldReturn200() throws Exception{


        SeatHoldResponse seat1 = new SeatHoldResponse(
                1L,1L,"A1",2L, LocalDateTime.now(),"EXPIRED",3L, 4L
        );

        SeatHoldResponse seat2 = new SeatHoldResponse(
                11L,1L,"A2",3L, LocalDateTime.now(),"EXPIRED",3L, 4L
        );

        List<SeatHoldResponse> list = List.of(seat1,seat2);

        when(seatHoldService.listActiveByTrip(1L)).thenReturn(list);

        mockMvc.perform(get("/api/seat-holds/trip/{tripId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].seatNumber").value("A1"))
                .andExpect(jsonPath("$[1].id").value(11L))
                .andExpect(jsonPath("$[1].seatNumber").value("A2"));
    }
}