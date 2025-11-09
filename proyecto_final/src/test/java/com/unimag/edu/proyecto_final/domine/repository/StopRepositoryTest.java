package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StopRepositoryTest extends AbstractRepositoryIT {

    @Autowired
    private StopRepository stopRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Test
    @DisplayName("Debe listar todas las paradas de una ruta en orden")
    void findByRouteIdOrderByStopOrderAsc() {
        Route route = routeRepository.save(Route.builder()
                .routeName("RutaNombre")
                .routeCode("RT001")
                .originName("Ciudad A")
                .destinationName("Ciudad B")
                .build());

        stopRepository.save(Stop.builder()
                .route(route)
                .stopName("Parada 1")
                .stopOrder(1)
                .build());

        stopRepository.save(Stop.builder()
                .route(route)
                .stopName("Parada 2")
                .stopOrder(2)
                .build());

        List<Stop> stops = stopRepository.findByRouteIdOrderByStopOrderAsc(route.getId());

        assertThat(stops).hasSize(2);
        assertThat(stops.get(0).getStopName()).isEqualTo("Parada 1");
    }

    @Test
    @DisplayName("Debe encontrar una parada específica por nombre dentro de una ruta")
    void findByRouteAndName() {
        Route route = routeRepository.save(Route.builder()
                .routeName("RutaNombre")
                .routeCode("RT002")
                .originName("Ciudad C")
                .destinationName("Ciudad D")
                .build());

        stopRepository.save(Stop.builder()
                .route(route)
                .stopName("Centro")
                .stopOrder(1)
                .build());

        Optional<Stop> result = stopRepository.findByRouteAndName(route.getId(), "centro");

        assertThat(result).isPresent();
        assertThat(result.get().getStopName()).isEqualTo("Centro");
    }

    @Test
    @DisplayName("Debe encontrar la parada anterior y siguiente correctamente")
    void findPreviousStop() {
        Route route = routeRepository.save(Route.builder()
                .routeName("RutaNombre")
                .routeCode("RT003").build());

        Stop s1 = stopRepository.save(Stop.builder().route(route).stopName("A").stopOrder(1).build());
        Stop s2 = stopRepository.save(Stop.builder().route(route).stopName("B").stopOrder(2).build());


        Optional<Stop> prev = stopRepository.findPreviousStop(route.getId(), s2.getStopOrder());


        assertThat(prev).isPresent();
        assertThat(prev.get().getStopName()).isEqualTo("A");

    }

    @Test
    void findNextStop() {
        Route route = routeRepository.save(Route.builder()
                .routeName("RutaNombre")
                .routeCode("RT003").build());


        Stop s2 = stopRepository.save(Stop.builder().route(route).stopName("B").stopOrder(2).build());
        Stop s3 = stopRepository.save(Stop.builder().route(route).stopName("C").stopOrder(3).build());

        Optional<Stop> next = stopRepository.findNextStop(route.getId(), s2.getStopOrder());

        assertThat(next).isPresent();
        assertThat(next.get().getStopName()).isEqualTo("C");
    }

    @Test
    @DisplayName("Debe devolver paradas consecutivas")
    void findConsecutiveSegments() {
        Route route = routeRepository.save(Route.builder()
                .routeName("RutaNombre")
                .routeCode("RT004").build());

        stopRepository.save(Stop.builder().route(route).stopName("X").stopOrder(1).build());
        stopRepository.save(Stop.builder().route(route).stopName("Y").stopOrder(2).build());
        stopRepository.save(Stop.builder().route(route).stopName("Z").stopOrder(3).build());

        List<Object[]> segments = stopRepository.findConsecutiveSegments(route.getId());

        assertThat(segments).hasSize(2);
        assertThat(segments.get(0)[1]).isEqualTo("X");
        assertThat(segments.get(0)[3]).isEqualTo("Y");
    }
}