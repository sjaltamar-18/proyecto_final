package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.FareRule;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.entities.enumera.DynamicPricing;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FareRuleRepositoryTest extends  AbstractRepositoryIT {
    @Autowired
    private FareRuleRepository fareRuleRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private StopRepository stopRepository;

    @Test
    @DisplayName("Debe encontrar una tarifa para un tramo específico")
    void findFareRuleForSegment() {
        Route route = routeRepository.save(Route.builder().routeName("Ruta 1")
                .routeCode("ACD345").build());
        Stop from = stopRepository.save(Stop.builder().stopName("Origen")
                .route(route).stopOrder(1).build());
        Stop to = stopRepository.save(Stop.builder().stopName("Destino")
                .route(route).stopOrder(2).build());

        FareRule fareRule = fareRuleRepository.save(FareRule.builder()
                .route(route)
                .fromStop(from)
                .toStop(to)
                .basePrice(5000.0)
                .build());

        Optional<FareRule> result = fareRuleRepository.findFareRuleForSegment(
                route.getId(), from.getId(), to.getId()
        );

        assertThat(result).isPresent();
        assertThat(result.get().getBasePrice()).isEqualTo(5000);
    }

    @Test
    @DisplayName("Debe listar todas las tarifas de una ruta")
    void findByRoute_Id() {
        Route route = routeRepository.save(Route.builder().routeName("Ruta Central")
                .routeCode("QSR242").build());
        Stop stop1 = stopRepository.save(Stop.builder().stopName("Origen").stopOrder(1).route(route).build());
        Stop stop2 = stopRepository.save(Stop.builder().stopName("Destino").stopOrder(2).route(route).build());

        fareRuleRepository.save(FareRule.builder()
                .route(route)
                .fromStop(stop1)
                .toStop(stop2)
                .basePrice(100.0)
                .discountPrice("{}")
                .build());

        fareRuleRepository.save(FareRule.builder()
                .route(route)
                .fromStop(stop1)
                .toStop(stop2)
                .basePrice(120.0)
                .discountPrice("{}")
                .build());

        List<FareRule> result = fareRuleRepository.findByRoute_Id(route.getId());

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Debe listar las tarifas con precios dinámicos activos")
    void findDynamicPricingRules() {
        Route route = routeRepository.save(Route.builder().routeName("Ruta 99")
                .routeCode("QAS124").build());

        fareRuleRepository.save(FareRule.builder().route(route).dynamicPricing(DynamicPricing.ON).build());
        fareRuleRepository.save(FareRule.builder().route(route).dynamicPricing(DynamicPricing.OFF).build());

        List<FareRule> result = fareRuleRepository.findDynamicPricingRules(route.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDynamicPricing()).isEqualTo(DynamicPricing.ON);
    }

    @Test
    @DisplayName("Debe encontrar tarifas con descuentos configurados")
    void findWithDiscounts() {
        Route route = routeRepository.save(
                Route.builder().routeName("Ruta 88").routeCode("QWE341").build()
        );
        FareRule fare1 = FareRule.builder()
                .route(route)
                .basePrice(5000.0)
                .discountPrice("{\"student\":0.5}")
                .build();

        FareRule fare2 = FareRule.builder()
                .route(route)

                .basePrice(5500.0)
                .discountPrice("{}")
                .build();

        fareRuleRepository.save(fare1);
        fareRuleRepository.save(fare2);
    }

    @Test
    @DisplayName("Debe listar los tramos válidos entre dos paradas")
    void findSegmentRange() {
        Route route = routeRepository.save(Route.builder().routeName("Ruta 10")
                .routeCode("WER123").build());

        Stop stop1 = stopRepository.save(Stop.builder().stopName("A").stopOrder(1).route(route).build());
        Stop stop2 = stopRepository.save(Stop.builder().stopName("B").stopOrder(2).route(route).build());
        Stop stop3 = stopRepository.save(Stop.builder().stopName("C").stopOrder(3).route(route).build());

        fareRuleRepository.save(FareRule.builder().route(route).fromStop(stop1).toStop(stop2).build());
        fareRuleRepository.save(FareRule.builder().route(route).fromStop(stop2).toStop(stop3).build());

        List<FareRule> result = fareRuleRepository.findSegmentRange(route.getId(), 1, 3);

        assertThat(result).hasSize(2);
    }
}