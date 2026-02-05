package com.thy.route_service.dto.transportation.request;

import jakarta.validation.constraints.*;
import java.util.Set;

public record TransportationCreateRequest(
        @NotNull Long originId,
        @NotNull Long destinationId,
        @NotNull String type,
        Set<@Min(1) @Max(7) Integer> operatingDays
) {}
