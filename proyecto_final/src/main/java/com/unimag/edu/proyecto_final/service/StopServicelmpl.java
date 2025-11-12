package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.StopDtos;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.repository.RouteRepository;
import com.unimag.edu.proyecto_final.domine.repository.StopRepository;
import com.unimag.edu.proyecto_final.service.mappers.StopMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StopServicelmpl implements StopService {

    private final StopRepository stopRepository;
    private final RouteRepository routeRepository;
    private final StopMapper stopMapper;

    @Override
    public StopDtos.StopResponse create(Long routeId, StopDtos.StopCreateRequest request) {
       Route route = routeRepository.findById(routeId)
               .orElseThrow(()-> new EntityNotFoundException("route not found"));

       stopRepository.findByRouteAndName(request.routeId(), request.name())
               .orElseThrow(()-> new IllegalArgumentException("stop already exists"));

        Stop stop = stopMapper.toEntity(request);
        stop.setRoute(route);
        stop.setStopName(request.name());
        stop.setStopOrder(request.order());
        stop.setLatitude(request.lat());
        stop.setLongitude(request.lng());

        Stop saved = stopRepository.save(stop);
        return stopMapper.toResponse(saved);

    }

    @Override
    @Transactional(readOnly = true)
    public StopDtos.StopResponse get(Long id) {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("stop not found"));

        return stopMapper.toResponse(stop);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StopDtos.StopResponse> listByRoute(Long routeId, Pageable pageable) {
        List<Stop> stops = stopRepository.findByRouteIdOrderByStopOrderAsc(routeId);
        return stops.stream().map(stopMapper::toResponse).toList();
    }

    @Override
    public StopDtos.StopResponse update(Long id, StopDtos.StopUpdateRequest request) {
       Stop stop = stopRepository.findById(id)
               .orElseThrow(()-> new EntityNotFoundException("stop not found"));

       stopMapper.updateEntityFromDto(request,stop);
       Stop update = stopRepository.save(stop);
       return stopMapper.toResponse(stop);
    }

    @Override
    public void delete(Long id) {
        Stop stop = stopRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("stop not found"));
        stopRepository.delete(stop);

    }
}
