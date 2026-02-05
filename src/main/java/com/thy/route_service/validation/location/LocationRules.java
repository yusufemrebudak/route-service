package com.thy.route_service.validation.location;
import com.thy.route_service.exception.ConflictException;
import com.thy.route_service.exception.NotFoundException;
import com.thy.route_service.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class LocationRules {

    private final LocationRepository locationRepository;

    public void checkLocationExists(Long id) {
        if (id == null || !locationRepository.existsById(id)) {
            throw new NotFoundException("Location not found with id: " + id);
        }
    }

    public void checkLocationCodeUnique(String code) {
        if (locationRepository.existsByLocationCode(code)) {
            throw new ConflictException("Location code already exists: " + code);
        }
    }

    public void checkLocationCodeUniqueForUpdate(Long id, String newCode) {
        locationRepository.findByLocationCode(newCode)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("Location code already exists: " + newCode);
                });
    }

    /**
     * If user provided locationCode => normalize (trim + uppercase).
     * If not provided => generate acronym from name (e.g., "Istanbul City Center" -> "ICC").
     */
    public String resolveLocationCode(String providedCode, String name) {

        if (providedCode != null && !providedCode.isBlank()) {
            return providedCode.trim().toUpperCase();
        }

        if (name == null || name.isBlank()) {
            throw new NotFoundException("Location name is required to generate location code");
        }

        String generated = Arrays.stream(name.trim().split("\\s+"))
                .filter(w -> !w.isBlank())
                .map(w -> String.valueOf(Character.toUpperCase(w.charAt(0))))
                .collect(Collectors.joining());

        return generated.length() > 10 ? generated.substring(0, 10) : generated;
    }
}