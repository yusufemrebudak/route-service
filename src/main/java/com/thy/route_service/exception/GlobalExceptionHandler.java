package com.thy.route_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(
            ApiException ex,
            HttpServletRequest request) {

        ApiErrorResponse err = new ApiErrorResponse(
                LocalDateTime.now(),
                ex.getStatus().value(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(ex.getStatus()).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleOther(
            Exception ex,
            HttpServletRequest request) {

        ApiErrorResponse err = new ApiErrorResponse(
                LocalDateTime.now(),
                500,
                "Unexpected error",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
}