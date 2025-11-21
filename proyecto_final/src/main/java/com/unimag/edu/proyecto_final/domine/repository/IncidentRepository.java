package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Incident;
import com.unimag.edu.proyecto_final.domine.entities.enumera.EntityType;
import com.unimag.edu.proyecto_final.domine.entities.enumera.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident,Long> {

    List<Incident> findByEntityTypeAndEntityId(EntityType entityType, Long entityId);


    List<Incident> findByType(Type type);


    @Query("""
           SELECT i FROM Incident i
           WHERE i.creationDate BETWEEN :start AND :end
           ORDER BY i.creationDate DESC
           """)
    List<Incident> findBetweenDates(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end);


    @Query("""
           SELECT COUNT(i) FROM Incident i
           WHERE i.type = :type AND i.creationDate BETWEEN :start AND :end
           """)
    Long countByTypeBetween(@Param("type") Type type,
                            @Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end);

    @Query("""
           SELECT i FROM Incident i
           WHERE i.entityType = 'TRIP' AND i.entityId = :tripId AND i.type = :type
           """)
    List<Incident> findTripIncidentsByType(@Param("tripId") Long tripId,
                                           @Param("type") Type type);


    @Transactional
    @Modifying
    @Query("DELETE FROM Incident i WHERE i.creationDate < :threshold")
    int deleteOlderThan(@Param("threshold") LocalDateTime threshold);
}
