package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.BaggageDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BaggageServicelmpl implements BaggageService {
    @Override
    public BaggageDtos.BaggageResponse register(BaggageDtos.BaggageCreateRequest request) {
        return null;
    }

    @Override
    public BaggageDtos.BaggageResponse get(Long id) {
        return null;
    }

    @Override
    public Page<BaggageDtos.BaggageResponse> listByTicket(Long ticketId, Pageable pageable) {
        return null;
    }

    @Override
    public BaggageDtos.BaggageResponse update(Long id, BaggageDtos.BaggageUpdateRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
