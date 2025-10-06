package com.example.navigator.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.example.navigator.dto.Route;
import com.example.navigator.dto.RouteListResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

    public List<Route> findRoutesBetween(Long fromId, Long toId, String orderBy) {
        StringBuilder query = new StringBuilder();
        query.append("page=0&size=1000");
        query.append("&filter.fromId.equals=").append(fromId);
        query.append("&filter.toId.equals=").append(toId);

        if (orderBy != null && !orderBy.isBlank()) {
            query.append("&sort=").append(encode(orderBy));
        }

        String url = firstServiceBase + "/routes?" + query;

        Response r = client.target(url)
                .request(MediaType.APPLICATION_XML)
                .get();

        if (r.getStatus() / 100 != 2) {
            throw new RuntimeException("Failed to query routes, status=" + r.getStatus());
        }

        RouteListResponse resp = r.readEntity(RouteListResponse.class);
        return resp.getRoutes();
    }

    private static String encode(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
