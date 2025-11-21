package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.RouteDtos.*;
import com.unimag.edu.proyecto_final.api.dto.StopDtos;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.repository.RouteRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.RouteMapper;


import com.unimag.edu.proyecto_final.service.mappers.StopMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.*;

import java.util.Optional;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class RouteServicelmplTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteMapper routeMapper;

    @Mock
    private StopMapper stopMapper;


    @InjectMocks
    private RouteServicelmpl service;

    @BeforeEach
    void setup() {}


    @Test
    void create_debe_crear_route_correctamente() {
        RouteCreateRequest req = mock(RouteCreateRequest.class);

        when(req.code()).thenReturn("R100");
        when(req.name()).thenReturn("Ruta Caribe");
        when(req.origin()).thenReturn("Barranquilla");
        when(req.destination()).thenReturn("Cartagena");
        when(req.distanceKm()).thenReturn(120.0);
        when(req.durationMin()).thenReturn(180);

        when(routeRepository.findByRouteCode("R100")).thenReturn(Optional.empty());
        when(routeRepository.findByOriginAndDestination("Barranquilla", "Cartagena"))
                .thenReturn(Optional.empty());

        Route route = new Route();
        when(routeMapper.toEntity(req)).thenReturn(route);

        Route saved = Route.builder().id(10L).build();
        when(routeRepository.save(route)).thenReturn(saved);

        RouteResponse mapped = mock(RouteResponse.class);
        when(routeMapper.toResponse(saved)).thenReturn(mapped);

        var result = service.create(req);

        assertThat(result).isEqualTo(mapped);

        verify(routeRepository).findByRouteCode("R100");
        verify(routeRepository).findByOriginAndDestination("Barranquilla", "Cartagena");
        verify(routeMapper).toEntity(req);
        verify(routeRepository).save(route);
    }

    @Test
    void create_debe_fallar_si_code_existe() {
        RouteCreateRequest req = mock(RouteCreateRequest.class);
        when(req.code()).thenReturn("R100");

        when(routeRepository.findByRouteCode("R100")).thenReturn(Optional.of(new Route()));

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Route already exists");
    }

    @Test
    void create_debe_fallar_si_origin_destination_existen() {
        RouteCreateRequest req = mock(RouteCreateRequest.class);
        when(req.code()).thenReturn("R100");
        when(req.origin()).thenReturn("AAA");
        when(req.destination()).thenReturn("BBB");

        when(routeRepository.findByRouteCode("R100")).thenReturn(Optional.empty());
        when(routeRepository.findByOriginAndDestination("AAA", "BBB"))
                .thenReturn(Optional.of(new Route()));

        assertThatThrownBy(() -> service.create(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Route already exists");
    }

    @Test
    void get_debe_retornar_route_si_existe() {
        Route route = Route.builder().id(5L).build();
        when(routeRepository.findById(5L)).thenReturn(Optional.of(route));

        RouteResponse mapped = mock(RouteResponse.class);
        when(routeMapper.toResponse(route)).thenReturn(mapped);

        var result = service.get(5L);

        assertThat(result).isEqualTo(mapped);
        verify(routeRepository).findById(5L);
    }

    @Test
    void get_debe_fallar_si_no_existe() {
        when(routeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("route not found");
    }

    @Test
    void list_debe_retornar_page_correctamente() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Route> page = new PageImpl<>(List.of(new Route()));
        when(routeRepository.findAll(pageable)).thenReturn(page);
        when(routeMapper.toResponse(any())).thenReturn(mock(RouteResponse.class));

        var result = service.list(pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(routeRepository).findAll(pageable);
    }

    @Test
    void searchByCity_debe_buscar_en_rutas_principales() {
        Route r1 = Route.builder().id(1L).build();
        when(routeRepository.searchByCity("Cali")).thenReturn(List.of(r1));

        when(routeMapper.toResponse(r1)).thenReturn(mock(RouteResponse.class));

        var result = service.searchByCity("Cali");

        assertThat(result).hasSize(1);
        verify(routeRepository).searchByCity("Cali");
        verify(routeMapper).toResponse(r1);
    }

    @Test
    void searchByCity_debe_buscar_intermediate_si_principal_vacio() {
        Route r = Route.builder().id(2L).build();
        when(routeRepository.searchByCity("Montería")).thenReturn(List.of());
        when(routeRepository.findByIntermediateStop("Montería")).thenReturn(List.of(r));

        when(routeMapper.toResponse(r)).thenReturn(mock(RouteResponse.class));

        var result = service.searchByCity("Montería");

        assertThat(result).hasSize(1);
        verify(routeRepository).searchByCity("Montería");
        verify(routeRepository).findByIntermediateStop("Montería");
    }

    @Test
    void update_debe_actualizar_route_correctamente() {
        Route route = Route.builder().id(50L).build();
        when(routeRepository.findById(50L)).thenReturn(Optional.of(route));

        RouteUpdateRequest req = mock(RouteUpdateRequest.class);
        when(req.name()).thenReturn("Nueva Ruta");
        when(req.origin()).thenReturn("Nuevo Origen");
        when(req.destination()).thenReturn("Nuevo Destino");
        when(req.distanceKm()).thenReturn(300.0);
        when(req.durationMin()).thenReturn(240);

        Route saved = Route.builder().id(50L).build();
        when(routeRepository.save(route)).thenReturn(saved);

        RouteResponse mapped = mock(RouteResponse.class);
        when(routeMapper.toResponse(saved)).thenReturn(mapped);

        var result = service.update(50L, req);

        verify(routeMapper).updateEntityFromDto(req, route);
        verify(routeRepository).save(route);

        assertThat(route.getRouteName()).isEqualTo("Nueva Ruta");
        assertThat(route.getOriginName()).isEqualTo("Nuevo Origen");
        assertThat(route.getDestinationName()).isEqualTo("Nuevo Destino");
        assertThat(route.getDistance()).isEqualTo(300.0);
        assertThat(route.getTime()).isEqualTo(240);

        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void update_debe_fallar_si_no_existe() {
        when(routeRepository.findById(77L)).thenReturn(Optional.empty());
        RouteUpdateRequest req = mock(RouteUpdateRequest.class);

        assertThatThrownBy(() -> service.update(77L, req))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("route not found");
    }

    @Test
    void delete_debe_eliminar_si_existe() {
        Route route = Route.builder().id(88L).build();
        when(routeRepository.findById(88L)).thenReturn(Optional.of(route));

        service.delete(88L);

        verify(routeRepository).delete(route);
    }

    @Test
    void delete_debe_fallar_si_no_existe() {
        when(routeRepository.findById(888L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(888L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("route not found");
    }
    @Test
    void getStopsByRoute_debe_retornar_stops_en_orden_correcto() {

        Long routeId = 1L;

        Stop stop1 = new Stop();
        stop1.setId(10L);
        stop1.setStopOrder(2);

        Stop stop2 = new Stop();
        stop2.setId(20L);
        stop2.setStopOrder(1);

        Set<Stop> stops = Set.of(stop1, stop2);

        Route route = new Route();
        route.setId(routeId);
        route.setStops(stops);

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));


        StopDtos.StopResponse dto2 = new StopDtos.StopResponse(20L, routeId, "A", 1, 1.0, 1.0);
        StopDtos.StopResponse dto1 = new StopDtos.StopResponse(10L, routeId, "B", 2, 2.0, 2.0);

        when(stopMapper.toResponse(stop2)).thenReturn(dto2); // orden 1
        when(stopMapper.toResponse(stop1)).thenReturn(dto1); // orden 2


        List<StopDtos.StopResponse> result = service.getStopsByRoute(routeId);


        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(20L); // primero el stopOrder = 1
        assertThat(result.get(1).id()).isEqualTo(10L); // luego el stopOrder = 2
    }

    @Test
    void getStopsByRoute_debe_lanzar_excepcion_si_route_no_existe() {

        Long routeId = 999L;
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());


        assertThatThrownBy(() -> service.getStopsByRoute(routeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Route not found");
    }
}