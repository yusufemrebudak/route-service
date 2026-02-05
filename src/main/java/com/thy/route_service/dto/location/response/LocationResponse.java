package com.thy.route_service.dto.location.response;

public record LocationResponse(
        Long id,
        String name,
        String country,
        String city,
        String locationCode,
        Long version
) {}