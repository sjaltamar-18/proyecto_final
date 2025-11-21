

package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.ParcelDtos;
import com.unimag.edu.proyecto_final.domine.entities.Parcel;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.entities.enumera.StatusParcel;
import com.unimag.edu.proyecto_final.domine.repository.ParcelRepository;
import com.unimag.edu.proyecto_final.domine.repository.StopRepository;
import com.unimag.edu.proyecto_final.exception.BadRequestException;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.ParcelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class ParcelServicelmpl implements  ParcelService{

    private final ParcelRepository parcelRepository;
    private final StopRepository stopRepository;
    private final ParcelMapper parcelMapper;
    private final IncidentService incidentService;

    @Override
    public ParcelDtos.ParcelResponse create(ParcelDtos.ParcelCreateRequest request) {
        if (parcelRepository.existsByCode(request.code())){
            throw  new IllegalArgumentException("Code already exists");
        }

        Stop  fromStop = stopRepository.findById(request.fromStopId())
                .orElseThrow(() -> new NotFoundException("Stop not found"));

        Stop toStop = stopRepository.findById(request.toStopId())
                .orElseThrow(() -> new NotFoundException("Stop not found"));

        Parcel parcel = parcelMapper.toEntity(request);
        parcel.setFromStop(fromStop);
        parcel.setToStop(toStop);
        parcel.setPrice(request.price());
        parcel.setStatusParcel(StatusParcel.CREATED);

        Parcel saved = parcelRepository.save(parcel);
        return parcelMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ParcelDtos.ParcelResponse get(Long id) {
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Parcel not found"));
        return parcelMapper.toResponse(parcel);
    }

    @Override
    @Transactional(readOnly = true)
    public ParcelDtos.ParcelResponse getByCode(String code) {
        Parcel parcel =  parcelRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Parcel not found"));
        return parcelMapper.toResponse(parcel);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ParcelDtos.ParcelResponse> listByStatus(String status, Pageable pageable) {
        StatusParcel statusParcel = StatusParcel.valueOf(status.toUpperCase());
        List<ParcelDtos.ParcelResponse> list = parcelRepository.findByStatusParcel(statusParcel)
                .stream().map(parcelMapper::toResponse).toList();
        return new PageImpl<>(list, pageable, list.size());
    }

    @Override
    @Transactional
    public ParcelDtos.ParcelResponse update(Long id, ParcelDtos.ParcelUpdateRequest request) {

        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("parcel not found"));

        String newStatus = request.status();

        if ("COMPLETED".equalsIgnoreCase(newStatus)) {
            parcel.setStatusParcel(StatusParcel.COMPLETED);
        }

        else if ("FAILED".equalsIgnoreCase(newStatus)) {
            parcel.setStatusParcel(StatusParcel.FAILED);

            incidentService.createDeliveryFailureIncident(
                    parcel.getId(),
                    "Entrega fallida reportada para el paquete con código " + parcel.getCode()
            );
        }

        else {
            parcel.setStatusParcel(StatusParcel.valueOf(newStatus));
        }

        parcel = parcelRepository.save(parcel);

        return parcelMapper.toResponse(parcel);
    }



    @Override
    public void delete(Long id) {
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Parcel not found"));
        parcelRepository.delete(parcel);

    }
}
