package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.BusDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BusServicelmpl  implements BusService
{
    @Override
    public BusDtos.BusResponse create(BusDtos.BusCreateRequest request) {
        return null;
    }

    @Override
    public BusDtos.BusResponse get(Long id) {
        return null;
    }

    @Override
    public Page<BusDtos.BusResponse> list(Pageable pageable) {
        return null;
    }

    @Override
    public BusDtos.BusResponse update(Long id, BusDtos.BusUpdateRequest request) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }
}
