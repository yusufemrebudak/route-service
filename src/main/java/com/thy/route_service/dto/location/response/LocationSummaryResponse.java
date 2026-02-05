package com.thy.route_service.dto.location.response;

public record LocationSummaryResponse(
        Long id,
        String locationCode,
        String name,
        String city,
        String country
) {}