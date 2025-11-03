package com.example.routeservice.exception;

import java.io.Serializable;

public class RouteNotFoundException extends RuntimeException implements Serializable {
    public RouteNotFoundException(String message) {
        super(message);
    }
}
