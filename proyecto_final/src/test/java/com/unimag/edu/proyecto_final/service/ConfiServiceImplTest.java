package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.ConfiDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Confi;
import com.unimag.edu.proyecto_final.domine.repository.ConfiRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.ConfiMapper;

import jakarta.persistence.EntityExistsException;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
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
class ConfiServiceImplTest {

    @Mock
    private ConfiRepository confiRepository;

    @Mock
    private ConfiMapper confiMapper;

    @InjectMocks
    private ConfiServicelmpl service;

    @BeforeEach
    void setup() {}

    @Test
    void create_debe_crear_config_correctamente() {
        ConfigCreateRequest req = mock(ConfigCreateRequest.class);
        when(req.key()).thenReturn("HOLD_TIME");

        when(confiRepository.existsByKey("HOLD_TIME")).thenReturn(false);

        Confi confi = Confi.builder().key("HOLD_TIME").build();
        when(confiMapper.toEntity(req)).thenReturn(confi);

        ConfigResponse mappedResp = mock(ConfigResponse.class);
        when(confiMapper.toResponse(confi)).thenReturn(mappedResp);

        var result = service.create(req);

        assertThat(result).isNotNull().isEqualTo(mappedResp);
        verify(confiRepository).existsByKey("HOLD_TIME");
        verify(confiMapper).toEntity(req);
        verify(confiRepository).save(confi);
        verify(confiMapper).toResponse(confi);
    }

    @Test
    void create_debe_fallar_si_key_ya_existe() {
        ConfigCreateRequest req = mock(ConfigCreateRequest.class);
        when(req.key()).thenReturn("HOLD_TIME");

        when(confiRepository.existsByKey("HOLD_TIME")).thenReturn(true);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("config already exists");
    }

    @Test
    void get_debe_retornar_config_si_existe() {
        Confi confi = Confi.builder().key("MAX_OVERBOOK").build();
        when(confiRepository.findByKey("MAX_OVERBOOK")).thenReturn(Optional.of(confi));

        ConfigResponse mapped = mock(ConfigResponse.class);
        when(confiMapper.toResponse(confi)).thenReturn(mapped);

        var result = service.get("MAX_OVERBOOK");

        assertThat(result).isNotNull();
        verify(confiRepository).findByKey("MAX_OVERBOOK");
        verify(confiMapper).toResponse(confi);
    }

    @Test
    void get_debe_fallar_si_no_existe() {
        when(confiRepository.findByKey("UNKNOWN")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get("UNKNOWN"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("config not found");
    }

    @Test
    void list_debe_devolver_pagina_mapeada() {
        Pageable pageable = PageRequest.of(0, 10);

        Confi c1 = Confi.builder().key("A").build();
        Confi c2 = Confi.builder().key("B").build();

        Page<Confi> page = new PageImpl<>(List.of(c1, c2), pageable, 2);

        when(confiRepository.findAll(pageable)).thenReturn(page);
        when(confiMapper.toResponse(any())).thenReturn(mock(ConfigResponse.class));

        var resp = service.list(pageable);

        assertThat(resp.getTotalElements()).isEqualTo(2);
        verify(confiRepository).findAll(pageable);
        verify(confiMapper, times(2)).toResponse(any());
    }

    @Test
    void update_debe_modificar_y_guardar() {
        Confi confi = Confi.builder().key("HOLD_TIME").build();
        when(confiRepository.findByKey("HOLD_TIME")).thenReturn(Optional.of(confi));

        ConfigUpdateRequest req = mock(ConfigUpdateRequest.class);

        ConfigResponse mapped = mock(ConfigResponse.class);
        when(confiMapper.toResponse(confi)).thenReturn(mapped);

        var result = service.update("HOLD_TIME", req);

        verify(confiMapper).updateEntityFromDto(req, confi);
        verify(confiRepository).save(confi);
        verify(confiMapper).toResponse(confi);

        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void update_debe_fallar_si_no_existe() {
        when(confiRepository.findByKey("NOT_EXIST")).thenReturn(Optional.empty());
        ConfigUpdateRequest req = mock(ConfigUpdateRequest.class);

        assertThatThrownBy(() -> service.update("NOT_EXIST", req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("config not found");
    }

    @Test
    void delete_debe_eliminar_si_existe() {
        Confi confi = Confi.builder().key("FEE").build();
        when(confiRepository.findByKey("FEE")).thenReturn(Optional.of(confi));

        service.delete("FEE");

        verify(confiRepository).delete(confi);
    }

    @Test
    void delete_debe_fallar_si_no_existe() {
        when(confiRepository.findByKey("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete("INVALID"))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("config not found");
    }
}
