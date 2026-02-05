package com.thy.route_service.entity;

import com.thy.route_service.entity.enums.TransportationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "transportations")
@Getter
@Setter
@NoArgsConstructor
public class Transportation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "origin_location_id", nullable = false)
    private Location origin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_location_id", nullable = false)
    private Location destination;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransportationType type;

    // Bonus: operating days (1=Mon ... 7=Sun)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "transportation_operating_days",
            joinColumns = @JoinColumn(name = "transportation_id")
    )
    @Column(name = "day", nullable = false)
    private Set<Integer> operatingDays = new HashSet<>();
}