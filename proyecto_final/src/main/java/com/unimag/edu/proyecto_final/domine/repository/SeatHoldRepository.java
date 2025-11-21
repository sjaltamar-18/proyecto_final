package com.unimag.edu.proyecto_final.domine.repository;

import com.unimag.edu.proyecto_final.domine.entities.SeatHold;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusSeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatHoldRepository extends JpaRepository<SeatHold,Long> {


    List<SeatHold> findByTripIdAndStatus(Long tripId, StatusSeatHold status);

    @Query("""
           SELECT COUNT(s) > 0 FROM SeatHold s
           WHERE s.trip.id = :tripId
           AND s.seatNumber = :seat
           AND s.status = 'HOLD'
           AND s.expirationDate > CURRENT_TIMESTAMP
           """)
    boolean isSeatOnHold(@Param("tripId") Long tripId, @Param("seat") String seat);

    Optional<SeatHold> findByTripIdAndSeatNumberAndStatus(Long tripId, String seatNumber, StatusSeatHold status);

    @Transactional
    @Modifying
    @Query("UPDATE SeatHold s SET s.status = 'EXPIRED' WHERE s.expirationDate < CURRENT_TIMESTAMP AND s.status = 'HOLD'")
    int expireHolds();

    @Transactional
    @Modifying
    @Query("DELETE FROM SeatHold s WHERE s.status = 'EXPIRED' AND s.expirationDate < :limit")
    int deleteExpiredOlderThan(@Param("limit") LocalDateTime limit);

    List<SeatHold> findByUserIdAndStatus(Long userId, StatusSeatHold status);
    @Query("""
    SELECT CASE WHEN COUNT(sh) > 0 THEN TRUE ELSE FALSE END
    FROM SeatHold sh
    WHERE sh.trip.id = :tripId
      AND sh.seatNumber = :seatNumber
      AND sh.status = 'HOLD'
      AND sh.expirationDate > CURRENT_TIMESTAMP
      AND sh.fromStop.stopOrder <= :toStopOrder
      AND sh.toStop.stopOrder   >= :fromStopOrder
    """)
    boolean isSeatOnHoldByTramo(Long tripId,
                                String seatNumber,
                                int fromStopOrder,
                                int toStopOrder);

}