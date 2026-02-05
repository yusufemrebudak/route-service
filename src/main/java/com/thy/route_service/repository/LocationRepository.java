package com.thy.route_service.repository;

import com.thy.route_service.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLocationCode(String locationCode);

    boolean existsByLocationCode(String locationCode);
}
