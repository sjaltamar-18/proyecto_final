package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.FareRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FareRuleRepository extends JpaRepository<FareRule,Long> {
    //Buscar tarifa base para un tramo específico (from → to)
    @Query("""
           SELECT f FROM FareRule f
           WHERE f.route.id = :routeId
             AND f.fromStop.id = :fromId
             AND f.toStop.id = :toId
           """)
    Optional<FareRule> findFareRuleForSegment(@Param("routeId") Long routeId,
                                                @Param("fromId") Long fromId,
                                                @Param("toId") Long toId);

    // Listar todas las reglas de una ruta (para panel administrativo)
    @Query("""
           SELECT f FROM FareRule f
           WHERE f.route.id = :routeId
           ORDER BY f.fromStop.order, f.toStop.order
           """)
    List<FareRule> findByRouteId(@Param("routeId") Long routeId);

    // Buscar tarifas con precios dinámicos habilitados
    @Query("""
           SELECT f FROM FareRule f
           WHERE f.route.id = :routeId AND f.dynamicPricing = 'ON'
           """)
    List<FareRule> findDynamicPricingRules(@Param("routeId") Long routeId);

    // Buscar tarifas con descuentos configurados (niños, estudiante, etc.)
    @Query("""
           SELECT f FROM FareRule f
           WHERE f.route.id = :routeId
             AND JSON_LENGTH(f.discountPrice) > 0
           """)
    List<FareRule> findWithDiscounts(@Param("routeId") Long routeId);

    // Buscar todos los tramos válidos entre dos paradas de una ruta
    @Query("""
           SELECT f FROM FareRule f
           WHERE f.route.id = :routeId
             AND f.fromStop.order >= :fromOrder
             AND f.toStop.order <= :toOrder
           ORDER BY f.fromStop.order
           """)
    List<FareRule> findSegmentRange(@Param("routeId") Long routeId,
                                    @Param("fromOrder") int fromOrder,
                                    @Param("toOrder") int toOrder);
}
