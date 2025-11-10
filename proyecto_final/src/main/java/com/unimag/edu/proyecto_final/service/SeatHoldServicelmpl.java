package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.SeatHoldDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional

public class SeatHoldServicelmpl implements  SeatHoldService {
    @Override
    public SeatHoldDtos.SeatHoldResponse hold(SeatHoldDtos.SeatHoldCreateRequest request) {
        return null;
    }

    @Override
    public SeatHoldDtos.SeatHoldResponse get(Long id) {
        return null;
    }

    @Override
    public Page<SeatHoldDtos.SeatHoldResponse> listByTrip(Long tripId, Pageable pageable) {
        return null;
    }

    @Override
    public void releaseExpired() {

    }

    @Override
    public void expire(Long id) {

    }
}
