package com.example.navigator.service;

import com.example.navigator.dto.RouteListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NavigatorService {

    @Value("${route-management.base-url}")
    private String targetServiceBaseUrl;

    @Autowired
    private RestTemplate restTemplate;


    public RouteListResponse findRoutesBetween(Long fromId, Long toId, String orderBy) {

        String sortParam = (orderBy != null && !orderBy.isBlank())
                ? orderBy + ",asc"
                : "id,asc";

        String url = String.format("%s/routes?" +
                        "page=0&size=1000&filter.fromId.equals=%d&filter.toId.equals=%d&sort=%s",
                targetServiceBaseUrl, fromId, toId, sortParam);


        return restTemplate.getForObject(
                url,
                RouteListResponse.class
        );
    }

    public String addRouteBetween(Long idFrom, Long idTo, Double distance) {
        String url = String.format("%s/routes/add/%d/%d/%s",
                targetServiceBaseUrl, idFrom, idTo, distance);

        return restTemplate.postForObject(
                url,
                null,
                String.class
        );
    }
}