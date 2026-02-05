package com.thy.route_service.service;


import com.thy.route_service.dto.location.request.LocationCreateRequest;
import com.thy.route_service.dto.location.request.LocationUpdateRequest;
import com.thy.route_service.dto.location.response.LocationResponse;

import java.util.List;

public interface LocationService {

    LocationResponse create(LocationCreateRequest request);

    LocationResponse update(Long id, LocationUpdateRequest request);

    LocationResponse getById(Long id);

    List<LocationResponse> getAll();

    void delete(Long id);
}