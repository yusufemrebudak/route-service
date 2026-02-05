package com.thy.route_service.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends ApiException {

    public BusinessRuleException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}