
package com.unimag.edu.proyecto_final.service;

import com.sun.jdi.request.InvalidRequestStateException;
import com.unimag.edu.proyecto_final.api.dto.BaggageDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Baggage;
import com.unimag.edu.proyecto_final.domine.entities.Ticket;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
import com.unimag.edu.proyecto_final.domine.repository.BaggageRepository;
import com.unimag.edu.proyecto_final.domine.repository.TicketRepository;
import com.unimag.edu.proyecto_final.exception.InvalidStateException;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.BaggageMapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BaggageServiceImplTest {

    @Mock
    private BaggageRepository baggageRepository;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private BaggageMapper mapper;

    @Mock
    private ConfiService confiService;

    @InjectMocks
    private BaggageServicelmpl service;

    @BeforeEach
    void init() {}
    @Test
    void register_debe_crear_baggage_correctamente() {
        // Mock DTO
        BaggageCreateRequest req = mock(BaggageCreateRequest.class);
        when(req.ticketId()).thenReturn(55L);
        when(req.weightKg()).thenReturn(25.0);

        // Ticket SOLD
        Ticket ticket = Ticket.builder()
                .id(55L)
                .statusTicket(StatusTicket.SOLD)
                .build();
        when(ticketRepository.findById(55L)).thenReturn(Optional.of(ticket));

        // Configuración para la regla 6
        when(confiService.getDouble("BAGGAGE_WEIGHT_LIMIT_KG")).thenReturn(20.0);
        when(confiService.getDouble("BAGGAGE_EXCESS_FEE_PER_KG")).thenReturn(3000.0);

        // Mapper
        Baggage baggage = new Baggage();
        when(mapper.toEntity(any())).thenAnswer(inv -> {
            baggage.setWeight(req.weightKg());  // ← SOLUCIÓN FINAL
            return baggage;
        });

        when(mapper.toResponse(any())).thenReturn(mock(BaggageResponse.class));

        // Repository
        when(baggageRepository.save(any())).thenReturn(baggage);

        // Ejecutar
        BaggageResponse result = service.register(req);

        assertThat(result).isNotNull();

        assertThat(baggage.getTicket()).isEqualTo(ticket);
        assertThat(baggage.getWeight()).isEqualTo(25.0);                      // ← ahora sí funciona
        assertThat(baggage.getFee().doubleValue()).isEqualTo(new BigDecimal(15000.0));
        assertThat(baggage.getTagCode()).startsWith("BAG-55-");

        verify(mapper).toResponse(any());
    }



    @Test
    void register_debe_fallar_si_ticket_no_existe() {
        BaggageCreateRequest req = mock(BaggageCreateRequest.class);
        when(req.ticketId()).thenReturn(99L);

        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.register(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("ticket not found");

        verify(ticketRepository).findById(99L);
    }
    @Test
    void register_debe_fallar_si_ticket_no_esta_sold() {

        BaggageCreateRequest req = mock(BaggageCreateRequest.class);
        when(req.ticketId()).thenReturn(55L);

        Ticket ticket = Ticket.builder()
                .id(55L)
                .statusTicket(StatusTicket.CANCELLED)
                .build();

        when(ticketRepository.findById(55L)).thenReturn(Optional.of(ticket));

        assertThatThrownBy(() -> service.register(req))
                .isInstanceOf(InvalidStateException.class)
                .hasMessageContaining("Equipaje solo permitido para tickets vendidos");

        verify(ticketRepository).findById(55L);
    }


    @Test
    void get_debe_devolver_response_si_existe() {
        Baggage baggage = Baggage.builder().id(5L).build();
        when(baggageRepository.findById(5L)).thenReturn(Optional.of(baggage));

        BaggageResponse response = mock(BaggageResponse.class);
        when(mapper.toResponse(baggage)).thenReturn(response);

        var result = service.get(5L);

        assertThat(result).isNotNull();
        verify(baggageRepository).findById(5L);
        verify(mapper).toResponse(baggage);
    }

    @Test
    void get_debe_fallar_si_no_existe() {
        when(baggageRepository.findById(888L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(888L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("baggage not found");
    }


    @Test
    void listByTicket_debe_devolver_pagina_mapeada() {
        Pageable pageable = PageRequest.of(0, 10);

        Baggage a = Baggage.builder().id(1L).build();
        Baggage b = Baggage.builder().id(2L).build();

        Page<Baggage> page = new PageImpl<>(List.of(a, b), pageable, 2);

        when(baggageRepository.findAll(pageable)).thenReturn(page);
        when(mapper.toResponse(any())).thenReturn(mock(BaggageResponse.class));

        Page<BaggageResponse> result = service.listByTicket(10L, pageable);

        assertThat(result.getContent()).hasSize(2);
        verify(baggageRepository).findAll(pageable);
        verify(mapper, times(2)).toResponse(any());
    }


    @Test
    void update_debe_modificar_y_guardar() {
        Long id = 10L;

        Baggage baggage = Baggage.builder().id(id).build();
        when(baggageRepository.findById(id)).thenReturn(Optional.of(baggage));

        BaggageUpdateRequest req = mock(BaggageUpdateRequest.class);

        BaggageResponse mapped = mock(BaggageResponse.class);
        when(mapper.toResponse(baggage)).thenReturn(mapped);

        BaggageResponse result = service.update(id, req);

        verify(mapper).updateEntityFromDto(req, baggage);
        verify(baggageRepository).save(baggage);
        verify(mapper).toResponse(baggage);

        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void update_debe_fallar_si_no_existe() {
        when(baggageRepository.findById(404L)).thenReturn(Optional.empty());
        BaggageUpdateRequest req = mock(BaggageUpdateRequest.class);

        assertThatThrownBy(() -> service.update(404L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("baggage not found");
    }


    @Test
    void delete_debe_eliminar_si_existe() {
        Long id = 100L;
        Baggage baggage = Baggage.builder().id(id).build();

        when(baggageRepository.findById(id)).thenReturn(Optional.of(baggage));

        service.delete(id);

        verify(baggageRepository).delete(baggage);
    }

    @Test
    void delete_debe_fallar_si_no_existe() {
        when(baggageRepository.findById(777L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(777L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("baggage not found");
    }
}
