package com.thy.route_service.dto.location.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocationUpdateRequest(

        @NotBlank
        @Size(max = 100)
        String name,
        @NotBlank
        @Size(max = 50)
        String country,
        @NotBlank
        @Size(max = 50)
        String city,

        String locationCode
) {}