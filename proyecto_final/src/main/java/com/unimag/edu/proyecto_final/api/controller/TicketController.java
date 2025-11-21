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
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;


    @PostMapping("/{tripId}/tickets")
    public ResponseEntity<TicketResponse> create(
            @PathVariable Long tripId,
            @Valid @RequestBody TicketCreateRequest request
    ) {

        TicketCreateRequest fixedRequest = new TicketCreateRequest(
                tripId,
                request.passengerId(),
                request.seatNumber(),
                request.fromStopId(),
                request.toStopId(),
                request.price(),
                request.paymentMethod(),
                request.approvedByDispatcher()
        );

        TicketResponse created = ticketService.create(fixedRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @GetMapping("/tickets/{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable Long id){
        return ResponseEntity.ok(ticketService.get(id));
    }


    @GetMapping("/{tripId}/tickets")
    public ResponseEntity<List<TicketResponse>> listByTrip(@PathVariable Long tripId){
        return ResponseEntity.ok(ticketService.listByTrip(tripId));
    }


    @GetMapping("/passenger/{passengerId}/tickets")
    public ResponseEntity<List<TicketResponse>> listByPassenger(@PathVariable Long passengerId){
        return ResponseEntity.ok(ticketService.listByPassenger(passengerId));
    }


    @PutMapping("/tickets/{id}")
    public ResponseEntity<TicketResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TicketUpdateRequest request
    ){
        return ResponseEntity.ok(ticketService.update(id, request));
    }


    @PostMapping("/tickets/{id}/cancel")
    public ResponseEntity<TicketResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.cancel(id));
    }


    @GetMapping("/tickets/qr/qrCode")
    public ResponseEntity<TicketResponse> qrCode(@RequestParam String ticketCode){
        return ResponseEntity.ok(ticketService.getByQrCode(ticketCode));
    }
}