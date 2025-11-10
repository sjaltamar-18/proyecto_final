package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.TripDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional

public class TripServicelmpl implements  TripService {
    @Override
    public TripDtos.TripResponse schedule(TripDtos.TripCreateRequest request) {
        return null;
    }

    @Override
    public TripDtos.TripResponse get(Long id) {
        return null;
    }

    @Override
    public Page<TripDtos.TripResponse> list(Long routeId, LocalDate date, Pageable pageable) {
        return null;
    }

    @Override
    public TripDtos.TripResponse updateStatus(Long id, TripDtos.TripUpdateRequest request) {
        return null;
    }

    @Override
    public void openBoarding(Long id) {

    }

    @Override
    public void closeBoarding(Long id) {

    }

    @Override
    public void markDeparted(Long id) {

    }

    @Override
    public void markArrived(Long id) {

    }
}
