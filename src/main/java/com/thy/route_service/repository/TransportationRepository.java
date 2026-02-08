package com.thy.route_service.repository;

import com.thy.route_service.entity.Transportation;
import com.thy.route_service.entity.enums.TransportationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransportationRepository extends JpaRepository<Transportation, Long> {
    List<Transportation> findByOrigin_Id(Long originId);

    @Query("""
        select t from Transportation t
        join fetch t.origin
        join fetch t.destination
    """)
    List<Transportation> findAllWithLocations();


    // boolean existsByOriginIdOrDestinationId(Long id,Long id2);

    @Query("select count(t)>0 from Transportation t where t.origin.id=:id or t.destination.id=:id")
    boolean existsByOriginIdOrDestinationId(Long id);

    Optional<Transportation> findByOriginIdAndDestinationIdAndType(Long originId, Long destinationId, TransportationType type);


}