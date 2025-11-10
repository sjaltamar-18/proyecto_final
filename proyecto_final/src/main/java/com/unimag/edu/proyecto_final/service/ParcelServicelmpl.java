package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.ParcelDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional

public class ParcelServicelmpl implements  ParcelService{
    @Override
    public ParcelDtos.ParcelResponse create(ParcelDtos.ParcelCreateRequest request) {
        return null;
    }

    @Override
    public ParcelDtos.ParcelResponse get(Long id) {
        return null;
    }

    @Override
    public ParcelDtos.ParcelResponse updateStatus(String code, ParcelDtos.ParcelUpdateRequest request) {
        return null;
    }

    @Override
    public Page<ParcelDtos.ParcelResponse> listInTransit(Pageable pageable) {
        return null;
    }

    @Override
    public Page<ParcelDtos.ParcelResponse> listByUser(Long userId, Pageable pageable) {
        return null;
    }
}
