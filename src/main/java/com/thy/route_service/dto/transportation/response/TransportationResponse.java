package com.thy.route_service.dto.transportation.response;

import com.thy.route_service.dto.location.response.LocationSummaryResponse;

import java.util.Set;

public record TransportationResponse(
        Long id,
        String type,
        LocationSummaryResponse origin,
        LocationSummaryResponse destination,
        Set<Integer> operatingDays,
        Long version
) {}