package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Bus;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusBus;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTrip;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BusRepositoryTest extends AbstractRepositoryIT{

    @Autowired
    private BusRepository busRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private RouteRepository routeRepository;


    @Test
    @DisplayName("Debe encontrar un bus por placa")
    void findByPlate() {
        Bus bus = busRepository.save(Bus.builder().
                plate("abc123")
                .capacity(40)
                .status(StatusBus.ACTIVE)
                .amenities("wifi").build());

        Optional<Bus> result = busRepository.findByPlate("abc123");
        assertTrue(result.isPresent());
        assertThat(result.get().getPlate()).isEqualTo("abc123");
        assertThat(result.get().getCapacity()).isEqualTo(40);
    }

    @Test
    @DisplayName("Debe verificar si existe una placa")
    void existsByPlate() {
        busRepository.save(Bus.builder()
                .plate("abc123")
                .capacity(50)
                .status(StatusBus.ACTIVE).build());
        boolean exists = busRepository.existsByPlate("abc123");
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Debe encontrar solo buses disponibles para despacho")
    void findAvailableBuses() {
        busRepository.save(Bus.builder()
                .plate("BUS001")
                .capacity(40)
                .status(StatusBus.AVAILABLE)
                .build());
        busRepository.save(Bus.builder()
                .plate("BUS002")
                .capacity(30)
                .status(StatusBus.INACTIVE)
                .build());

        var result = busRepository.findAvailableBuses();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(StatusBus.AVAILABLE);
        assertThat(result.get(0).getPlate()).isEqualTo("BUS001");

    }

    @Test
    @DisplayName("Debe encontrar buses por estado específico")
    void findByStatus() {
        busRepository.save(Bus.builder()
                .plate("BUS100")
                .capacity(25)
                .status(StatusBus.ACTIVE)
                .build());

        busRepository.save(Bus.builder()
                .plate("BUS200")
                .capacity(35)
                .status(StatusBus.AVAILABLE)
                .build());
        var result = busRepository.findByStatus(StatusBus.ACTIVE);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlate()).isEqualTo("BUS100");
        assertThat(result.get(0).getStatus()).isEqualTo(StatusBus.ACTIVE);
    }

    @Test
    @DisplayName("Debe encontrar buses disponibles con capacidad mínima requerida")
    void findAvailableWithMinCapacity() {
        busRepository.save(Bus.builder()
                .plate("BUS300")
                .capacity(20)
                .status(StatusBus.AVAILABLE)
                .build());

        busRepository.save(Bus.builder()
                .plate("BUS400")
                .capacity(50)
                .status(StatusBus.AVAILABLE)
                .build());

        busRepository.save(Bus.builder()
                .plate("BUS500")
                .capacity(40)
                .status(StatusBus.INACTIVE)
                .build());

        var result = busRepository.findAvailableWithMinCapacity(30);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPlate()).isEqualTo("BUS400");
        assertThat(result.get(0).getCapacity()).isGreaterThanOrEqualTo(30);
        assertThat(result.get(0).getStatus()).isEqualTo(StatusBus.AVAILABLE);
    }

    @Test
    @DisplayName("Encuentra apartir de una comodidad")
    void findByAmenity() {
        busRepository.save(Bus.builder()
                .plate("abc123")
                .capacity(40)
                .status(StatusBus.ACTIVE)
                .amenities("wifi").build());

        List<Bus> result = busRepository.findByAmenity("wifi");
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getPlate()).isEqualTo("abc123");
    }

    @Test
    @DisplayName("Encuentra buses en viajes activos")
    void findBusesInActiveTrips() {
        Route route2 = routeRepository.save(Route.builder().routeCode("ASV1234")
                .routeName("Cartagena-santa marta").build());
        Route route1 = routeRepository.save(Route.builder().routeCode("ASV123")
                .routeName("santa marta-medellin").build());
        Bus activeBus = busRepository.save(Bus.builder()
                .plate("ACT123")
                .capacity(45)
                .status(StatusBus.AVAILABLE)
                .build());

        Bus inactiveBus = busRepository.save(Bus.builder()
                .plate("QWS123")
                .capacity(30)
                .status(StatusBus.AVAILABLE)
                .build());

        tripRepository.save(Trip.builder()
                .bus(activeBus)
                .route(route2)
                .statusTrip(StatusTrip.SCHEDULED)
                .build());

        tripRepository.save(Trip.builder()
                .bus(inactiveBus)
                .route(route1)
                .statusTrip(StatusTrip.ARRIVED)
                .build());

        var result = busRepository.findBusesInActiveTrips();

        assertThat(result)
                .hasSize(1)
                .extracting(Bus::getPlate)
                .containsExactly("ACT123");


    }

    @Test
    @DisplayName("Debe contar buses agrupados por estado")
    void countByStatus() {
        busRepository.save(Bus.builder()
                .plate("CNT001")
                .status(StatusBus.AVAILABLE)
                .capacity(40)
                .build());

        busRepository.save(Bus.builder()
                .plate("CNT002")
                .status(StatusBus.INACTIVE)
                .capacity(30)
                .build());

        busRepository.save(Bus.builder()
                .plate("CNT003")
                .status(StatusBus.AVAILABLE)
                .capacity(25)
                .build());

        var result = busRepository.countByStatus();

        var availableCount = result.stream()
                .filter(row -> row[0].equals(StatusBus.AVAILABLE))
                .map(row -> (Long) row[1])
                .findFirst()
                .orElse(0L);

        var inactiveCount = result.stream()
                .filter(row -> row[0].equals(StatusBus.INACTIVE))
                .map(row -> (Long) row[1])
                .findFirst()
                .orElse(0L);

        assertThat(availableCount).isEqualTo(2L);
        assertThat(inactiveCount).isEqualTo(1L);
    }
}