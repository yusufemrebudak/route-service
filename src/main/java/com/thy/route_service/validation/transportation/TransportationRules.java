package com.thy.route_service.validation.transportation;


import com.thy.route_service.exception.BusinessRuleException;
import com.thy.route_service.exception.NotFoundException;
import com.thy.route_service.repository.LocationRepository;
import com.thy.route_service.repository.TransportationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class TransportationRules {

    private final LocationRepository locationRepository;
    private final TransportationRepository transportationRepository;

    public void checkTransportationExists(Long id) {
        if (id == null || !transportationRepository.existsById(id)) {
            throw new NotFoundException("Transportation not found with id: " + id);
        }
    }

    public void checkLocationsExist(Long originId, Long destinationId) {
        if (originId == null || !locationRepository.existsById(originId)) {
            throw new NotFoundException("Origin location not found with id: " + originId);
        }
        if (destinationId == null || !locationRepository.existsById(destinationId)) {
            throw new NotFoundException("Destination location not found with id: " + destinationId);
        }
    }

    public void checkOriginDestinationDifferent(Long originId, Long destinationId) {
        if (originId != null && originId.equals(destinationId)) {
            throw new BusinessRuleException("Origin and destination cannot be the same location.");
        }
    }

    public void checkOperatingDays(Set<Integer> operatingDays) {
        if (operatingDays == null || operatingDays.isEmpty()) {
            // boş ise "her gün çalışıyor" kabul edeceğiz (bonus route filtresi bunu böyle yorumlayacak)
            return;
        }
        for (Integer day : operatingDays) {
            if (day == null || day < 1 || day > 7) {
                throw new BusinessRuleException("Operating days must be between 1 and 7.");
            }
        }
    }
    public void checkAvaliableTransportation(Long id) {
        if (id != null && ( transportationRepository.existsByOriginIdOrDestinationId(id) )) {
            throw new BusinessRuleException("This location cannot be deleted, because of the avaliable transportations.");
        }
    }
}