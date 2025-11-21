package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.FareRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FareRuleRepository extends JpaRepository<FareRule,Long> {

    @Query("""
           SELECT f FROM FareRule f
           WHERE f.route.id = :routeId
             AND f.fromStop.id = :fromId
             AND f.toStop.id = :toId
           """)
    Optional<FareRule> findFareRuleForSegment(@Param("routeId") Long routeId,
                                              @Param("fromId") Long fromId,
                                              @Param("toId") Long toId);


    @Query("""
           SELECT f FROM FareRule f
           WHERE f.route.id = :routeId
           ORDER BY f.fromStop.stopOrder, f.toStop.stopOrder
           """)
    List<FareRule> findByRoute_Id(@Param("routeId") Long routeId);


    @Query("""
           SELECT f FROM FareRule f
           WHERE f.route.id = :routeId AND f.dynamicPricing = 'ON'
           """)
    List<FareRule> findDynamicPricingRules(@Param("routeId") Long routeId);


    @Query(value = """
   SELECT * FROM fare_rules f
   WHERE f.route_id = :routeId
     AND f.discount_price <> '{}'
   """, nativeQuery = true)
    List<FareRule> findWithDiscounts(@Param("routeId") Long routeId);


    @Query("""
           SELECT f FROM FareRule f
           WHERE f.route.id = :routeId
             AND f.fromStop.stopOrder >= :fromOrder
             AND f.toStop.stopOrder <= :toOrder
           ORDER BY f.fromStop.stopOrder
           """)
    List<FareRule> findSegmentRange(@Param("routeId") Long routeId,
                                    @Param("fromOrder") int fromOrder,
                                    @Param("toOrder") int toOrder);
}