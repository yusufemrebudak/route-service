package com.thy.route_service.service;


import com.thy.route_service.dto.route.RouteResponse;

import java.time.LocalDate;
import java.util.List;

public interface RouteService {
    List<RouteResponse> findRoutes(Long originId, Long destinationId, LocalDate date);
}