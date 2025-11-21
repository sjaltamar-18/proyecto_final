package com.unimag.edu.proyecto_final.api.controller;

import com.unimag.edu.proyecto_final.api.dto.TicketDtos.*;
import com.unimag.edu.proyecto_final.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketCreateRequest request){
        TicketResponse created = ticketService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
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
    @PostMapping("/{id}/cancel")
    public ResponseEntity<TicketResponse> cancel(@PathVariable Long id) {
        TicketResponse response = ticketService.cancel(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/qr/qrCode")
    public ResponseEntity<TicketResponse> qrCode(@RequestParam String ticketCode){
        return ResponseEntity.ok(ticketService.getByQrCode(ticketCode));
    }
}

