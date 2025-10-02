package com.example.routeservice.service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.routeservice.dto.DistanceGroupResponse;
import com.example.routeservice.dto.DistanceSumResponse;
import com.example.routeservice.dto.FilteredRoutesResponse;
import com.example.routeservice.dto.RouteCreateRequest;
import com.example.routeservice.dto.RouteListResponse;
import com.example.routeservice.dto.RouteUpdateRequest;
import com.example.routeservice.entity.Route;
import com.example.routeservice.exception.RouteNotFoundException;
import com.example.routeservice.exception.ValidationException;
import com.example.routeservice.filter.RouteFilter;
import com.example.routeservice.repository.RouteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;

@ApplicationScoped
@Transactional
public class RouteServiceImpl implements RouteService {

    @Inject
    private RouteRepository routeRepository;

    @Inject
    private Validator validator;

    @Override
    public RouteListResponse getRoutes(RouteFilter filter) {
        List<Route> routes = routeRepository.findAll(filter);
        Long totalElements = routeRepository.count(filter);

        int totalPages = (int) Math.ceil((double) totalElements / filter.getSize());

        return new RouteListResponse(
                routes,
                totalElements,
                totalPages,
                filter.getPage(),
                filter.getSize()
        );
    }

    @Override
    public Route getRouteById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Маршрут с ID=" + id + " не существует"));
    }

    @Override
    public Route createRoute(RouteCreateRequest request) {
        // Валидация
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации данных",
                    violations.stream()
                            .map(v -> v.getMessage())
                            .collect(Collectors.toList()));
        }

        Route route = new Route();
        route.setName(request.getName());
        route.setCoordinates(request.getCoordinates());
        route.setFromLocation(request.getFromLocation());
        route.setToLocation(request.getToLocation());
        route.setDistance(request.getDistance());

        return routeRepository.save(route);
    }

    @Override
    public Route updateRoute(Long id, RouteUpdateRequest request) {
        // Валидация
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации данных",
                    violations.stream()
                            .map(v -> v.getMessage())
                            .collect(Collectors.toList()));
        }

        Route existingRoute = getRouteById(id);

        existingRoute.setName(request.getName());
        existingRoute.setCoordinates(request.getCoordinates());
        existingRoute.setFromLocation(request.getFromLocation());
        existingRoute.setToLocation(request.getToLocation());
        existingRoute.setDistance(request.getDistance());

        return routeRepository.update(existingRoute);
    }

    @Override
    public void deleteRoute(Long id) {
        Route route = getRouteById(id);
        routeRepository.delete(id);
    }

    @Override
    public DistanceSumResponse getDistanceSum() {
        Object[] result = routeRepository.getDistanceSum();

        Double totalSum = (Double) result[0];
        Long count = (Long) result[1];
        Double minDistance = (Double) result[2];
        Double maxDistance = (Double) result[3];

        Double averageDistance = count > 0 ? totalSum / count : 0.0;

        return new DistanceSumResponse(
                totalSum,
                count.intValue(),
                averageDistance,
                minDistance,
                maxDistance
        );
    }

    @Override
    public DistanceGroupResponse groupByDistance() {
        List<Object[]> groups = routeRepository.groupByDistance();
        Long totalRoutes = routeRepository.count(new RouteFilter());

        List<DistanceGroupResponse.DistanceGroup> distanceGroups = groups.stream()
                .map(group -> {
                    Double distance = (Double) group[0];
                    Long count = (Long) group[1];
                    Double percentage = totalRoutes > 0 ? (count.doubleValue() / totalRoutes) * 100 : 0.0;

                    return new DistanceGroupResponse.DistanceGroup(
                            distance,
                            count.intValue(),
                            percentage
                    );
                })
                .collect(Collectors.toList());

        return new DistanceGroupResponse(
                distanceGroups,
                distanceGroups.size(),
                totalRoutes.intValue()
        );
    }

    @Override
    public FilteredRoutesResponse getRoutesWithDistanceGreaterThan(Double minDistance) {
        if (minDistance == null || minDistance <= 1) {
            throw new ValidationException("Некорректное значение параметра",
                    List.of("minDistance должен быть больше 1"));
        }

        List<Route> routes = routeRepository.findByDistanceGreaterThan(minDistance);

        // Вычисляем статистику
        Double minDist = routes.stream()
                .map(Route::getDistance)
                .min(Double::compare)
                .orElse(0.0);

        Double maxDist = routes.stream()
                .map(Route::getDistance)
                .max(Double::compare)
                .orElse(0.0);

        Double avgDist = routes.stream()
                .mapToDouble(Route::getDistance)
                .average()
                .orElse(0.0);

        return new FilteredRoutesResponse(
                routes,
                routes.size(),
                minDist,
                maxDist,
                avgDist
        );
    }
}