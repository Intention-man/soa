package com.example.routeservice.proxy;

import java.util.Set;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/")
public class JAXRSConfiguration extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(
                RouteProxyResource.class,
                ApplicationWadlResource.class
        );
    }
}

