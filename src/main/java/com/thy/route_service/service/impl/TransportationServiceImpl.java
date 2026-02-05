package com.thy.route_service.service.impl;

import com.thy.route_service.dto.transportation.request.TransportationCreateRequest;
import com.thy.route_service.dto.transportation.request.TransportationUpdateRequest;
import com.thy.route_service.dto.transportation.response.TransportationResponse;
import com.thy.route_service.entity.Location;
import com.thy.route_service.entity.Transportation;
import com.thy.route_service.entity.enums.TransportationType;
import com.thy.route_service.exception.BusinessRuleException;
import com.thy.route_service.exception.NotFoundException;
import com.thy.route_service.mapper.TransportationMapper;
import com.thy.route_service.repository.LocationRepository;
import com.thy.route_service.repository.TransportationRepository;
import com.thy.route_service.service.TransportationService;
import com.thy.route_service.validation.transportation.TransportationRules;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransportationServiceImpl implements TransportationService {

    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;
    private final TransportationRules transportationRules;
    private final TransportationMapper transportationMapper;

    @Override
    public TransportationResponse create(TransportationCreateRequest request) {

        transportationRules.checkOriginDestinationDifferent(request.originId(), request.destinationId());
        transportationRules.checkLocationsExist(request.originId(), request.destinationId());
        transportationRules.checkOperatingDays(request.operatingDays());

        Location origin = loadLocation(request.originId(), "Origin");
        Location destination = loadLocation(request.destinationId(), "Destination");
        TransportationType type = parseType(request.type());

        Transportation entity = transportationMapper.toEntity(
                origin,
                destination,
                type,
                request.operatingDays()
        );

        Transportation saved = transportationRepository.save(entity);
        return transportationMapper.toResponse(saved);
    }

    @Override
    public TransportationResponse update(Long id, TransportationUpdateRequest request) {

        Transportation t = transportationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transportation not found with id: " + id));

        // hedef origin/destination (null gelirse mevcut)
        Long originId = request.originId() != null ? request.originId() : t.getOrigin().getId();
        Long destinationId = request.destinationId() != null ? request.destinationId() : t.getDestination().getId();

        transportationRules.checkOriginDestinationDifferent(originId, destinationId);
        transportationRules.checkLocationsExist(originId, destinationId);

        Location origin = request.originId() != null ? loadLocation(request.originId(), "Origin") : null;
        Location destination = request.destinationId() != null ? loadLocation(request.destinationId(), "Destination") : null;

        TransportationType type = null;
        if (request.type() != null && !request.type().isBlank()) {
            type = parseType(request.type());
        }

        if (request.operatingDays() != null) {
            transportationRules.checkOperatingDays(request.operatingDays());
        }

        transportationMapper.applyUpdate(
                t,
                origin,
                destination,
                type,
                request.operatingDays()
        );

        return transportationMapper.toResponse(t);
    }

    @Override
    @Transactional(readOnly = true)
    public TransportationResponse getById(Long id) {
        Transportation t = transportationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transportation not found with id: " + id));
        return transportationMapper.toResponse(t);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransportationResponse> getAll() {
        // N+1 önlemek için fetch join methodunu kullanıyoruz
        return transportationRepository.findAllWithLocations()
                .stream()
                .map(transportationMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        transportationRules.checkTransportationExists(id);
        transportationRepository.deleteById(id);
    }

    private Location loadLocation(Long id, String label) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(label + " location not found with id: " + id));
    }

    private TransportationType parseType(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BusinessRuleException("Transportation type is required.");
        }
        try {
            return TransportationType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessRuleException("Invalid transportation type: " + raw);
        }
    }
}