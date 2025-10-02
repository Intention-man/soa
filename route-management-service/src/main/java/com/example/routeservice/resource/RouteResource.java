package com.example.routeservice.resource;

import java.net.URI;
import java.util.Map;

import com.example.routeservice.dto.DistanceGroupResponse;
import com.example.routeservice.dto.DistanceSumResponse;
import com.example.routeservice.dto.FilteredRoutesResponse;
import com.example.routeservice.dto.RouteCreateRequest;
import com.example.routeservice.dto.RouteListResponse;
import com.example.routeservice.dto.RouteUpdateRequest;
import com.example.routeservice.entity.Route;
import com.example.routeservice.filter.RouteFilter;
import com.example.routeservice.service.RouteService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/routes")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class RouteResource {

    @Inject
    private RouteService routeService;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getRoutes(
            @QueryParam("page") @DefaultValue("0") Integer page,
            @QueryParam("size") @DefaultValue("20") Integer size,
            @QueryParam("sort") String[] sort) {

        // Получаем все параметры фильтрации
        Map<String, String> filterParams = uriInfo.getQueryParameters().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("filter."))
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get(0)
                ));

        RouteFilter filter = new RouteFilter(page, size, sort, filterParams);
        RouteListResponse response = routeService.getRoutes(filter);

        return Response.ok(response).build();
    }

    @POST
    public Response createRoute(RouteCreateRequest request) {
        Route createdRoute = routeService.createRoute(request);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(createdRoute.getId().toString())
                .build();

        return Response.created(location)
                .entity(createdRoute)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getRouteById(@PathParam("id") Long id) {
        Route route = routeService.getRouteById(id);
        return Response.ok(route).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateRoute(@PathParam("id") Long id, RouteUpdateRequest request) {
        Route updatedRoute = routeService.updateRoute(id, request);
        return Response.ok(updatedRoute).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteRoute(@PathParam("id") Long id) {
        routeService.deleteRoute(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/distance/sum")
    public Response getDistanceSum() {
        DistanceSumResponse response = routeService.getDistanceSum();
        return Response.ok(response).build();
    }

    @GET
    @Path("/distance/group")
    public Response groupByDistance() {
        DistanceGroupResponse response = routeService.groupByDistance();
        return Response.ok(response).build();
    }

    @GET
    @Path("/distance/greater-than")
    public Response getRoutesWithDistanceGreaterThan(@QueryParam("minDistance") Double minDistance) {
        FilteredRoutesResponse response = routeService.getRoutesWithDistanceGreaterThan(minDistance);
        return Response.ok(response).build();
    }
}