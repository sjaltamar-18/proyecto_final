package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.FareRuleDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.FareRule;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.repository.FareRuleRepository;
import com.unimag.edu.proyecto_final.domine.repository.RouteRepository;
import com.unimag.edu.proyecto_final.domine.repository.StopRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.FareRuleMapper;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareRuleServiceImplTest {

    @Mock
    private FareRuleRepository fareRuleRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private StopRepository stopRepository;

    @Mock
    private FareRuleMapper mapper;

    @InjectMocks
    private FareRuleServicelmpl service;

    @BeforeEach
    void setup() {}

    @Test
    void create_debe_crear_fareRule_correctamente() {
        FareRuleCreateRequest req = mock(FareRuleCreateRequest.class);
        when(req.routeId()).thenReturn(1L);
        when(req.fromStopId()).thenReturn(10L);
        when(req.toStopId()).thenReturn(20L);

        Route route = Route.builder().id(1L).build();
        Stop fromStop = Stop.builder().id(10L).build();
        Stop toStop = Stop.builder().id(20L).build();

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(stopRepository.findById(20L)).thenReturn(Optional.of(toStop));
        when(stopRepository.findById(10L)).thenReturn(Optional.of(fromStop));

        FareRule fareRule = FareRule.builder().build();
        when(mapper.toEntity(req)).thenReturn(fareRule);

        FareRule saved = FareRule.builder().id(100L).build();
        when(fareRuleRepository.save(fareRule)).thenReturn(saved);

        FareRuleResponse mappedResp = mock(FareRuleResponse.class);
        when(mapper.toResponse(saved)).thenReturn(mappedResp);

        var result = service.create(req);

        assertThat(result).isNotNull().isEqualTo(mappedResp);
        assertThat(fareRule.getRoute()).isEqualTo(route);
        assertThat(fareRule.getFromStop()).isEqualTo(fromStop);
        assertThat(fareRule.getToStop()).isEqualTo(toStop);

        verify(routeRepository).findById(1L);
        verify(stopRepository).findById(20L);
        verify(stopRepository).findById(10L);
        verify(mapper).toEntity(req);
        verify(fareRuleRepository).save(fareRule);
        verify(mapper).toResponse(saved);
    }

    @Test
    void create_debe_fallar_si_route_no_existe() {
        FareRuleCreateRequest req = mock(FareRuleCreateRequest.class);
        when(req.routeId()).thenReturn(99L);

        when(routeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Route not found");
    }

    @Test
    void create_debe_fallar_si_stop_to_no_existe() {
        FareRuleCreateRequest req = mock(FareRuleCreateRequest.class);
        when(req.routeId()).thenReturn(1L);
        when(req.toStopId()).thenReturn(999L);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(Route.builder().id(1L).build()));
        when(stopRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Stop not found");
    }

    @Test
    void create_debe_fallar_si_stop_from_no_existe() {
        FareRuleCreateRequest req = mock(FareRuleCreateRequest.class);
        when(req.routeId()).thenReturn(1L);
        when(req.toStopId()).thenReturn(20L);
        when(req.fromStopId()).thenReturn(999L);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(Route.builder().id(1L).build()));
        when(stopRepository.findById(20L)).thenReturn(Optional.of(Stop.builder().id(20L).build()));
        when(stopRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Stop not found");
    }

    @Test
    void get_debe_devolver_si_existe() {
        FareRule rule = FareRule.builder().id(5L).build();
        when(fareRuleRepository.findById(5L)).thenReturn(Optional.of(rule));

        FareRuleResponse resp = mock(FareRuleResponse.class);
        when(mapper.toResponse(rule)).thenReturn(resp);

        var result = service.get(5L);

        assertThat(result).isEqualTo(resp);
        verify(fareRuleRepository).findById(5L);
    }

    @Test
    void get_debe_fallar_si_no_existe() {
        when(fareRuleRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(404L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("FareRule not found");
    }

    @Test
    void listByRoute_debe_devolver_lista_mapeada_en_page() {
        Pageable pageable = PageRequest.of(0, 10);

        FareRule r1 = FareRule.builder().id(1L).build();
        FareRule r2 = FareRule.builder().id(2L).build();

        when(fareRuleRepository.findByRoute_Id(1L)).thenReturn(List.of(r1, r2));

        when(mapper.toResponse(any())).thenReturn(mock(FareRuleResponse.class));

        Page<FareRuleResponse> page = service.listByRoute(1L, pageable);

        assertThat(page.getTotalElements()).isEqualTo(2);
        verify(fareRuleRepository).findByRoute_Id(1L);
        verify(mapper, times(2)).toResponse(any());
    }

    @Test
    void update_debe_modificar_y_guardar() {
        FareRule existing = FareRule.builder().id(3L).build();
        when(fareRuleRepository.findById(3L)).thenReturn(Optional.of(existing));

        FareRuleUpdateRequest req = mock(FareRuleUpdateRequest.class);

        FareRule saved = FareRule.builder().id(3L).build();
        when(fareRuleRepository.save(existing)).thenReturn(saved);

        FareRuleResponse mapped = mock(FareRuleResponse.class);
        when(mapper.toResponse(saved)).thenReturn(mapped);

        var result = service.update(3L, req);

        verify(mapper).updateEntityFromDto(req, existing);
        verify(fareRuleRepository).save(existing);
        verify(mapper).toResponse(saved);

        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void update_debe_fallar_si_no_existe() {
        when(fareRuleRepository.findById(7L)).thenReturn(Optional.empty());
        FareRuleUpdateRequest req = mock(FareRuleUpdateRequest.class);

        assertThatThrownBy(() -> service.update(7L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("FareRule not found");
    }


    @Test
    void delete_debe_eliminar_si_existe() {
        FareRule rule = FareRule.builder().id(99L).build();
        when(fareRuleRepository.findById(99L)).thenReturn(Optional.of(rule));

        service.delete(99L);

        verify(fareRuleRepository).delete(rule);
    }

    @Test
    void delete_debe_fallar_si_no_existe() {
        when(fareRuleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("FareRule not found");
    }
}
