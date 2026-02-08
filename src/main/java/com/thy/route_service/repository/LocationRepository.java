package com.thy.route_service.repository;

import com.thy.route_service.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLocationCode(String locationCode);

    Optional<Location> findByNameAndCity(String name, String city);
    boolean existsByLocationCode(String locationCode);

    boolean existsByNameAndCity(String name, String city);

}
