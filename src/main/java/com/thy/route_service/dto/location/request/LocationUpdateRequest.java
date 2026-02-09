package com.thy.route_service.dto.location.request;

import jakarta.validation.constraints.Size;


public record LocationUpdateRequest(

        @Size(max = 100)
        String name,

        @Size(max = 50)
        String country,

        @Size(max = 50)
        String city,

        @Size(max = 10)
        String locationCode
) {}