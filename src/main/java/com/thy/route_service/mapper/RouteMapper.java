package com.thy.route_service.mapper;


import com.thy.route_service.dto.location.response.LocationSummaryResponse;
import com.thy.route_service.dto.route.RouteResponse;
import com.thy.route_service.dto.route.RouteStepResponse;
import com.thy.route_service.entity.Transportation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RouteMapper {

    private final LocationMapper locationMapper;

    public RouteMapper(LocationMapper locationMapper) {
        this.locationMapper = locationMapper;
    }

    public RouteResponse toRouteResponse(List<Transportation> path) {
        List<RouteStepResponse> steps = path.stream()
                .map(this::toStep)
                .toList();

        return new RouteResponse(steps);
    }

    private RouteStepResponse toStep(Transportation t) {
        LocationSummaryResponse origin = locationMapper.toSummary(t.getOrigin());
        LocationSummaryResponse destination = locationMapper.toSummary(t.getDestination());

        return new RouteStepResponse(
                t.getId(),
                t.getType().name(),
                origin,
                destination,
                t.getOperatingDays()
        );
    }
}