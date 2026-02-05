package com.thy.route_service.repository;

import com.thy.route_service.entity.Transportation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransportationRepository extends JpaRepository<Transportation, Long> {
    List<Transportation> findByOrigin_Id(Long originId);

    @Query("""
        select t from Transportation t
        join fetch t.origin
        join fetch t.destination
    """)
    List<Transportation> findAllWithLocations();
}