package com.thy.route_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location extends BaseEntity{



    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String country;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(name = "location_code", nullable = false, unique = true, length = 10)
    private String locationCode;

}