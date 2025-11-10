package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RouteRepositoryTest  extends AbstractRepositoryIT{
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private StopRepository stopRepository;

    @Test
    @DisplayName("Debe buscar rutas por nombre de ciudad en origen o destino")
    void searchByCity() {
        routeRepository.save(Route.builder()
                .routeName("NombreRuta")
                .routeCode("R001")
                .originName("Bogotá")
                .destinationName("Medellín")
                .build());

        List<Route> result = routeRepository.searchByCity("Bogotá");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDestinationName()).isEqualTo("Medellín");
        assertThat(result.get(0).getOriginName()).isEqualTo("Bogotá");
    }

    @Test
    @DisplayName("Debe encontrar una ruta por su código")
    void findByRouteCode() {
        routeRepository.save(Route.builder()
                .routeName("NombreRuta")
                .routeCode("R002")
                .originName("Cali")
                .destinationName("Popayán")
                .build());

        Optional<Route> result = routeRepository.findByRouteCode("R002");

        assertThat(result).isPresent();
        assertThat(result.get().getOriginName()).isEqualTo("Cali");
    }

    @Test@DisplayName("Debe encontrar ruta exacta entre origen y destino")
    void findByOriginAndDestination() {
        routeRepository.save(Route.builder()
                .routeName("NombreRuta")
                .routeCode("R003")
                .originName("Santa Marta")
                .destinationName("Barranquilla")
                .build());

        Optional<Route> result = routeRepository.findByOriginAndDestination("Santa Marta", "Barranquilla");

        assertThat(result).isPresent();
        assertThat(result.get().getRouteCode()).isEqualTo("R003");
    }

    @Test
    @DisplayName("Debe listar rutas con sus paradas ")
    void findAllWithStops() {

        Route route = Route.builder()
                .routeName("NombreRuta")
                .routeCode("R004")
                .originName("Cartagena")
                .destinationName("Sincelejo")
                .build();
        route = routeRepository.save(route);


        Stop stop = Stop.builder()
                .stopName("Arjona")
                .route(route)
                .stopOrder(1)
                .build();
        stopRepository.save(stop);


        route.getStops().add(stop);


        List<Route> result = routeRepository.findAllWithStops();


        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getStops()).isNotEmpty();
    }

    @Test
    @DisplayName("Debe encontrar rutas que tengan una ciudad como parada intermedia")
    void findByIntermediateStop() {
        Route route = routeRepository.save(Route.builder()
                .routeName("NombreRuta")
                .routeCode("R005")
                .originName("Cúcuta")
                .destinationName("Pamplona")
                .build());

        stopRepository.save(Stop.builder()
                .stopName("Chinácota")
                .route(route)
                .stopOrder(1)
                .build());

        List<Route> result = routeRepository.findByIntermediateStop("Chinácota");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRouteCode()).isEqualTo("R005");
    }
}