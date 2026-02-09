package com.thy.route_service.service.impl;

import com.thy.route_service.dto.route.RouteResponse;
import com.thy.route_service.entity.Location;
import com.thy.route_service.entity.Transportation;
import com.thy.route_service.entity.enums.TransportationType;
import com.thy.route_service.exception.BusinessRuleException;
import com.thy.route_service.mapper.RouteMapper;
import com.thy.route_service.repository.TransportationRepository;
import com.thy.route_service.validation.route.RouteRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {

    @Mock
    private TransportationRepository transportationRepository;

    @Mock
    private RouteRules routeRules;

    @Mock
    private RouteMapper routeMapper;

    @InjectMocks
    private RouteServiceImpl routeService;

    private Location istanbul;
    private Location london;
    private Location newYork;
    private Location paris;

    @BeforeEach
    void setUp() {
        // Create test locations
        istanbul = createLocation(1L, "Istanbul Airport", "Istanbul", "Turkey", "IST");
        london = createLocation(2L, "Heathrow Airport", "London", "UK", "LHR");
        newYork = createLocation(3L, "John F. Kennedy Airport", "New York", "USA", "JFK");
        paris = createLocation(4L, "Charles de Gaulle Airport", "Paris", "France", "CDG");
    }

    @Test
    void findRoutes_WithDirectFlight_ShouldReturnSingleRoute() {
        // Given
        Long originId = 1L;
        Long destinationId = 3L;
        LocalDate date = LocalDate.of(2026, 2, 10);

        Transportation directFlight = createTransportation(1L, istanbul, newYork, TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5));
        List<Transportation> transportations = List.of(directFlight);

        RouteResponse expectedRoute = createMockRouteResponse();

        when(transportationRepository.findAllWithLocations()).thenReturn(transportations);
        when(routeRules.canAddStep(anyList(), eq(directFlight), eq(date))).thenReturn(true);
        when(routeRules.isCompleteValidRoute(List.of(directFlight))).thenReturn(true);
        when(routeMapper.toRouteResponse(List.of(directFlight))).thenReturn(expectedRoute);

        // When
        List<RouteResponse> result = routeService.findRoutes(originId, destinationId, date);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(expectedRoute);
        verify(routeRules).validateSearchInput(originId, destinationId);
        verify(transportationRepository).findAllWithLocations();
        verify(routeMapper).toRouteResponse(List.of(directFlight));
    }

    @Test
    void findRoutes_WithConnectingFlights_ShouldReturnMultiStepRoute() {
        // Given
        Long originId = 1L;
        Long destinationId = 3L;
        LocalDate date = LocalDate.of(2026, 2, 10);

        Transportation istToLhr = createTransportation(1L, istanbul, london, TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5));
        Transportation lhrToJfk = createTransportation(2L, london, newYork, TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5));
        List<Transportation> transportations = List.of(istToLhr, lhrToJfk);

        RouteResponse expectedRoute = createMockRouteResponse();

        when(transportationRepository.findAllWithLocations()).thenReturn(transportations);
        when(routeRules.canAddStep(anyList(), any(Transportation.class), eq(date))).thenReturn(true);
        when(routeRules.isCompleteValidRoute(List.of(istToLhr, lhrToJfk))).thenReturn(true);
        when(routeMapper.toRouteResponse(List.of(istToLhr, lhrToJfk))).thenReturn(expectedRoute);

        // When
        List<RouteResponse> result = routeService.findRoutes(originId, destinationId, date);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(expectedRoute);
        verify(routeRules).validateSearchInput(originId, destinationId);
        verify(transportationRepository).findAllWithLocations();
    }

    @Test
    void findRoutes_WithMultiModalTransport_ShouldReturnValidRoute() {
        // Given
        Long originId = 1L;
        Long destinationId = 3L;
        LocalDate date = LocalDate.of(2026, 2, 10);

        Transportation busToAirport = createTransportation(1L, istanbul, london, TransportationType.BUS, Set.of(1, 2, 3, 4, 5));
        Transportation flight = createTransportation(2L, london, paris, TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5));
        Transportation subwayFromAirport = createTransportation(3L, paris, newYork, TransportationType.SUBWAY, Set.of(1, 2, 3, 4, 5));
        List<Transportation> transportations = List.of(busToAirport, flight, subwayFromAirport);

        RouteResponse expectedRoute = createMockRouteResponse();

        when(transportationRepository.findAllWithLocations()).thenReturn(transportations);
        when(routeRules.canAddStep(anyList(), any(Transportation.class), eq(date))).thenReturn(true);
        when(routeRules.isCompleteValidRoute(List.of(busToAirport, flight, subwayFromAirport))).thenReturn(true);
        when(routeMapper.toRouteResponse(List.of(busToAirport, flight, subwayFromAirport))).thenReturn(expectedRoute);

        // When
        List<RouteResponse> result = routeService.findRoutes(originId, destinationId, date);

        // Then
        assertThat(result).hasSize(1);
        verify(routeRules).canAddStep(List.of(), busToAirport, date);
        verify(routeRules).canAddStep(List.of(busToAirport), flight, date);
        verify(routeRules).canAddStep(List.of(busToAirport, flight), subwayFromAirport, date);
    }

    @Test
    void findRoutes_WithInvalidInput_ShouldThrowException() {
        // Given
        Long originId = 1L;
        Long destinationId = 1L; // Same as origin

        doThrow(new BusinessRuleException("originId and destinationId cannot be the same."))
                .when(routeRules).validateSearchInput(originId, destinationId);

        // When & Then
        assertThatThrownBy(() -> routeService.findRoutes(originId, destinationId, null))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("originId and destinationId cannot be the same.");

        verify(routeRules).validateSearchInput(originId, destinationId);
        verify(transportationRepository, never()).findAllWithLocations();
    }

    @Test
    void findRoutes_WithNullOriginId_ShouldThrowException() {
        // Given
        Long originId = null;
        Long destinationId = 1L;

        doThrow(new BusinessRuleException("originId and destinationId are required."))
                .when(routeRules).validateSearchInput(originId, destinationId);

        // When & Then
        assertThatThrownBy(() -> routeService.findRoutes(originId, destinationId, null))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("originId and destinationId are required.");
    }

    @Test
    void findRoutes_WithNullDestinationId_ShouldThrowException() {
        // Given
        Long originId = 1L;
        Long destinationId = null;

        doThrow(new BusinessRuleException("originId and destinationId are required."))
                .when(routeRules).validateSearchInput(originId, destinationId);

        // When & Then
        assertThatThrownBy(() -> routeService.findRoutes(originId, destinationId, null))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("originId and destinationId are required.");
    }

    @Test
    void findRoutes_NoValidRoutes_ShouldReturnEmptyList() {
        // Given
        Long originId = 1L;
        Long destinationId = 999L; // Non-existing destination
        LocalDate date = LocalDate.of(2026, 2, 10);

        Transportation flight = createTransportation(1L, istanbul, london, TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5));
        List<Transportation> transportations = List.of(flight);

        when(transportationRepository.findAllWithLocations()).thenReturn(transportations);

        // When
        List<RouteResponse> result = routeService.findRoutes(originId, destinationId, date);

        // Then
        assertThat(result).isEmpty();
        verify(routeRules).validateSearchInput(originId, destinationId);
        verify(transportationRepository).findAllWithLocations();
    }

    @Test
    void findRoutes_WithDateRestriction_ShouldFilterByOperatingDays() {
        // Given
        Long originId = 1L;
        Long destinationId = 3L;
        LocalDate date = LocalDate.of(2026, 2, 10); // Monday

        Transportation flight = createTransportation(1L, istanbul, newYork, TransportationType.FLIGHT, Set.of(2, 3, 4, 5, 6)); // Tue-Sat only
        List<Transportation> transportations = List.of(flight);

        when(transportationRepository.findAllWithLocations()).thenReturn(transportations);
        when(routeRules.canAddStep(anyList(), eq(flight), eq(date))).thenReturn(false); // Date doesn't match operating days

        // When
        List<RouteResponse> result = routeService.findRoutes(originId, destinationId, date);

        // Then
        assertThat(result).isEmpty();
        verify(routeRules).canAddStep(List.of(), flight, date);
        verify(routeMapper, never()).toRouteResponse(any());
    }

    @Test
    void findRoutes_WithValidOperatingDay_ShouldReturnRoute() {
        // Given
        Long originId = 1L;
        Long destinationId = 3L;
        LocalDate date = LocalDate.of(2026, 2, 11); // Tuesday

        Transportation flight = createTransportation(1L, istanbul, newYork, TransportationType.FLIGHT, Set.of(2, 3, 4, 5, 6)); // Tue-Sat
        List<Transportation> transportations = List.of(flight);

        RouteResponse expectedRoute = createMockRouteResponse();

        when(transportationRepository.findAllWithLocations()).thenReturn(transportations);
        when(routeRules.canAddStep(anyList(), eq(flight), eq(date))).thenReturn(true);
        when(routeRules.isCompleteValidRoute(List.of(flight))).thenReturn(true);
        when(routeMapper.toRouteResponse(List.of(flight))).thenReturn(expectedRoute);

        // When
        List<RouteResponse> result = routeService.findRoutes(originId, destinationId, date);

        // Then
        assertThat(result).hasSize(1);
        verify(routeRules).canAddStep(List.of(), flight, date);
    }

    @Test
    void findRoutes_WithMaxStepsExceeded_ShouldNotReturnLongRoutes() {
        // Given
        Long originId = 1L;
        Long destinationId = 5L;
        LocalDate date = LocalDate.of(2026, 2, 10);

        Location location5 = createLocation(5L, "Test Airport", "Test City", "Test Country", "TST");

        Transportation step1 = createTransportation(1L, istanbul, london, TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5));
        Transportation step2 = createTransportation(2L, london, paris, TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5));
        Transportation step3 = createTransportation(3L, paris, newYork, TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5));
        Transportation step4 = createTransportation(4L, newYork, location5, TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5));

        List<Transportation> transportations = List.of(step1, step2, step3, step4);

        when(transportationRepository.findAllWithLocations()).thenReturn(transportations);
        when(routeRules.canAddStep(anyList(), any(Transportation.class), eq(date))).thenReturn(true);

        // When
        List<RouteResponse> result = routeService.findRoutes(originId, destinationId, date);

        // Then
        assertThat(result).isEmpty(); // Should not find route with more than 3 steps
    }


    @Test
    void findRoutes_WithNullDate_ShouldIgnoreDateValidation() {
        // Given
        Long originId = 1L;
        Long destinationId = 3L;

        Transportation flight = createTransportation(1L, istanbul, newYork, TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5));
        List<Transportation> transportations = List.of(flight);

        RouteResponse expectedRoute = createMockRouteResponse();

        when(transportationRepository.findAllWithLocations()).thenReturn(transportations);
        when(routeRules.canAddStep(anyList(), eq(flight), isNull())).thenReturn(true);
        when(routeRules.isCompleteValidRoute(List.of(flight))).thenReturn(true);
        when(routeMapper.toRouteResponse(List.of(flight))).thenReturn(expectedRoute);

        // When
        List<RouteResponse> result = routeService.findRoutes(originId, destinationId, null);

        // Then
        assertThat(result).hasSize(1);
        verify(routeRules).canAddStep(List.of(), flight, null);
    }

    @Test
    void findRoutes_WithIncompleteRoute_ShouldNotReturnIncompleteRoute() {
        // Given
        Long originId = 1L;
        Long destinationId = 3L;
        LocalDate date = LocalDate.of(2026, 2, 10);

        Transportation flight = createTransportation(1L, istanbul, newYork, TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5));
        List<Transportation> transportations = List.of(flight);

        when(transportationRepository.findAllWithLocations()).thenReturn(transportations);
        when(routeRules.canAddStep(anyList(), eq(flight), eq(date))).thenReturn(true);
        when(routeRules.isCompleteValidRoute(List.of(flight))).thenReturn(false); // Invalid route

        // When
        List<RouteResponse> result = routeService.findRoutes(originId, destinationId, date);

        // Then
        assertThat(result).isEmpty();
        verify(routeRules).isCompleteValidRoute(List.of(flight));
        verify(routeMapper, never()).toRouteResponse(any());
    }

    // Helper methods
    private Location createLocation(Long id, String name, String city, String country, String locationCode) {
        Location location = new Location();
        location.setId(id);
        location.setName(name);
        location.setCity(city);
        location.setCountry(country);
        location.setLocationCode(locationCode);
        return location;
    }

    private Transportation createTransportation(Long id, Location origin, Location destination,
                                              TransportationType type, Set<Integer> operatingDays) {
        Transportation transportation = new Transportation();
        transportation.setId(id);
        transportation.setOrigin(origin);
        transportation.setDestination(destination);
        transportation.setType(type);
        transportation.setOperatingDays(operatingDays);
        return transportation;
    }

    private RouteResponse createMockRouteResponse() {
        return mock(RouteResponse.class);
    }
}
