package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.api.dto.TripDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.enumera.BoardingStatus;
import com.unimag.edu.proyecto_final.service.TripService;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(TripController.class)
class TripControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    TripService tripService;

    private final LocalDateTime departureReal = LocalDateTime.of(2025, 1, 1, 8, 0);


    @Test
    void createTrip_shouldReturn201() throws Exception {

        TripCreateRequest tripCreateRequest = new TripCreateRequest(
                2L, 1L,
                LocalDate.now(),
                LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(6),
                BoardingStatus.BOARDING_OPEN
        );

        TripResponse response = new TripResponse(
                10L,
                2L,
                1L,
                tripCreateRequest.date(),
                tripCreateRequest.departureAt(),
                tripCreateRequest.arrivalEta(),
                departureReal,
                "SCHEDULED",
                BoardingStatus.BOARDING_OPEN
        );

        when(tripService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/trips")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tripCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.departureReal").value("2025-01-01T08:00:00"))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }


    @Test
    void getTripById_shouldReturn200() throws Exception {

        TripResponse response = new TripResponse(
                10L,
                1L,
                2L,
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(6),
                departureReal,
                "SCHEDULED",
                BoardingStatus.BOARDING_OPEN
        );

        when(tripService.get(10L)).thenReturn(response);

        mockMvc.perform(get("/api/trips/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.departureReal").value("2025-01-01T08:00:00"));
    }


    @Test
    void listByRoute_shouldReturn200() throws Exception {
        LocalDate date = LocalDate.now();

        List<TripResponse> list = List.of(
                new TripResponse(
                        1L,
                        1L,
                        1L,
                        date,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(1),
                        departureReal,
                        "DEPARTED",
                        BoardingStatus.BOARDING_OPEN
                )
        );

        when(tripService.listByRoute(eq(1L), eq(date))).thenReturn(list);

        mockMvc.perform(get("/api/trips/route/1").param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].routeId").value(1L))
                .andExpect(jsonPath("[0].departureReal").value("2025-01-01T08:00:00"));
    }

    @Test
    void openBoarding_shouldReturn200() throws Exception {

        TripResponse response = new TripResponse(
                10L,
                2L,
                1L,
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                departureReal,
                "SCHEDULED",
                BoardingStatus.BOARDING_OPEN
        );

        when(tripService.openBoarding(10L)).thenReturn(response);

        mockMvc.perform(post("/api/trips/10/boarding/open"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.boardingStatus").value("BOARDING_OPEN"));
    }

    @Test
    void closeBoarding_shouldReturn200() throws Exception {

        TripResponse response = new TripResponse(
                10L,
                2L,
                1L,
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                departureReal,
                "SCHEDULED",
                BoardingStatus.BOARDING_CLOSED
        );

        when(tripService.closeBoarding(10L)).thenReturn(response);

        mockMvc.perform(post("/api/trips/10/boarding/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.boardingStatus").value("BOARDING_CLOSED"));
    }
    @Test
    void listUpcoming_shouldReturn200() throws Exception {

        List<TripResponse> list = List.of(
                new TripResponse(
                        1L,
                        1L,
                        10L,
                        LocalDate.now(),
                        LocalDateTime.now().plusHours(2),
                        LocalDateTime.now().plusHours(4),
                        departureReal,
                        "SCHEDULED",
                        BoardingStatus.BOARDING_OPEN
                )
        );

        when(tripService.listUpcoming()).thenReturn(list);

        mockMvc.perform(get("/api/trips/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"))
                .andExpect(jsonPath("$[0].departureReal").value("2025-01-01T08:00:00"));
    }


    @Test
    void listBetween_shouldReturn200() throws Exception {

        LocalDate date = LocalDate.of(2025, 11, 10);
        LocalDateTime start = LocalDateTime.of(2025, 11, 10, 8, 0);
        LocalDateTime end = LocalDateTime.of(2025, 11, 10, 20, 0);

        List<TripResponse> list = List.of(
                new TripResponse(
                        1L,
                        2L,
                        3L,
                        date,
                        start.plusHours(1),
                        start.plusHours(3),
                        departureReal,
                        "SCHEDULED",
                        BoardingStatus.BOARDING_OPEN
                )
        );

        when(tripService.listBetween(start, end)).thenReturn(list);

        mockMvc.perform(get("/api/trips/between")
                        .param("start", start.toString())
                        .param("end", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].departureReal").value("2025-01-01T08:00:00"));
    }


    @Test
    void updateTrip_shouldReturn200() throws Exception {

        TripUpdateRequest request = new TripUpdateRequest(
                LocalDateTime.of(2025, 11, 13, 11, 0),
                LocalDateTime.of(2025, 11, 13, 13, 0),
                "UPDATED"
        );

        TripResponse response = new TripResponse(
                1L,
                1L,
                10L,
                LocalDate.now(),
                request.departureAt(),
                request.arrivalEta(),
                departureReal,
                "UPDATED",
                BoardingStatus.BOARDING_OPEN
        );

        when(tripService.update(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/trips/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UPDATED"))
                .andExpect(jsonPath("$.departureReal").value("2025-01-01T08:00:00"));
    }
    @Test
    void authorizeDeparture_shouldReturn200() throws Exception {

        TripResponse response = new TripResponse(
                10L,
                3L,
                5L,
                LocalDate.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now(),
                "DEPARTED",
                BoardingStatus.BOARDING_OPEN
        );

        when(tripService.authorizeDeparture(10L, 99L)).thenReturn(response);

        mockMvc.perform(post("/api/trips/10/depart")
                        .param("driverId", "99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.routeId").value(3L))
                .andExpect(jsonPath("$.busId").value(5L))
                .andExpect(jsonPath("$.status").value("DEPARTED"))
                .andExpect(jsonPath("$.departureReal").exists());
    }


    @Test
    void deleteTrip_shouldReturn204() throws Exception {
        doNothing().when(tripService).delete(1L);

        mockMvc.perform(delete("/api/trips/1"))
                .andExpect(status().isNoContent());
    }
}
