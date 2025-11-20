package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.BaggageDtos;
import com.unimag.edu.proyecto_final.domine.repository.BaggageRepository;
import com.unimag.edu.proyecto_final.domine.repository.TicketRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.BaggageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BaggageServicelmpl implements BaggageService {

    private final BaggageRepository baggageRepository;
    private final TicketRepository ticketRepository;
    private final BaggageMapper baggageMapper;

    @Override
    @Transactional
    public BaggageDtos.BaggageResponse register(BaggageDtos.BaggageCreateRequest request) {
        var ticket = ticketRepository.findById(request.ticketId())
                .orElseThrow(() -> new NotFoundException("ticket not found" ));
        var baggage = baggageMapper.toEntity(request);
        baggage.setTicket(ticket);
        baggageRepository.save(baggage);
        return baggageMapper.toResponse(baggage);
    }

    @Override
    public BaggageDtos.BaggageResponse get(Long id) {
        var baggage = baggageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("baggage not found" ));
        return baggageMapper.toResponse(baggage);
    }

    @Override
    public Page<BaggageDtos.BaggageResponse> listByTicket(Long ticketId, Pageable pageable) {
        return baggageRepository.findAll(pageable)
                .map(baggageMapper::toResponse);
    }

    @Override
    @Transactional
    public BaggageDtos.BaggageResponse update(Long id, BaggageDtos.BaggageUpdateRequest request) {
        var baggage = baggageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("baggage not found" ));
        baggageMapper.updateEntityFromDto(request,baggage);
        baggageRepository.save(baggage);
        return baggageMapper.toResponse(baggage);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        var baggage = baggageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("baggage not found" ));
        baggageRepository.delete(baggage);


    }
}
