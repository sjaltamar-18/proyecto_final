
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.SeatHoldDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SeatHoldService {

    SeatHoldResponse create(SeatHoldCreateRequest request);

    SeatHoldResponse get(Long id);

    List<SeatHoldResponse> listActiveByTrip(Long tripId);

    List<SeatHoldResponse> listByUser(Long userId);

    SeatHoldResponse update(Long id, SeatHoldUpdateRequest request);

    int expireHolds();

    int cleanOldExpired();

    void delete(Long id);
}
