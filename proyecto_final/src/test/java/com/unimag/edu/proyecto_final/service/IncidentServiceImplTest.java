
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.IncidentDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Incident;
import com.unimag.edu.proyecto_final.domine.entities.enumera.EntityType;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Type;
import com.unimag.edu.proyecto_final.domine.repository.IncidentRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.IncidentMapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceImplTest {

    @Mock
    private IncidentRepository incidentRepository;

    @Mock
    private IncidentMapper incidentMapper;

    @InjectMocks
    @Spy
    private IncidentServicelmpl service;

    @BeforeEach
    void setup() {}

    // ==========================================================================
    // CREATE
    // ==========================================================================
    @Test
    void create_debe_crear_incident_correctamente() {
        IncidentCreateRequest req = mock(IncidentCreateRequest.class);
        Incident incident = new Incident();

        incident.setEntityType(EntityType.TICKET);
        incident.setType(Type.SECURITY);

        when(incidentMapper.toEntity(req)).thenReturn(incident);

        Incident saved = Incident.builder().id(1L).build();
        when(incidentRepository.save(incident)).thenReturn(saved);

        IncidentResponse mappedResp = mock(IncidentResponse.class);
        when(incidentMapper.toResponse(saved)).thenReturn(mappedResp);

        var result = service.create(req);

        assertThat(result).isEqualTo(mappedResp);
        verify(incidentRepository).save(incident);
        verify(incidentMapper).toResponse(saved);
    }

    @Test
    void create_debe_fallar_si_entityType_es_null() {
        IncidentCreateRequest req = mock(IncidentCreateRequest.class);
        Incident incident = new Incident();

        incident.setEntityType(null); // produce error
        incident.setType(Type.DELIVERY_FAIL);

        when(incidentMapper.toEntity(req)).thenReturn(incident);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Incident not found");
    }

    @Test
    void create_debe_fallar_si_type_es_null() {
        IncidentCreateRequest req = mock(IncidentCreateRequest.class);
        Incident incident = new Incident();

        incident.setEntityType(EntityType.TRIP);
        incident.setType(null);

        when(incidentMapper.toEntity(req)).thenReturn(incident);

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Incident type not found");
    }

    // ==========================================================================
    // GET
    // ==========================================================================
    @Test
    void get_debe_retornar_incident_si_existe() {
        Incident incident = Incident.builder().id(10L).build();
        when(incidentRepository.findById(10L)).thenReturn(Optional.of(incident));

        IncidentResponse mapped = mock(IncidentResponse.class);
        when(incidentMapper.toResponse(incident)).thenReturn(mapped);

        var result = service.get(10L);

        assertThat(result).isEqualTo(mapped);
        verify(incidentRepository).findById(10L);
    }

    @Test
    void get_debe_fallar_si_no_existe() {
        when(incidentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Incident not found");
    }

    // ==========================================================================
    // LIST
    // ==========================================================================
    @Test
    void list_debe_devolver_pagina_mapeada() {
        Pageable pageable = PageRequest.of(0, 10);

        Incident a = Incident.builder().id(1L).build();
        Incident b = Incident.builder().id(2L).build();

        Page<Incident> page = new PageImpl<>(List.of(a, b), pageable, 2);

        when(incidentRepository.findAll(pageable)).thenReturn(page);
        when(incidentMapper.toResponse(any())).thenReturn(mock(IncidentResponse.class));

        var resp = service.list(pageable);

        assertThat(resp.getTotalElements()).isEqualTo(2);
        verify(incidentRepository).findAll(pageable);
        verify(incidentMapper, times(2)).toResponse(any());
    }

    // ==========================================================================
    // LIST BY ENTITY
    // ==========================================================================
    @Test
    void listByEntity_debe_filtrar_por_type() {
        when(incidentRepository.findByType(Type.DELIVERY_FAIL))
                .thenReturn(List.of(Incident.builder().id(1L).build()));

        when(incidentMapper.toResponse(any()))
                .thenReturn(mock(IncidentResponse.class));

        var list = service.listByEntity("DELIVERY_FAIL", 50L);

        assertThat(list).hasSize(1);
        verify(incidentRepository).findByType(Type.DELIVERY_FAIL);
    }

    // ==========================================================================
    // LIST BY TYPE
    // ==========================================================================
    @Test
    void listByType_debe_filtrar_correctamente() {
        when(incidentRepository.findByType(Type.OVERBOOK))
                .thenReturn(List.of(Incident.builder().id(5L).build()));

        when(incidentMapper.toResponse(any()))
                .thenReturn(mock(IncidentResponse.class));

        var list = service.listByType("overbook");

        assertThat(list).hasSize(1);
        verify(incidentRepository).findByType(Type.OVERBOOK);
    }

    // ==========================================================================
    // LIST BETWEEN DATES
    // ==========================================================================
    @Test
    void listBetweenDates_debe_devolver_incidents_mapeados() {
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();

        Incident one = Incident.builder().id(1L).build();
        when(incidentRepository.findBetweenDates(start, end)).thenReturn(List.of(one));

        when(incidentMapper.toResponse(one)).thenReturn(mock(IncidentResponse.class));

        var list = service.listBetweenDates(start, end);

        assertThat(list).hasSize(1);
        verify(incidentRepository).findBetweenDates(start, end);
    }

    // ==========================================================================
    // UPDATE
    // ==========================================================================
    @Test
    void update_debe_modificar_y_guardar() {
        Incident existing = Incident.builder().id(40L).build();
        when(incidentRepository.findById(40L)).thenReturn(Optional.of(existing));

        IncidentUpdateRequest req = mock(IncidentUpdateRequest.class);

        Incident saved = Incident.builder().id(40L).build();
        when(incidentRepository.save(existing)).thenReturn(saved);

        IncidentResponse mapped = mock(IncidentResponse.class);
        when(incidentMapper.toResponse(saved)).thenReturn(mapped);

        var result = service.update(40L, req);

        verify(incidentMapper).updateEntityFromDto(req, existing);
        verify(incidentRepository).save(existing);
        verify(incidentMapper).toResponse(saved);

        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void update_debe_fallar_si_no_existe() {
        when(incidentRepository.findById(88L)).thenReturn(Optional.empty());
        IncidentUpdateRequest req = mock(IncidentUpdateRequest.class);

        assertThatThrownBy(() -> service.update(88L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Incident not found");
    }

    // ==========================================================================
    // DELETE
    // ==========================================================================
    @Test
    void delete_debe_eliminar_si_existe() {
        Incident inc = Incident.builder().id(33L).build();
        when(incidentRepository.findById(33L)).thenReturn(Optional.of(inc));

        service.delete(33L);

        verify(incidentRepository).delete(inc);
    }

    @Test
    void delete_debe_fallar_si_no_existe() {
        when(incidentRepository.findById(1000L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(1000L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Incident not found");
    }

    // ==========================================================================
    // DELETE OLDER THAN
    // ==========================================================================
    @Test
    void deleteOlderThan_debe_retorna_cantidad_de_eliminados() {
        when(incidentRepository.deleteOlderThan(any(LocalDateTime.class))).thenReturn(7);

        int count = service.deleteOlderThan(30);

        assertThat(count).isEqualTo(7);
        verify(incidentRepository).deleteOlderThan(any(LocalDateTime.class));
    }

    @Test
    void createDeliveryFailureIncident_debe_crear_incidente_correctamente() {
        Long parcelId = 99L;
        String reason = "OTP incorrecto";

        IncidentResponse expected = mock(IncidentResponse.class);

        // PREVENIR ejecución real del método create()
        doReturn(expected)
                .when(service)
                .create(any(IncidentCreateRequest.class));

        // Ejecutar
        IncidentResponse result = service.createDeliveryFailureIncident(parcelId, reason);

        assertThat(result).isEqualTo(expected);

        // Capturar los argumentos enviados al método create()
        ArgumentCaptor<IncidentCreateRequest> captor =
                ArgumentCaptor.forClass(IncidentCreateRequest.class);

        verify(service).create(captor.capture());

        IncidentCreateRequest sent = captor.getValue();

        assertThat(sent.type()).isEqualTo("PARCEL");
        assertThat(sent.entityId()).isEqualTo(parcelId);
        assertThat(sent.entityType()).isEqualTo("DELIVERY_FAILURE");
        assertThat(sent.note()).isEqualTo(reason);
    }
}
