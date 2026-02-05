package com.thy.route_service.exception;


import java.time.LocalDateTime;

public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String message,
        String path
) {}