
package com.unimag.edu.proyecto_final.service;

import com.unimag.edu.proyecto_final.api.dto.RouteDtos;
import com.unimag.edu.proyecto_final.api.dto.StopDtos;
import com.unimag.edu.proyecto_final.domine.entities.Route;
import com.unimag.edu.proyecto_final.domine.entities.Stop;
import com.unimag.edu.proyecto_final.domine.repository.RouteRepository;
import com.unimag.edu.proyecto_final.exception.NotFoundException;
import com.unimag.edu.proyecto_final.service.mappers.RouteMapper;
import com.unimag.edu.proyecto_final.service.mappers.StopMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class RouteServicelmpl implements RouteService{

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;
    private final StopMapper stopMapper;

    @Override
    public RouteDtos.RouteResponse create(RouteDtos.RouteCreateRequest request) {
        routeRepository.findByRouteCode(request.code())
                .ifPresent(r -> {
                    throw new IllegalArgumentException("Route already exists");
                });

        routeRepository.findByOriginAndDestination(request.origin(), request.destination())
                .ifPresent(r -> {throw new IllegalArgumentException("Route already exists");});

        Route route = routeMapper.toEntity(request);

        route.setRouteCode(request.code());
        route.setRouteName(request.name());
        route.setOriginName(request.origin());
        route.setDestinationName(request.destination());
        route.setDistance(request.distanceKm());
        route.setTime(request.durationMin() != null ? request.durationMin() / 60: null);


        Route saved = routeRepository.save(route);
        return routeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RouteDtos.RouteResponse get(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("route not found"));
        return routeMapper.toResponse(route);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RouteDtos.RouteResponse> list(Pageable pageable) {
        Page<Route> page = routeRepository.findAll(pageable);
        return page.map(routeMapper::toResponse);
    }

    @Override
    public List<RouteDtos.RouteResponse> searchByCity(String city) {
        List<Route> routes = routeRepository.searchByCity(city);
        if (routes.isEmpty()) {
            routes = routeRepository.findByIntermediateStop(city);
        }
        return routes.stream().map(routeMapper::toResponse).toList();
    }

    @Override
    public RouteDtos.RouteResponse update(Long id, RouteDtos.RouteUpdateRequest request) {
        Route route = routeRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("route not found"));

        routeMapper.updateEntityFromDto(request, route);

        if (request.name() != null) route.setRouteName(request.name());
        if (request.origin() != null) route.setOriginName(request.origin());
        if (request.destination() != null) route.setDestinationName(request.destination());
        if (request.distanceKm() != null) route.setDistance(request.distanceKm());
        if (request.durationMin() != null) route.setTime(request.durationMin() / 60);

        Route updated = routeRepository.save(route);
        return routeMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("route not found"));

        routeRepository.delete(route);
    }
    @Override
    public List<StopDtos.StopResponse> getStopsByRoute(Long routeId) {

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found"));

        return route.getStops()
                .stream()
                .sorted(Comparator.comparing(Stop::getStopOrder))
                .map(stopMapper::toResponse)
                .toList();
    }
}
