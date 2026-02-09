package com.thy.route_service.controller;

import com.thy.route_service.dto.location.response.LocationSummaryResponse;
import com.thy.route_service.dto.route.RouteResponse;
import com.thy.route_service.dto.route.RouteStepResponse;
import com.thy.route_service.exception.BusinessRuleException;
import com.thy.route_service.service.RouteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RouteController.class)
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RouteService routeService;

    @Test
    void findRoutes_WithValidParameters_ShouldReturnRoutes() throws Exception {
        // Given
        Long originId = 1L;
        Long destinationId = 2L;
        LocalDate date = LocalDate.of(2026, 2, 10);

        LocationSummaryResponse origin = new LocationSummaryResponse(1L, "IST", "Istanbul Airport", "Istanbul", "Turkey");
        LocationSummaryResponse destination = new LocationSummaryResponse(2L, "JFK", "John F. Kennedy Airport", "New York", "USA");

        RouteStepResponse step = new RouteStepResponse(
                1L, "FLIGHT", origin, destination, Set.of(1, 2, 3, 4, 5)
        );

        RouteResponse routeResponse = new RouteResponse(List.of(step));
        List<RouteResponse> expectedRoutes = List.of(routeResponse);

        when(routeService.findRoutes(originId, destinationId, date))
                .thenReturn(expectedRoutes);

        // When & Then
        mockMvc.perform(get("/routes")
                        .param("originId", originId.toString())
                        .param("destinationId", destinationId.toString())
                        .param("date", date.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].steps.length()").value(1))
                .andExpect(jsonPath("$[0].steps[0].transportationId").value(1))
                .andExpect(jsonPath("$[0].steps[0].type").value("FLIGHT"))
                .andExpect(jsonPath("$[0].steps[0].origin.name").value("Istanbul Airport"))
                .andExpect(jsonPath("$[0].steps[0].destination.name").value("John F. Kennedy Airport"));
    }

    @Test
    void findRoutes_WithoutDate_ShouldReturnRoutes() throws Exception {
        // Given
        Long originId = 1L;
        Long destinationId = 2L;

        LocationSummaryResponse origin = new LocationSummaryResponse(1L, "IST", "Istanbul Airport", "Istanbul", "Turkey");
        LocationSummaryResponse destination = new LocationSummaryResponse(2L, "JFK", "John F. Kennedy Airport", "New York", "USA");

        RouteStepResponse step = new RouteStepResponse(
                1L, "FLIGHT", origin, destination, Set.of(1, 2, 3, 4, 5)
        );

        RouteResponse routeResponse = new RouteResponse(List.of(step));
        List<RouteResponse> expectedRoutes = List.of(routeResponse);

        when(routeService.findRoutes(eq(originId), eq(destinationId), isNull()))
                .thenReturn(expectedRoutes);

        // When & Then
        mockMvc.perform(get("/routes")
                        .param("originId", originId.toString())
                        .param("destinationId", destinationId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void findRoutes_WithInvalidInput_ShouldReturnBadRequest() throws Exception {
        // Given
        Long originId = 1L;
        Long destinationId = 1L; // Same as origin

        when(routeService.findRoutes(originId, destinationId, null))
                .thenThrow(new BusinessRuleException("originId and destinationId cannot be the same."));

        // When & Then
        mockMvc.perform(get("/routes")
                        .param("originId", originId.toString())
                        .param("destinationId", destinationId.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findRoutes_WithMissingOriginId_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/routes")
                        .param("destinationId", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findRoutes_WithMissingDestinationId_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/routes")
                        .param("originId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findRoutes_WithInvalidDateFormat_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/routes")
                        .param("originId", "1")
                        .param("destinationId", "2")
                        .param("date", "invalid-date"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findRoutes_NoRoutesFound_ShouldReturnEmptyList() throws Exception {
        // Given
        Long originId = 1L;
        Long destinationId = 999L; // Non-existing destination

        when(routeService.findRoutes(originId, destinationId, null))
                .thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/routes")
                        .param("originId", originId.toString())
                        .param("destinationId", destinationId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void findRoutes_WithMultipleRoutes_ShouldReturnAllRoutes() throws Exception {
        // Given
        Long originId = 1L;
        Long destinationId = 3L;

        LocationSummaryResponse istanbul = new LocationSummaryResponse(1L, "IST", "Istanbul Airport", "Istanbul", "Turkey");
        LocationSummaryResponse london = new LocationSummaryResponse(2L, "LHR", "Heathrow Airport", "London", "UK");
        LocationSummaryResponse newYork = new LocationSummaryResponse(3L, "JFK", "John F. Kennedy Airport", "New York", "USA");

        // Direct route
        RouteStepResponse directStep = new RouteStepResponse(
                1L, "FLIGHT", istanbul, newYork, Set.of(1, 2, 3, 4, 5)
        );
        RouteResponse directRoute = new RouteResponse(List.of(directStep));

        // Route with connection
        RouteStepResponse step1 = new RouteStepResponse(
                2L, "FLIGHT", istanbul, london, Set.of(1, 2, 3, 4, 5)
        );
        RouteStepResponse step2 = new RouteStepResponse(
                3L, "FLIGHT", london, newYork, Set.of(1, 2, 3, 4, 5)
        );
        RouteResponse connectionRoute = new RouteResponse(List.of(step1, step2));

        List<RouteResponse> expectedRoutes = List.of(directRoute, connectionRoute);

        when(routeService.findRoutes(originId, destinationId, null))
                .thenReturn(expectedRoutes);

        // When & Then
        mockMvc.perform(get("/routes")
                        .param("originId", originId.toString())
                        .param("destinationId", destinationId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].steps.length()").value(1))
                .andExpect(jsonPath("$[1].steps.length()").value(2));
    }
}



