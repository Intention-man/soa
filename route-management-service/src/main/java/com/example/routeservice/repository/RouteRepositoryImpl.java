package com.example.routeservice.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.routeservice.entity.Route;
import com.example.routeservice.filter.RouteFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@ApplicationScoped
public class RouteRepositoryImpl implements RouteRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Route> findAll(RouteFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Route> cq = cb.createQuery(Route.class);
        Root<Route> root = cq.from(Route.class);

        applyFilters(cb, cq, root, filter);

        applySorting(cb, cq, root, filter);

        TypedQuery<Route> query = entityManager.createQuery(cq);

        if (filter.getPage() != null && filter.getSize() != null) {
            query.setFirstResult(filter.getPage() * filter.getSize());
            query.setMaxResults(filter.getSize());
        }

        return query.getResultList();
    }

    @Override
    public Optional<Route> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Route.class, id));
    }

    @Override
    public Route save(Route route) {
        if (route.getId() == null) {
            entityManager.persist(route);
            return route;
        } else {
            return entityManager.merge(route);
        }
    }

    @Override
    public Route update(Route route) {
        return entityManager.merge(route);
    }

    @Override
    public void delete(Long id) {
        Route route = entityManager.find(Route.class, id);
        if (route != null) {
            entityManager.remove(route);
        }
    }

    @Override
    public Long count(RouteFilter filter) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Route> root = cq.from(Route.class);

        cq.select(cb.count(root));

        applyFilters(cb, cq, root, filter);

        return entityManager.createQuery(cq).getSingleResult();
    }

    @Override
    public Object[] getDistanceSum() {
        return (Object[]) entityManager.createNamedQuery("Route.sumDistance").getSingleResult();
    }

    @Override
    public List<Object[]> groupByDistance() {
        return entityManager.createNamedQuery("Route.groupByDistance").getResultList();
    }

    @Override
    public List<Route> findByDistanceGreaterThan(Double minDistance) {
        return entityManager.createNamedQuery("Route.findByDistanceGreaterThan")
                .setParameter("minDistance", minDistance)
                .getResultList();
    }

    private void applyFilters(CriteriaBuilder cb, CriteriaQuery<?> cq,
                              Root<Route> root, RouteFilter filter) {
        if (filter.getFilterParams() == null || filter.getFilterParams().isEmpty()) {
            return;
        }

        List<Predicate> predicates = new ArrayList<>();

        for (Map.Entry<String, String> entry : filter.getFilterParams().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.trim().isEmpty()) {
                continue;
            }

            if (key.startsWith("filter.")) {
                String fieldOp = key.substring(7); // name.contains
                String[] parts = fieldOp.split("\\.");
                String field = parts[0];
                String operation = parts.length > 1 ? parts[1] : "equals";

                Path<?> path = resolveFieldPath(root, field);

                switch (operation) {
                    case "equals" -> predicates.add(cb.equal(path, parseValue(field, value)));
                    case "contains" -> predicates.add(
                            cb.like(cb.lower(path.as(String.class)), "%" + value.toLowerCase() + "%"));
                    case "gt", "gte", "lt", "lte", "min", "max" -> {
                        Object parsed = parseValue(field, value);
                        Predicate p = buildComparisonPredicate(cb, path, operation, parsed);
                        if (p != null) predicates.add(p);
                    }
                    default -> { /* ignore */ }
                }
            }
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }
    }

    private void applySorting(CriteriaBuilder cb, CriteriaQuery<Route> cq,
                              Root<Route> root, RouteFilter filter) {
        if (filter.getSort() != null && filter.getSort().length > 0) {
            List<Order> orders = new ArrayList<>();

            for (String sortParam : filter.getSort()) {
                String[] parts = sortParam.split(",");
                if (parts.length == 2) {
                    String field = parts[0];
                    String direction = parts[1];

                    Path<?> path = resolveFieldPath(root, field);

                    if ("asc".equalsIgnoreCase(direction)) {
                        orders.add(cb.asc(path));
                    } else if ("desc".equalsIgnoreCase(direction)) {
                        orders.add(cb.desc(path));
                    }
                }
            }

            if (!orders.isEmpty()) {
                cq.orderBy(orders);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate buildComparisonPredicate(CriteriaBuilder cb, Path<?> path,
                                               String operation, Object parsed) {
        if (parsed instanceof Number num) {
            return switch (operation) {
                case "gt" -> cb.gt(path.as(Number.class), num);
                case "gte", "min" -> cb.ge(path.as(Number.class), num);
                case "lt" -> cb.lt(path.as(Number.class), num);
                case "lte", "max" -> cb.le(path.as(Number.class), num);
                default -> null;
            };
        } else if (parsed instanceof Comparable comp) {
            // LocalDateTime, Date, String (если надо сравнивать), и т.д.
            return switch (operation) {
                case "gt" -> cb.greaterThan((Path<? extends Comparable>) path, comp);
                case "gte", "min" -> cb.greaterThanOrEqualTo((Path<? extends Comparable>) path, comp);
                case "lt" -> cb.lessThan((Path<? extends Comparable>) path, comp);
                case "lte", "max" -> cb.lessThanOrEqualTo((Path<? extends Comparable>) path, comp);
                default -> null;
            };
        }
        return null;
    }

    private Path<?> resolveFieldPath(Root<Route> root, String field) {
        return switch (field) {
            case "coordinatesX" -> root.get("coordinates").get("x");
            case "coordinatesY" -> root.get("coordinates").get("y");
            case "fromId" -> root.get("fromLocation").get("id");
            case "fromX" -> root.get("fromLocation").get("x");
            case "fromY" -> root.get("fromLocation").get("y");
            case "fromName" -> root.get("fromLocation").get("name");
            case "toId" -> root.get("toLocation").get("id");
            case "toX" -> root.get("toLocation").get("x");
            case "toY" -> root.get("toLocation").get("y");
            case "toName" -> root.get("toLocation").get("name");
            default -> root.get(field);
        };
    }

    private Object parseValue(String field, String value) {
        try {
            if (field.endsWith("X") || field.endsWith("Y")) {
                return Long.parseLong(value);
            } else if (field.equals("distance")) {
                return Double.parseDouble(value);
            } else if (field.equals("creationDate")) {
                return java.time.LocalDateTime.parse(value);
            }
        } catch (NumberFormatException ignored) {
        }
        return value;
    }
}