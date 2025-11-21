package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.TripDtos.*;
import com.unimag.edu.proyecto_final.domine.entities.Trip;
import com.unimag.edu.proyecto_final.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@Valid@RequestBody TripCreateRequest request )
    {
        TripResponse created = tripService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    @PostMapping("/{id}/depart")
    public ResponseEntity<TripResponse> authorizeDeparture(
            @PathVariable Long id,
            @RequestParam Long driverId
    ) {
        TripResponse response = tripService.authorizeDeparture(id, driverId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> get(@PathVariable Long id)
    {
        return ResponseEntity.ok(tripService.get(id));
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<TripResponse>> listByRoute(@PathVariable Long routeId,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date)
    {
        return ResponseEntity.ok(tripService.listByRoute(routeId, date));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<TripResponse>> listUpcoming()
    {
        return ResponseEntity.ok(tripService.listUpcoming());
    }

    @GetMapping("/between")
    public ResponseEntity<List<TripResponse>> listBetween(@RequestParam@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end){
        return ResponseEntity.ok(tripService.listBetween(start,end));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> update(@PathVariable Long id, @Valid @RequestBody TripUpdateRequest request){
        return ResponseEntity.ok(tripService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TripResponse> delete(@PathVariable Long id){
        tripService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
