package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.Parcel;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusParcel;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusSeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParcelRepository extends JpaRepository<Parcel,Long> {
// Busca por codigo para el rastreo o entrega
    Optional<Parcel> findByCode(String code);
// valida la existencia del codigo, con el fin de evitar cualquier duplicado
    boolean existsByCode(String code);
//Busca todas las encomiendas en tránsito entre dos paradas
@Query("SELECT p FROM Parcel p " +
        "WHERE p.fromStop.id = :fromId " +
        "AND p.toStop.id = :toId " +
        "AND p.statusParcel = :status")
List<Parcel> findInTransitBetweenStops(@Param("fromId") Long fromId,
                                       @Param("toId") Long toId,
                                       @Param("status") StatusParcel status);

// encomiendas por estado (_8()
    List<Parcel> findByStatusParcel(StatusParcel status);
// encomiendas pendientes por sincronizar
    @Query("SELECT p FROM Parcel p WHERE p.statusParcel = :status")
    List<Parcel> findPendingSync();
}
