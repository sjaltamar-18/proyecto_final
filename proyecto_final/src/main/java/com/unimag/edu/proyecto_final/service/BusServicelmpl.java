package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.BusDtos;
import com.unimag.edu.proyecto_final.domine.entities.Bus;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusBus;
import com.unimag.edu.proyecto_final.domine.repository.BusRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.BusMapper;
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
    private final BusRepository busRepository;
    private final BusMapper busMapper;

    @Override
    public BusDtos.BusResponse create(BusDtos.BusCreateRequest request) {
       if (busRepository.existsByPlate(request.plate())){
           throw new NotFoundException("plate already exists");
       }
        Bus bus = busMapper.toEntity(request);
       bus.setStatus(StatusBus.AVAILABLE);
       busRepository.save(bus);

       return busMapper.toResponse(bus);

    }

    @Override
    @Transactional(readOnly = true)
    public BusDtos.BusResponse get(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("bus not found"));
        return busMapper.toResponse(bus);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BusDtos.BusResponse> list(Pageable pageable) {
        return busRepository.findAll(pageable)
                .map(busMapper::toResponse);
    }

    @Override
    public BusDtos.BusResponse update(Long id, BusDtos.BusUpdateRequest request) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("bus not found"));

        busMapper.updateEntityFromDto(request,bus);

        if (request.status() != null){
            bus.setStatus(StatusBus.valueOf(request.status()));
        }
        busRepository.save(bus);
        return busMapper.toResponse(bus);
    }

    @Override
    public void delete(Long id) {
        Bus bus =  busRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("bus not found"));
        busRepository.delete(bus);

    }
}
