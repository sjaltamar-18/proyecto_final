package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.RouteDtos.*;
import com.unimag.edu.proyecto_final.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<RouteResponse> create(@Valid@RequestBody RouteCreateRequest routeCreateRequest) {
        RouteResponse routeResponse = routeService.create(routeCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(routeResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<RouteResponse>> list(Pageable pageable) {
        return ResponseEntity.ok(routeService.list(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RouteResponse>> searchByCity(@RequestParam String city) {
        return ResponseEntity.ok(routeService.searchByCity(city));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteResponse> update(@PathVariable Long id, @Valid  @RequestBody RouteUpdateRequest routeUpdateRequest) {
        return ResponseEntity.ok(routeService.update(id, routeUpdateRequest));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<RouteResponse> delete(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}