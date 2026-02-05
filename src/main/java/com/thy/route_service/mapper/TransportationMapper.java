package com.thy.route_service.mapper;


import com.thy.route_service.dto.location.response.LocationSummaryResponse;
import com.thy.route_service.dto.transportation.response.TransportationResponse;
import com.thy.route_service.entity.Location;
import com.thy.route_service.entity.Transportation;
import com.thy.route_service.entity.enums.TransportationType;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class TransportationMapper {

    private final LocationMapper locationMapper;

    public TransportationMapper(LocationMapper locationMapper) {
        this.locationMapper = locationMapper;
    }

    public Transportation toEntity(Location origin,
                                   Location destination,
                                   TransportationType type,
                                   Set<Integer> operatingDays) {
        Transportation t = new Transportation();
        t.setOrigin(origin);
        t.setDestination(destination);
        t.setType(type);
        t.setOperatingDays(normalizeDays(operatingDays));
        return t;
    }

    public void applyUpdate(Transportation target,
                            Location originOrNull,
                            Location destinationOrNull,
                            TransportationType typeOrNull,
                            Set<Integer> operatingDaysOrNull) {

        if (originOrNull != null) target.setOrigin(originOrNull);
        if (destinationOrNull != null) target.setDestination(destinationOrNull);
        if (typeOrNull != null) target.setType(typeOrNull);
        if (operatingDaysOrNull != null) target.setOperatingDays(normalizeDays(operatingDaysOrNull));
    }

    public TransportationResponse toResponse(Transportation t) {
        LocationSummaryResponse origin = locationMapper.toSummary(t.getOrigin());
        LocationSummaryResponse destination = locationMapper.toSummary(t.getDestination());

        return new TransportationResponse(
                t.getId(),
                t.getType().name(),
                origin,
                destination,
                t.getOperatingDays(),
                t.getVersion()
        );
    }

    private Set<Integer> normalizeDays(Set<Integer> days) {
        if (days == null || days.isEmpty()) return new HashSet<>();
        return new HashSet<>(days);
    }
}