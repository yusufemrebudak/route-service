package com.thy.route_service.dto.route;

import java.util.List;

public record RouteResponse(
        List<RouteStepResponse> steps
) {}