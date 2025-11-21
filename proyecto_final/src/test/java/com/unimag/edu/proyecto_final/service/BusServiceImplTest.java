
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.BusDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Bus;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusBus;
import com.unimag.edu.proyecto_final.domine.repository.BusRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.BusMapper;

import jakarta.persistence.EntityExistsException;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import org.springframework.data.domain.*;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BusServiceImplTest {

    @Mock
    private BusRepository busRepository;

    @Mock
    private BusMapper busMapper;

    @InjectMocks
    private BusServicelmpl service;

    @BeforeEach
    void setup() {}


    @Test
    void create_debe_crear_bus_correctamente() {
        BusCreateRequest req = mock(BusCreateRequest.class);
        when(req.plate()).thenReturn("ABC123");

        when(busRepository.existsByPlate("ABC123")).thenReturn(false);

        Bus bus = Bus.builder().build();
        when(busMapper.toEntity(req)).thenReturn(bus);

        BusResponse mappedResp = mock(BusResponse.class);
        when(busMapper.toResponse(bus)).thenReturn(mappedResp);

        var result = service.create(req);

        assertThat(result).isNotNull();
        assertThat(bus.getStatus()).isEqualTo(StatusBus.AVAILABLE);

        verify(busRepository).existsByPlate("ABC123");
        verify(busMapper).toEntity(req);
        verify(busRepository).save(bus);
        verify(busMapper).toResponse(bus);
    }

    @Test
    void create_debe_fallar_si_plate_ya_existe() {
        BusCreateRequest req = mock(BusCreateRequest.class);
        when(req.plate()).thenReturn("ZZZ999");

        when(busRepository.existsByPlate("ZZZ999")).thenReturn(true);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("plate already exists");
    }


    @Test
    void get_debe_retornar_bus_si_existe() {
        Bus bus = Bus.builder().id(5L).build();
        when(busRepository.findById(5L)).thenReturn(Optional.of(bus));

        BusResponse mappedResp = mock(BusResponse.class);
        when(busMapper.toResponse(bus)).thenReturn(mappedResp);

        var result = service.get(5L);

        assertThat(result).isNotNull();
        verify(busRepository).findById(5L);
        verify(busMapper).toResponse(bus);
    }

    @Test
    void get_debe_fallar_si_no_existe() {
        when(busRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("bus not found");
    }


    @Test
    void list_debe_devolver_pagina_mapeada() {
        Pageable pageable = PageRequest.of(0, 10);

        Bus a = Bus.builder().id(1L).build();
        Bus b = Bus.builder().id(2L).build();

        Page<Bus> page = new PageImpl<>(List.of(a, b), pageable, 2);

        when(busRepository.findAll(pageable)).thenReturn(page);
        when(busMapper.toResponse(any())).thenReturn(mock(BusResponse.class));

        var resp = service.list(pageable);

        assertThat(resp.getTotalElements()).isEqualTo(2);
        verify(busRepository).findAll(pageable);
        verify(busMapper, times(2)).toResponse(any());
    }


    @Test
    void update_debe_modificar_y_guardar() {
        Long id = 10L;

        Bus bus = Bus.builder().id(id).status(StatusBus.AVAILABLE).build();
        when(busRepository.findById(id)).thenReturn(Optional.of(bus));

        BusUpdateRequest req = mock(BusUpdateRequest.class);
        when(req.status()).thenReturn("ACTIVE");

        BusResponse mappedResp = mock(BusResponse.class);
        when(busMapper.toResponse(bus)).thenReturn(mappedResp);

        var result = service.update(id, req);

        verify(busMapper).updateEntityFromDto(req, bus);
        verify(busRepository).save(bus);
        verify(busMapper).toResponse(bus);

        assertThat(bus.getStatus()).isEqualTo(StatusBus.ACTIVE);
        assertThat(result).isEqualTo(mappedResp);
    }

    @Test
    void update_debe_fallar_si_no_existe() {
        when(busRepository.findById(44L)).thenReturn(Optional.empty());
        BusUpdateRequest req = mock(BusUpdateRequest.class);

        assertThatThrownBy(() -> service.update(44L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("bus not found");
    }


    @Test
    void delete_debe_eliminar_si_existe() {
        Long id = 15L;
        Bus bus = Bus.builder().id(id).build();

        when(busRepository.findById(id)).thenReturn(Optional.of(bus));

        service.delete(id);

        verify(busRepository).delete(bus);
    }

    @Test
    void delete_debe_fallar_si_no_existe() {
        when(busRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(123L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("bus not found");
    }
}
