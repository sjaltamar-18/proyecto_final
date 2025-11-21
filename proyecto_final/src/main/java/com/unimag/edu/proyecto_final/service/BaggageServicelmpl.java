package com.unimag.edu.proyecto_final.service;

import com.sun.jdi.request.InvalidRequestStateException;
import com.unimag.edu.proyecto_final.api.dto.BaggageDtos;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusTicket;
import com.unimag.edu.proyecto_final.domine.repository.BaggageRepository;
import com.unimag.edu.proyecto_final.domine.repository.TicketRepository;
import com.unimag.edu.proyecto_final.exception.BadRequestException;
import com.unimag.edu.proyecto_final.exception.InvalidStateException;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.BaggageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BaggageServicelmpl implements BaggageService {

    private final BaggageRepository baggageRepository;
    private final TicketRepository ticketRepository;
    private final BaggageMapper baggageMapper;
    private final ConfiService confiService;

    public BaggageDtos.BaggageResponse register(BaggageDtos.BaggageCreateRequest request) {


        var ticket = ticketRepository.findById(request.ticketId())
                .orElseThrow(() -> new NotFoundException("ticket not found"));


        if (ticket.getStatusTicket() != StatusTicket.SOLD) {
            throw new InvalidStateException("Equipaje solo permitido para tickets vendidos");
        }

        if (request.weightKg() == null || request.weightKg() <= 0) {
            throw new BadRequestException("Peso inválido");
        }

        double limit = confiService.getDouble("BAGGAGE_WEIGHT_LIMIT_KG");
        double feePerKg = confiService.getDouble("BAGGAGE_EXCESS_FEE_PER_KG");// ej: 3000

        // 5. Calcular exceso y fee
        double excess = Math.max(0, request.weightKg() - limit);
        BigDecimal feeCalculated = BigDecimal.valueOf(excess * feePerKg);


        String tag = "BAG-" + ticket.getId() + "-" +
                UUID.randomUUID().toString().substring(0, 6).toUpperCase();


        var baggage = baggageMapper.toEntity(request);


        baggage.setTicket(ticket);
        baggage.setFee(feeCalculated);
        baggage.setTagCode(tag);


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
