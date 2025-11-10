package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.SeatHoldDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SeatHoldService {

    SeatHoldResponse hold(SeatHoldCreateRequest request);

    SeatHoldResponse get(Long id);

    Page<SeatHoldResponse> listByTrip(Long tripId, Pageable pageable);

    void releaseExpired();

    void expire(Long id);
}
