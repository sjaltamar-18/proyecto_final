
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.TripDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TripService {

    TripResponse create(TripCreateRequest request);

    TripResponse get(Long id);

    TripResponse openBoarding(Long tripId);

    TripResponse closeBoarding(Long tripId);

    List<TripResponse> listByRoute(Long routeId, LocalDate date);

    List<TripResponse> listUpcoming();

    List<TripResponse> listBetween(LocalDateTime start, LocalDateTime end);

    TripResponse update(Long id, TripUpdateRequest request);

    TripResponse authorizeDeparture(Long tripId, Long driverId);

    void delete(Long id);
}