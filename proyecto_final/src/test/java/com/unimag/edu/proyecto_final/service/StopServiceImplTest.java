package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.StopDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.repository.RouteRepository;
import com.unimag.edu.proyecto_final.domine.repository.StopRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.StopMapper;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class StopServicelmplTest {

    @Mock
    private StopRepository stopRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private StopMapper stopMapper;

    @InjectMocks
    private StopServicelmpl service;

    @BeforeEach
    void setUp() {}


    @Test
    void create_debe_crear_stop_correctamente() {
        Long routeId = 10L;
        StopCreateRequest req = mock(StopCreateRequest.class);

        when(req.name()).thenReturn("Terminal Norte");
        when(req.order()).thenReturn(1);
        when(req.lat()).thenReturn(10.123);
        when(req.lng()).thenReturn(-74.321);

        Route route = Route.builder().id(routeId).build();
        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));

        when(stopRepository.findByRouteAndName(routeId, "Terminal Norte"))
                .thenReturn(Optional.empty());

        Stop stop = new Stop();
        when(stopMapper.toEntity(req)).thenReturn(stop);

        Stop saved = Stop.builder().id(100L).build();
        when(stopRepository.save(stop)).thenReturn(saved);

        StopResponse mapped = mock(StopResponse.class);
        when(stopMapper.toResponse(saved)).thenReturn(mapped);

        var result = service.create(routeId, req);

        assertThat(result).isEqualTo(mapped);

        verify(routeRepository).findById(routeId);
        verify(stopRepository).findByRouteAndName(routeId, "Terminal Norte");
        verify(stopMapper).toEntity(req);
        verify(stopRepository).save(stop);
    }

    @Test
    void create_debe_fallar_si_route_no_existe() {
        when(routeRepository.findById(99L)).thenReturn(Optional.empty());

        StopCreateRequest req = mock(StopCreateRequest.class);

        assertThatThrownBy(() -> service.create(99L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("route not found");
    }

    @Test
    void create_debe_fallar_si_stop_ya_existe_en_ruta() {
        Long routeId = 5L;
        StopCreateRequest req = mock(StopCreateRequest.class);
        when(req.name()).thenReturn("Centro");

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(new Route()));
        when(stopRepository.findByRouteAndName(routeId, "Centro"))
                .thenReturn(Optional.of(new Stop()));

        assertThatThrownBy(() -> service.create(routeId, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }


    @Test
    void get_debe_retornar_stop_si_existe() {
        Stop stop = Stop.builder().id(20L).build();
        when(stopRepository.findById(20L)).thenReturn(Optional.of(stop));

        StopResponse mapped = mock(StopResponse.class);
        when(stopMapper.toResponse(stop)).thenReturn(mapped);

        var result = service.get(20L);

        assertThat(result).isEqualTo(mapped);
        verify(stopRepository).findById(20L);
    }

    @Test
    void get_debe_fallar_si_no_existe() {
        when(stopRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(404L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("stop not found");
    }

    @Test
    void listByRoute_debe_retornar_lista_mapeada() {
        Stop a = Stop.builder().id(1L).build();
        Stop b = Stop.builder().id(2L).build();

        when(stopRepository.findByRouteIdOrderByStopOrderAsc(10L))
                .thenReturn(List.of(a, b));

        when(stopMapper.toResponse(a)).thenReturn(mock(StopResponse.class));
        when(stopMapper.toResponse(b)).thenReturn(mock(StopResponse.class));

        var result = service.listByRoute(10L);

        assertThat(result).hasSize(2);
        verify(stopRepository).findByRouteIdOrderByStopOrderAsc(10L);
    }


    @Test
    void update_debe_actualizar_y_guardar() {
        Stop stop = Stop.builder().id(30L).build();
        when(stopRepository.findById(30L)).thenReturn(Optional.of(stop));

        StopUpdateRequest req = mock(StopUpdateRequest.class);

        Stop updated = Stop.builder().id(30L).build();
        when(stopRepository.save(stop)).thenReturn(updated);

        StopResponse mapped = mock(StopResponse.class);
        when(stopMapper.toResponse(updated)).thenReturn(mapped);

        var result = service.update(30L, req);

        verify(stopMapper).updateEntityFromDto(req, stop);
        verify(stopRepository).save(stop);

        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void update_debe_fallar_si_no_existe() {
        when(stopRepository.findById(999L)).thenReturn(Optional.empty());
        StopUpdateRequest req = mock(StopUpdateRequest.class);

        assertThatThrownBy(() -> service.update(999L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("stop not found");
    }

    @Test
    void delete_debe_eliminar_si_existe() {
        Stop stop = Stop.builder().id(44L).build();
        when(stopRepository.findById(44L)).thenReturn(Optional.of(stop));

        service.delete(44L);

        verify(stopRepository).delete(stop);
    }

    @Test
    void delete_debe_fallar_si_no_existe() {
        when(stopRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(77L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("stop not found");
    }
}
