package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Parcel;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusParcel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ParcelRepositoryTest extends AbstractRepositoryIT{

    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private StopRepository stopRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Test
    @DisplayName("Debe encontrar una encomienda por su código")
    void findByCode() {
        Parcel parcel = parcelRepository.save(Parcel.builder()
                .code("PKG001")
                .statusParcel(StatusParcel.CREATED)
                .build());

        Optional<Parcel> result = parcelRepository.findByCode("PKG001");

        assertThat(result).isPresent();
        assertThat(result.get().getCode()).isEqualTo("PKG001");
    }

    @Test
    @DisplayName("Debe verificar si una encomienda ya existe por código")
    void existsByCode() {
        parcelRepository.save(Parcel.builder()
                .code("PKG002")
                .statusParcel(StatusParcel.CREATED)
                .build());

        boolean exists = parcelRepository.existsByCode("PKG002");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe encontrar encomiendas en tránsito entre dos paradas")
    void findInTransitBetweenStops() {
        Route route = routeRepository.save(Route.builder().routeCode("QWEA123")
                .routeName("santa marta-valledupar").build());
        Stop from = stopRepository.save(Stop.builder().stopName("Origen").route(route).build());
        Stop to = stopRepository.save(Stop.builder().stopName("Destino").route(route).build());

        parcelRepository.save(Parcel.builder()
                .code("PKG003")
                .fromStop(from)
                .toStop(to)
                .statusParcel(StatusParcel.IN_PROGRESS)
                .build());

        List<Parcel> result = parcelRepository.findInTransitBetweenStops(from.getId(), to.getId(),StatusParcel.IN_PROGRESS);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatusParcel()).isEqualTo(StatusParcel.IN_PROGRESS);
    }

    @Test
    @DisplayName("Debe listar las encomiendas por estado")
    void findByStatusParcel() {
        parcelRepository.save(Parcel.builder()
                .code("PKG004")
                .statusParcel(StatusParcel.COMPLETED)
                .build());

        List<Parcel> result = parcelRepository.findByStatusParcel(StatusParcel.COMPLETED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatusParcel()).isEqualTo(StatusParcel.COMPLETED);
    }

    @Test
    @DisplayName("Debe encontrar las encomiendas pendientes por sincronizar")
    void findPendingSync() {
        parcelRepository.save(Parcel.builder()
                .code("PKG005")
                .statusParcel(StatusParcel.CREATED)
                .build());

        parcelRepository.save(Parcel.builder()
                .code("PKG006")
                .statusParcel(StatusParcel.COMPLETED)
                .build());

        List<Parcel> result = parcelRepository.findByStatusParcel(StatusParcel.CREATED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("PKG005");
    }
}