package com.thy.route_service.dto.route;

import com.thy.route_service.dto.location.response.LocationSummaryResponse;

import java.util.Set;

public record RouteStepResponse(
        Long transportationId,
        String type,
        LocationSummaryResponse origin,
        LocationSummaryResponse destination,
        Set<Integer> operatingDays
) {}