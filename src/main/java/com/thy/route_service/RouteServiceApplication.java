package com.thy.route_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class RouteServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RouteServiceApplication.class, args);
	}

}
