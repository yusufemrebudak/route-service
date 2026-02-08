package com.thy.route_service.service;

import com.thy.route_service.dto.transportation.request.TransportationCreateRequest;
import com.thy.route_service.dto.transportation.request.TransportationUpdateRequest;
import com.thy.route_service.dto.transportation.response.TransportationResponse;
import com.thy.route_service.dto.transportation.response.TransportationTypeResponse;

import java.util.List;

public interface TransportationService {
    TransportationResponse create(TransportationCreateRequest request);
    TransportationResponse update(Long id, TransportationUpdateRequest request);
    TransportationResponse getById(Long id);
    List<TransportationResponse> getAll();
    void delete(Long id);
    List<TransportationTypeResponse> getAllTypes();
}