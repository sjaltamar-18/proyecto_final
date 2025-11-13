package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.TicketDtos.*;
import com.unimag.edu.proyecto_final.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TickerController {
    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketCreateRequest request){
        return ResponseEntity.ok(ticketService.create(request));
    }
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable Long id){
        return ResponseEntity.ok(ticketService.get(id));
    }
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<TicketResponse>> listByTrip(@PathVariable Long tripId){
        return ResponseEntity.ok(ticketService.listByTrip(tripId));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<TicketResponse>> listByPassenger(@PathVariable Long passengerId){
        return ResponseEntity.ok(ticketService.listByPassenger(passengerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> update(@PathVariable Long id, @Valid@RequestBody TicketUpdateRequest request){
        return ResponseEntity.ok(ticketService.update(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<TicketResponse> delete(@PathVariable Long id){
        ticketService.cancel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/qr/qrCode}")
    public ResponseEntity<TicketResponse> qrCode(@RequestParam String ticketCode){
        return ResponseEntity.ok(ticketService.getByQrCode(ticketCode));
    }
}

