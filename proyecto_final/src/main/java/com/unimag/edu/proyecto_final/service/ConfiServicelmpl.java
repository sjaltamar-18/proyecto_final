package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.ConfiDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConfiServicelmpl implements ConfiService {
    @Override
    public ConfiDtos.ConfigResponse create(ConfiDtos.ConfigCreateRequest request) {
        return null;
    }

    @Override
    public ConfiDtos.ConfigResponse get(String key) {
        return null;
    }

    @Override
    public Page<ConfiDtos.ConfigResponse> list(Pageable pageable) {
        return null;
    }

    @Override
    public ConfiDtos.ConfigResponse update(String key, ConfiDtos.ConfigUpdateRequest request) {
        return null;
    }

    @Override
    public void delete(String key) {

    }
}
