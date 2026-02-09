package com.thy.route_service.controller;


import com.thy.route_service.dto.transportation.request.TransportationCreateRequest;
import com.thy.route_service.dto.transportation.request.TransportationUpdateRequest;
import com.thy.route_service.dto.transportation.response.TransportationResponse;
import com.thy.route_service.dto.transportation.response.TransportationTypeResponse;
import com.thy.route_service.service.TransportationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/transportations")
@RequiredArgsConstructor
public class TransportationController {

    private final TransportationService transportationService;

    @PostMapping
    public ResponseEntity<TransportationResponse> create(@RequestBody @Valid TransportationCreateRequest request) {
        return ResponseEntity.ok(transportationService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransportationResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(transportationService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TransportationResponse>> list() {
        return ResponseEntity.ok(transportationService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransportationResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid TransportationUpdateRequest request
    ) {
        return ResponseEntity.ok(transportationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transportationService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/types")
    public ResponseEntity<List<TransportationTypeResponse>> listTypes() {
        return ResponseEntity.ok(transportationService.getAllTypes());
    }
}