package com.thy.route_service.controller;


import com.thy.route_service.dto.location.request.LocationCreateRequest;
import com.thy.route_service.dto.location.request.LocationUpdateRequest;
import com.thy.route_service.dto.location.response.LocationResponse;
import com.thy.route_service.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationResponse> create(@RequestBody @Valid LocationCreateRequest request) {
        return ResponseEntity.ok(locationService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<LocationResponse>> list() {
        return ResponseEntity.ok(locationService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid LocationUpdateRequest request
    ) {
        return ResponseEntity.ok(locationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}