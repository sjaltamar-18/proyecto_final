package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.SeatHoldDtos.*;
import com.unimag.edu.proyecto_final.service.SeatHoldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seat-holds")
@RequiredArgsConstructor
public class SeatHoldController {
    private final SeatHoldService seatHoldService;

    @PostMapping
    public ResponseEntity<SeatHoldResponse> createSeatHold(@Valid @RequestBody SeatHoldCreateRequest request ) {
        SeatHoldResponse created = seatHoldService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    @GetMapping("/{id}")
    public ResponseEntity<SeatHoldResponse> getSeatHoldById(@PathVariable Long id) {
        return ResponseEntity.ok(seatHoldService.get(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SeatHoldResponse>>  getSeatHoldsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(seatHoldService.listByUser(userId));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<SeatHoldResponse>> getActiveSeatHoldsByTripId(@PathVariable Long tripId) {
        return  ResponseEntity.ok(seatHoldService.listActiveByTrip(tripId));
    }

    @PatchMapping("/{id}")
    public  ResponseEntity<SeatHoldResponse> updateSeatHold(@PathVariable Long id, @Valid @RequestBody SeatHoldUpdateRequest request ) {
        return ResponseEntity.ok(seatHoldService.update(id, request));
    }

    @PostMapping("/expire")
    public ResponseEntity<Integer> expireHolds(){
        int expiredCount = seatHoldService.expireHolds();
        return ResponseEntity.ok(expiredCount);
    }

    @DeleteMapping("/clean")
    public ResponseEntity<Integer> cleanOldExpired(){
        int cleanCount = seatHoldService.cleanOldExpired();
        return ResponseEntity.ok(cleanCount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SeatHoldResponse> deleteSeatHold(@PathVariable Long id){
        seatHoldService.delete(id);
        return ResponseEntity.noContent().build();

    }

}