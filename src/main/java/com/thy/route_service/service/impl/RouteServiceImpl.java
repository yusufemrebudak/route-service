package com.thy.route_service.service.impl;


import com.thy.route_service.dto.route.RouteResponse;
import com.thy.route_service.entity.Transportation;
import com.thy.route_service.mapper.RouteMapper;
import com.thy.route_service.repository.TransportationRepository;
import com.thy.route_service.service.RouteService;
import com.thy.route_service.validation.route.RouteRules;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RouteServiceImpl implements RouteService {

    private final TransportationRepository transportationRepository;
    private final RouteRules routeRules;
    private final RouteMapper routeMapper;

    @Override
    public List<RouteResponse> findRoutes(Long originId, Long destinationId, LocalDate date) {

        routeRules.validateSearchInput(originId, destinationId);

        Map<Long, List<Transportation>> graph = buildGraph(
                transportationRepository.findAllWithLocations()
        );

        List<RouteResponse> results = new ArrayList<>();
        Deque<Transportation> path = new ArrayDeque<>();
        Set<Long> visitedLocations = new HashSet<>();
        visitedLocations.add(originId);

        dfs(originId, destinationId, date, graph, path, visitedLocations, results);

        return results;
    }

    private void dfs(Long current,
                     Long destination,
                     LocalDate date,
                     Map<Long, List<Transportation>> graph,
                     Deque<Transportation> path,
                     Set<Long> visitedLocations,
                     List<RouteResponse> results) {

        if (path.size() > 3) return;

        if (!path.isEmpty() && path.getLast().getDestination().getId().equals(destination)) {
            List<Transportation> completed = List.copyOf(path);
            if (routeRules.isCompleteValidRoute(completed)) {
                results.add(routeMapper.toRouteResponse(completed));
            }
            return;
        }

        for (Transportation next : graph.getOrDefault(current, List.of())) {

            Long nextDest = next.getDestination().getId();
            if (visitedLocations.contains(nextDest)) continue; // cycle guard

            if (!routeRules.canAddStep(List.copyOf(path), next, date)) continue;

            path.addLast(next);
            visitedLocations.add(nextDest);

            dfs(nextDest, destination, date, graph, path, visitedLocations, results);

            path.removeLast();
            visitedLocations.remove(nextDest);
        }
    }

    private Map<Long, List<Transportation>> buildGraph(List<Transportation> all) {
        Map<Long, List<Transportation>> graph = new HashMap<>();
        for (Transportation t : all) {
            graph.computeIfAbsent(t.getOrigin().getId(), k -> new ArrayList<>()).add(t);
        }
        return graph;
    }
}