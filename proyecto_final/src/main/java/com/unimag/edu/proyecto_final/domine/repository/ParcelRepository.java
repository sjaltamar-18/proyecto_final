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

    Optional<Parcel> findByCode(String code);

    boolean existsByCode(String code);

@Query("SELECT p FROM Parcel p " +
        "WHERE p.fromStop.id = :fromId " +
        "AND p.toStop.id = :toId " +
        "AND p.statusParcel = :status")
List<Parcel> findInTransitBetweenStops(@Param("fromId") Long fromId,
                                       @Param("toId") Long toId,
                                       @Param("status") StatusParcel status);


    List<Parcel> findByStatusParcel(StatusParcel status);

    @Query("SELECT p FROM Parcel p WHERE p.statusParcel = :status")
    List<Parcel> findPendingSync();
}
