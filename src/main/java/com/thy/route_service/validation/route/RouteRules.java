package com.thy.route_service.validation.route;


import com.thy.route_service.entity.Transportation;
import com.thy.route_service.entity.enums.TransportationType;
import com.thy.route_service.exception.BusinessRuleException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
@Component
public class RouteRules {

    private static final int MAX_STEPS = 3;

    public void validateSearchInput(Long originId, Long destinationId) {
        if (originId == null || destinationId == null) {
            throw new BusinessRuleException("originId and destinationId are required.");
        }
        if (originId.equals(destinationId)) {
            throw new BusinessRuleException("originId and destinationId cannot be the same.");
        }
    }

    public boolean canAddStep(List<Transportation> currentPath,
                              Transportation next,
                              LocalDate date) {

        // depth limit
        if (currentPath.size() >= MAX_STEPS) return false;

        // 연결 kuralı
        if (!currentPath.isEmpty()) {
            Transportation last = currentPath.get(currentPath.size() - 1);
            if (!last.getDestination().getId()
                    .equals(next.getOrigin().getId())) {
                return false;
            }
        }

        // 날짜 filtresi
        if (date != null && !matchesOperatingDays(next.getOperatingDays(), date)) {
            return false;
        }

        // exactly one FLIGHT, before/after max 1
        int flightCount = 0;
        boolean flightSeen = false;
        int before = 0;
        int after = 0;

        for (Transportation t : currentPath) {
            if (t.getType() == TransportationType.FLIGHT) {
                flightCount++;
                flightSeen = true;
            } else {
                if (!flightSeen) before++;
                else after++;
            }
        }

        if (next.getType() == TransportationType.FLIGHT) {
            flightCount++;
        } else {
            if (!flightSeen) before++;
            else after++;
        }

        return flightCount <= 1 && before <= 1 && after <= 1;
    }

    public boolean isCompleteValidRoute(List<Transportation> path) {
        long flights =
                path.stream()
                        .filter(t -> t.getType() == TransportationType.FLIGHT)
                        .count();
        return flights == 1;
    }

    private boolean matchesOperatingDays(Set<Integer> days,
                                         LocalDate date) {

        if (days == null || days.isEmpty()) return true;

        int day = date.getDayOfWeek().getValue();
        return days.contains(day);
    }
}