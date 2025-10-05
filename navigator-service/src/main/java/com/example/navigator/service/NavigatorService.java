package com.example.navigator.service;

import com.example.navigator.dto.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@ApplicationScoped
public class NavigatorService {

    private final String firstServiceBase;
    private final Client client = ClientBuilder.newClient();

    public NavigatorService() {
        String env = System.getenv("FIRST_SERVICE_BASE_URL");
        if (env == null || env.isBlank()) {
            env = "http://localhost:8080/route-management-service";
        }
        this.firstServiceBase = env;
    }

    // Получить один маршрут из первого сервиса по ID
    public Route getRouteById(Long id) {
        String url = String.format("%s/routes/%d", firstServiceBase, id);
        Response r = client.target(url)
                .request(MediaType.APPLICATION_XML)
                .get();
        if (r.getStatus() / 100 != 2) {
            throw new RuntimeException("Failed to fetch route id=" + id + ", status=" + r.getStatus());
        }
        return r.readEntity(Route.class);
    }

    // Найти маршруты по idFrom и idTo, с сортировкой orderBy (например "distance,asc")
    public List<Route> findRoutesBetween(Long idFrom, Long idTo, String orderBy) {
        String q = String.format(
                "page=0&size=1000&filter.idFrom.eq=%d&filter.idTo.eq=%d",
                idFrom, idTo
        );
        if (orderBy != null && !orderBy.isBlank()) {
            q += "&sort=" + encode(orderBy);
        }

        String url = firstServiceBase + "/routes?" + q;

        Response r = client.target(url)
                .request(MediaType.APPLICATION_XML)
                .get();
        if (r.getStatus() / 100 != 2) {
            throw new RuntimeException("Failed to query routes, status=" + r.getStatus());
        }
        RouteListResponse resp = r.readEntity(RouteListResponse.class);
        return resp.getRoutes();
    }

    // Создать новый маршрут в первом сервисе
    public Route createRoute(RouteCreateRequest request) {
        String url = firstServiceBase + "/routes";
        Response r = client.target(url)
                .request(MediaType.APPLICATION_XML)
                .post(Entity.entity(request, MediaType.APPLICATION_XML));
        if (r.getStatus() / 100 != 2 && r.getStatus() != 201) {
            throw new RuntimeException("Failed to create route, status=" + r.getStatus());
        }
        return r.readEntity(Route.class);
    }

    private static String encode(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
