✈️ Aviation Route Service – Backend

This project is a Spring Boot–based backend system for managing aviation and multi-modal transportation routes between locations.

It provides CRUD operations for locations and transportation links, route-finding logic(DFS algorithm), and business rule validations, exposed via RESTful APIs.

- The aim of this project is to build a backend system for the aviation domain that models transportation networks between locations and calculates all possible routes from a given origin to a destination. The system is designed to help users discover optimal travel options by combining different transportation types while ensuring data consistency and business rule validation.

* Tech Stack
  - Java
  - Spring Boot 
  - Spring Data JPA / Hibernate 
  - PostgreSQL 
  - Swagger / OpenAPI

* Core Features
  - Location CRUD operations
  - Transportation management between locations
  - Route discovery logic
  - Business rules layer
  - DTO + Mapper architecture
  - Global exception handling
  - Enum-based transportation types
  - Database-level constraints and validation
  - Swagger / OpenAPI documentation


// SQL Script to initialize the database with sample data
-- =========================
INSERT INTO locations
(name, city, country, location_code, created_at, updated_at, version)
VALUES
('İstanbul Airport',        'İstanbul', 'Türkiye', 'IST', now(), now(), 0),
('Sabiha Gökçen Airport',   'İstanbul', 'Türkiye', 'SAW', now(), now(), 0),
('Ankara Esenboğa Airport', 'Ankara',   'Türkiye', 'ESB', now(), now(), 0),
('Taksim Square',          'İstanbul', 'Türkiye', 'TAK', now(), now(), 0),
('Kızılay Square',        'Ankara',   'Türkiye', 'KIZ', now(), now(), 0);

-- =========================
-- TRANSPORTATIONS
-- =========================
WITH l AS (
SELECT
MAX(CASE WHEN location_code='IST' THEN id END) AS ist,
MAX(CASE WHEN location_code='SAW' THEN id END) AS saw,
MAX(CASE WHEN location_code='ESB' THEN id END) AS esb,
MAX(CASE WHEN location_code='TAK' THEN id END) AS tak,
MAX(CASE WHEN location_code='KIZ' THEN id END) AS kiz
FROM locations
)
INSERT INTO transportations
(origin_location_id, destination_location_id, type, created_at, updated_at, version)
VALUES
((SELECT tak FROM l), (SELECT ist FROM l), 'UBER',   now(), now(), 0),
((SELECT ist FROM l), (SELECT esb FROM l), 'FLIGHT', now(), now(), 0),
((SELECT esb FROM l), (SELECT kiz FROM l), 'BUS',    now(), now(), 0),
((SELECT tak FROM l), (SELECT saw FROM l), 'SUBWAY', now(), now(), 0),
((SELECT saw FROM l), (SELECT esb FROM l), 'FLIGHT', now(), now(), 0);

-- =========================
-- OPERATING DAYS
-- =========================

-- FLIGHT: 1-7
INSERT INTO transportation_operating_days (transportation_id, day)
SELECT t.id, d.day
FROM transportations t
CROSS JOIN (VALUES (1),(2),(3),(4),(5),(6),(7)) d(day)
WHERE t.type = 'FLIGHT';

-- BUS: 1-5
INSERT INTO transportation_operating_days (transportation_id, day)
SELECT t.id, d.day
FROM transportations t
CROSS JOIN (VALUES (1),(2),(3),(4),(5)) d(day)
WHERE t.type = 'BUS';

-- UBER + SUBWAY: 1-7
INSERT INTO transportation_operating_days (transportation_id, day)
SELECT t.id, d.day
FROM transportations t
CROSS JOIN (VALUES (1),(2),(3),(4),(5),(6),(7)) d(day)
WHERE t.type IN ('UBER','SUBWAY');