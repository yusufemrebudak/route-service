package com.thy.route_service.service.impl;


import com.thy.route_service.dto.location.request.LocationCreateRequest;
import com.thy.route_service.dto.location.request.LocationUpdateRequest;
import com.thy.route_service.dto.location.response.LocationResponse;
import com.thy.route_service.entity.Location;
import com.thy.route_service.exception.NotFoundException;
import com.thy.route_service.mapper.LocationMapper;
import com.thy.route_service.repository.LocationRepository;
import com.thy.route_service.service.LocationService;
import com.thy.route_service.validation.location.LocationRules;
import com.thy.route_service.validation.transportation.TransportationRules;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationRules locationRules;
    private final LocationMapper locationMapper;
    private final TransportationRules transportationRules;


    @Override
    public LocationResponse create(LocationCreateRequest request) {
        String code = locationRules.resolveLocationCode(request.locationCode(), request.name());
        locationRules.checkLocationCodeUnique(code);
        locationRules.checkNameAndCityUnique(request.name(), request.city());

        Location entity = locationMapper.toEntity(request, code);
        Location saved = locationRepository.save(entity);

        return locationMapper.toResponse(saved);
    }

    @Override
    public LocationResponse update(Long id, LocationUpdateRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location not found: " + id));

        // code değiştirilecekse rules + set
        if (request.locationCode() != null && !request.locationCode().isBlank()) {
            String newCode = locationRules.resolveLocationCode(request.locationCode(), location.getName());
            locationRules.checkLocationCodeUniqueForUpdate(id, newCode);
            location.setLocationCode(newCode);
        }
        locationRules.checkNameAndCityUniqueForUpdate(id,request.name(),request.city());

        // diğer alanlar
        locationMapper.applyUpdate(request, location);

        return locationMapper.toResponse(location);
    }

    @Override
    @Transactional(readOnly = true)
    public LocationResponse getById(Long id) {
        Location l = locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Location not found: " + id));
        return locationMapper.toResponse(l);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocationResponse> getAll() {
        return locationRepository.findAll()
                .stream()
                .map(locationMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        locationRules.checkLocationExists(id);
        transportationRules.checkAvaliableTransportation(id);
        locationRepository.deleteById(id);
    }
}