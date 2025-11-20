package com.unimag.edu.proyecto_final.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unimag.edu.proyecto_final.api.dto.TicketDtos.*;
import com.unimag.edu.proyecto_final.service.TicketService;
import org.apache.catalina.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(TicketController.class)

class TicketControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    TicketService ticketService;

    @Test
    void createTicket_shouldReturn201() throws  Exception {
        TicketCreateRequest request = new TicketCreateRequest(1L,2L,"A1",12L,3L,20.0,"CARD",true);

        TicketResponse response = new TicketResponse(100L,1L,2L,"A1",12L,3L,20.0,"CARD","CREATED","QR123");

        when(ticketService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/tickets").contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L));
    }

    @Test
    void getTicket_shouldReturn200() throws  Exception {
        TicketResponse response = new TicketResponse(5L,1L,2L,"A2",10L,20L,20.0,"CASH","OK","QR123");

        when(ticketService.get(5L)).thenReturn(response);

        mockMvc.perform(get("/api/tickets/5").contentType(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    void listByTrip_shouldReturn200() throws  Exception {

        TicketResponse t1 = new TicketResponse(
                1L, 10L, 99L, "A1", 1L, 2L, 15.0, "CASH", "CREATED", "QR1"
        );

        List<TicketResponse> list = List.of(t1);

        when(ticketService.listByTrip(10L)).thenReturn(list);

        mockMvc.perform(get("/api/tickets/trip/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].tripId").value(10L));
    }

    @Test
    void listByPassenger_shouldReturn200() throws  Exception {
        TicketResponse t1 = new TicketResponse(
                3L, 20L, 100L, "A4", 1L, 2L, 15.0, "CASH", "CREATED", "QR3"
        );

        List<TicketResponse> list = List.of(t1);

        when(ticketService.listByPassenger(100L)).thenReturn(list);

        mockMvc.perform(get("/api/tickets/passenger/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L))
                .andExpect(jsonPath("$[0].passengerId").value(100L));

    }

    @Test
    void updateTicket_shouldReturn200() throws  Exception {
        TicketUpdateRequest request = new TicketUpdateRequest(
                "APPROVED", "CARD"
        );

        TicketResponse updated = new TicketResponse(
                15L, 1L, 1L, "A1", 1L, 2L, 20.0, "CARD", "APPROVED", "QR12"
        );

        when(ticketService.update(eq(15L), any())).thenReturn(updated);

        mockMvc.perform(put("/api/tickets/15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void deleteTicket_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/tickets/9"))
                .andExpect(status().isNoContent());

        verify(ticketService).cancel(9L);
    }

    @Test
    void qrCode_shouldReturn200() throws Exception {

        TicketResponse response = new TicketResponse(
                77L, 1L, 1L, "A3", 10L, 20L, 30.0, "CARD", "OK", "XYZ"
        );

        when(ticketService.getByQrCode("ABC123")).thenReturn(response);

        mockMvc.perform(get("/api/tickets/qr/qrCode")
                        .param("ticketCode", "ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(77L));
    }

}