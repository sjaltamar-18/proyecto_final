package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.ParcelDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParcelService {

    ParcelResponse create(ParcelCreateRequest request);


    ParcelResponse get(Long id);

    ParcelResponse getByCode(String code);

    Page<ParcelResponse> listByStatus(String status, Pageable pageable);

    ParcelResponse update(Long id, ParcelUpdateRequest request);

    void delete(Long id);
}
