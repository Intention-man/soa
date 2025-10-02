package com.example.routeservice.service;

import com.example.routeservice.dto.DistanceGroupResponse;
import com.example.routeservice.dto.DistanceSumResponse;
import com.example.routeservice.dto.FilteredRoutesResponse;
import com.example.routeservice.dto.RouteCreateRequest;
import com.example.routeservice.dto.RouteListResponse;
import com.example.routeservice.dto.RouteUpdateRequest;
import com.example.routeservice.entity.Route;
import com.example.routeservice.filter.RouteFilter;

public interface RouteService {
    RouteListResponse getRoutes(RouteFilter filter);

    Route getRouteById(Long id);

    Route createRoute(RouteCreateRequest request);

    Route updateRoute(Long id, RouteUpdateRequest request);

    void deleteRoute(Long id);

    DistanceSumResponse getDistanceSum();

    DistanceGroupResponse groupByDistance();

    FilteredRoutesResponse getRoutesWithDistanceGreaterThan(Double minDistance);
}