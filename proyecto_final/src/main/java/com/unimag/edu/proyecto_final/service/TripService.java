package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.TripDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

public interface TripService {

    TripResponse schedule(TripCreateRequest request);

    TripResponse get(Long id);

    Page<TripResponse> list(Long routeId, LocalDate date, Pageable pageable);

    TripResponse updateStatus(Long id, TripUpdateRequest request);

    void openBoarding(Long id);

    void closeBoarding(Long id);

    void markDeparted(Long id);

    void markArrived(Long id);
}
