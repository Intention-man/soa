package com.example.routeservice.service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.routeservice.dto.DistanceGroupResponse;
import com.example.routeservice.dto.DistanceSumResponse;
import com.example.routeservice.dto.FilteredRoutesResponse;
import com.example.routeservice.dto.RouteCreateRequest;
import com.example.routeservice.dto.RouteListResponse;
import com.example.routeservice.dto.RouteUpdateRequest;
import com.example.routeservice.entity.Coordinates;
import com.example.routeservice.entity.FromLocation;
import com.example.routeservice.entity.Route;
import com.example.routeservice.entity.ToLocation;
import com.example.routeservice.exception.RouteNotFoundException;
import com.example.routeservice.exception.ValidationException;
import com.example.routeservice.filter.RouteFilter;
import com.example.routeservice.repository.RouteRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@ApplicationScoped
@Transactional
public class RouteServiceImpl implements RouteService {

    @Inject
    private RouteRepository routeRepository;

    @Inject
    private Validator validator;

    @PersistenceContext
    private EntityManager entityManager;

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
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации данных",
                    violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.toList()));
        }

        Route route = new Route();
        route.setName(request.getName());

        if (request.getCoordinates() != null) {
            route.setCoordinates(mergeOrPersist(request.getCoordinates()));
        }
        if (request.getFromLocation() != null) {
            route.setFromLocation(mergeOrPersist(request.getFromLocation()));
        }
        if (request.getToLocation() != null) {
            route.setToLocation(mergeOrPersist(request.getToLocation()));
        }

        route.setDistance(request.getDistance());

        return routeRepository.save(route);
    }

    @Transactional
    @Override
    public Route updateRoute(Long id, RouteUpdateRequest request) {
        System.out.println(request);

        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ValidationException("Ошибка валидации данных",
                    violations.stream()
                            .map(ConstraintViolation::getMessage)
                            .collect(Collectors.toList()));
        }

        Route route = getRouteById(id);
        System.out.println(entityManager.contains(route));

        if (request.getName() != null) {
            route.setName(request.getName());
        }
        if (request.getDistance() != null) {
            route.setDistance(request.getDistance());
        }

        if (request.getCoordinates() != null) {
            route.setCoordinates(mergeOrPersist(request.getCoordinates()));
        }
        if (request.getFromLocation() != null) {
            route.setFromLocation(mergeOrPersist(request.getFromLocation()));
        }
        if (request.getToLocation() != null) {
            route.setToLocation(mergeOrPersist(request.getToLocation()));
        }

        entityManager.flush();
        return route;
    }

    private <T> T mergeOrPersist(T entity) {
        if (entity == null) {
            return null;
        }

        Object id = getEntityId(entity);

        if (id == null) {
            entityManager.persist(entity);
            return entity;
        }
        if (entityManager.contains(entity)) {
            return entity;
        }
        if (entityManager.find(entity.getClass(), getEntityId(entity)) != null) {
            return entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
            return entity;
        }

    }

    private Object getEntityId(Object entity) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            return field.get(entity);
        } catch (Exception e) {
            return null;
        }
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
        Double avg = count > 0 ? totalSum / count : 0.0;

        return new DistanceSumResponse(totalSum, count.intValue(), avg, minDistance, maxDistance);
    }

    @Override
    public DistanceGroupResponse groupByDistance() {
        List<Object[]> groups = routeRepository.groupByDistance();
        Long totalRoutes = routeRepository.count(new RouteFilter());

        List<DistanceGroupResponse.DistanceGroup> list = groups.stream()
                .map(g -> new DistanceGroupResponse.DistanceGroup(
                        (Double) g[0],
                        ((Long) g[1]).intValue(),
                        totalRoutes > 0 ? ((Long) g[1]).doubleValue() / totalRoutes * 100 : 0.0))
                .collect(Collectors.toList());

        return new DistanceGroupResponse(list, list.size(), totalRoutes.intValue());
    }

    @Override
    public FilteredRoutesResponse getRoutesWithDistanceGreaterThan(Double minDistance) {
        if (minDistance == null || minDistance <= 1) {
            throw new ValidationException("Некорректное значение параметра", List.of("minDistance должен быть > 1"));
        }

        List<Route> routes = routeRepository.findByDistanceGreaterThan(minDistance);
        Double min = routes.stream().map(Route::getDistance).min(Double::compare).orElse(0.0);
        Double max = routes.stream().map(Route::getDistance).max(Double::compare).orElse(0.0);
        Double avg = routes.stream().mapToDouble(Route::getDistance).average().orElse(0.0);

        return new FilteredRoutesResponse(routes, routes.size(), min, max, avg);
    }

    public FromLocation getFromLocationById(Long id) {
        return entityManager.find(FromLocation.class, id);
    }

    public ToLocation getToLocationById(Long id) {
        return entityManager.find(ToLocation.class, id);
    }

    public Coordinates getMidCoords(FromLocation fromLoc, ToLocation toLoc) {
        Integer midX = 0;
        long midY = 0L;

        if (fromLoc != null && toLoc != null) {
            midX = (int) ((fromLoc.getX() + toLoc.getX()) / 2.0);
            midY = (long) ((fromLoc.getY() + toLoc.getY()) / 2.0);
        } else if (fromLoc != null) {
            midX = fromLoc.getX();
            midY = fromLoc.getY();
        } else if (toLoc != null) {
            midX = Math.toIntExact(toLoc.getX());
            midY = toLoc.getY();
        }

        return new Coordinates(midX, midY);
    }

}