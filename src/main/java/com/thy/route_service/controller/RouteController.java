package com.thy.route_service.controller;

import com.thy.route_service.dto.route.RouteResponse;

import com.thy.route_service.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("api/v1/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    public ResponseEntity<List<RouteResponse>> findRoutes(
            @RequestParam Long originId,
            @RequestParam Long destinationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(routeService.findRoutes(originId, destinationId, date));
    }
}