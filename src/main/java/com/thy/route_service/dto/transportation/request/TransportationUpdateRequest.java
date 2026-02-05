package com.thy.route_service.dto.transportation.request;

import jakarta.validation.constraints.*;
import java.util.Set;

public record TransportationUpdateRequest(
        Long originId,
        Long destinationId,
        String type,
        Set<@Min(1) @Max(7) Integer> operatingDays
) {}