package com.example.routeservice.repository;

import com.example.routeservice.entity.Route;
import com.example.routeservice.filter.RouteFilter;
import java.util.List;
import java.util.Optional;

public interface RouteRepository {
    List<Route> findAll(RouteFilter filter);
    Optional<Route> findById(Long id);
    Route save(Route route);
    Route update(Route route);
    void delete(Long id);
    Long count(RouteFilter filter);
    Object[] getDistanceSum();
    List<Object[]> groupByDistance();
    List<Route> findByDistanceGreaterThan(Double minDistance);
}