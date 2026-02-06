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
        if (originId == null || destinationId == null)
            throw new BusinessRuleException("originId and destinationId are required.");
        if (originId.equals(destinationId))
            throw new BusinessRuleException("originId and destinationId cannot be the same.");
    }

    public boolean canAddStep(List<Transportation> path, Transportation next, LocalDate date) {

        if (path.size() >= MAX_STEPS) return false;

        if (!path.isEmpty()) {
            Transportation last = path.get(path.size() - 1);
            if (!last.getDestination().getId().equals(next.getOrigin().getId())) return false;
        }

        if (date != null && !matchesOperatingDays(next.getOperatingDays(), date)) return false;

        int flights = 0;
        boolean flightSeen = false;
        int before = 0, after = 0;

        for (Transportation t : path) {
            if (t.getType() == TransportationType.FLIGHT) {
                flights++;
                flightSeen = true;
            } else {
                if (!flightSeen) before++; else after++;
            }
        }

        if (next.getType() == TransportationType.FLIGHT) flights++;
        else { if (!flightSeen) before++; else after++; }

        return flights <= 1 && before <= 1 && after <= 1;
    }

    public boolean isCompleteValidRoute(List<Transportation> path) {
        return path.stream().filter(t -> t.getType() == TransportationType.FLIGHT).count() == 1;
    }

    private boolean matchesOperatingDays(Set<Integer> days, LocalDate date) {
        if (days == null || days.isEmpty()) return true;
        return days.contains(date.getDayOfWeek().getValue());
    }
}