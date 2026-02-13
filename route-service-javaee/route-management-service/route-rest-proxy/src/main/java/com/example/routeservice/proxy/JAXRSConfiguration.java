package com.example.routeservice.proxy;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.Set;

@ApplicationPath("/")
public class JAXRSConfiguration extends Application {
    public Set<Class<?>> getClasses() {
        return Set.of(RouteProxyResource.class, ApplicationWadlResource.class);
    }
}
