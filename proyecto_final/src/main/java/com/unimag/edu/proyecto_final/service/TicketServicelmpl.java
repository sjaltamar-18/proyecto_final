package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.TicketDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional

public class TicketServicelmpl implements TicketService {
    @Override
    public TicketDtos.TicketResponse create(TicketDtos.TicketCreateRequest request) {
        return null;
    }

    @Override
    public TicketDtos.TicketResponse get(Long id) {
        return null;
    }

    @Override
    public Page<TicketDtos.TicketResponse> listByUser(Long userId, Pageable pageable) {
        return null;
    }

    @Override
    public TicketDtos.TicketResponse cancel(Long id) {
        return null;
    }

    @Override
    public TicketDtos.TicketResponse markNoShow(Long id) {
        return null;
    }

    @Override
    public double calculatePrice(TicketDtos.TicketUpdateRequest request) {
        return 0;
    }
}
