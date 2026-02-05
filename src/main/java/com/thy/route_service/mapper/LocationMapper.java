package com.thy.route_service.mapper;

import com.thy.route_service.dto.location.request.LocationCreateRequest;
import com.thy.route_service.dto.location.request.LocationUpdateRequest;
import com.thy.route_service.dto.location.response.LocationResponse;
import com.thy.route_service.dto.location.response.LocationSummaryResponse;
import com.thy.route_service.entity.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location toEntity(LocationCreateRequest req, String resolvedCode) {
        Location l = new Location();
        l.setName(req.name());
        l.setCountry(req.country());
        l.setCity(req.city());
        l.setLocationCode(resolvedCode);
        return l;
    }

    public void applyUpdate(LocationUpdateRequest req, Location target) {
        if (req.name() != null) target.setName(req.name());
        if (req.country() != null) target.setCountry(req.country());
        if (req.city() != null) target.setCity(req.city());
        // locationCode update service/rules'ta kontrol edilip setleniyor
    }

    public LocationResponse toResponse(Location l) {
        return new LocationResponse(
                l.getId(),
                l.getName(),
                l.getCountry(),
                l.getCity(),
                l.getLocationCode(),
                l.getVersion()
        );
    }

    public LocationSummaryResponse toSummary(Location l) {
        return new LocationSummaryResponse(
                l.getId(),
                l.getLocationCode(),
                l.getName(),
                l.getCity(),
                l.getCountry()
        );
    }
}