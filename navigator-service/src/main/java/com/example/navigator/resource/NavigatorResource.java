package com.example.navigator.resource;

import com.example.navigator.dto.*;
import com.example.navigator.service.NavigatorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/navigator")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class NavigatorResource {

    @Inject
    private NavigatorService navigatorService;

    /**
     * Найти маршруты между локациями по ID.
     * orderBy: строка в формате "field,direction" (например "distance,asc" или "name,desc")
     */
    @GET
    @Path("/routes/{idFrom}/{idTo}/{orderBy}")
    public Response findRoutesBetween(
            @PathParam("idFrom") Long idFrom,
            @PathParam("idTo") Long idTo,
            @PathParam("orderBy") String orderBy) {

        // Получаем список маршрутов из первого сервиса
        List<Route> found;
        try {
            found = navigatorService.findRoutesBetween(idFrom, idTo, orderBy);
        } catch (Exception e) {
            return Response.serverError()
                    .entity("<error>" + e.getMessage() + "</error>")
                    .build();
        }

        RouteListResponse response = new RouteListResponse();
        response.setRoutes(found);
        response.setTotalElements((long) (found == null ? 0 : found.size()));
        response.setCurrentPage(0);
        response.setPageSize(found == null ? 0 : found.size());
        response.setTotalPages(1);

        return Response.ok(response).build();
    }

    /**
     * Добавить новый маршрут между локациями, взятыми из маршрутов idFrom и idTo (копируем дескрипторы from/to).
     */
    @POST
    @Path("/route/add/{idFrom}/{idTo}/{distance}")
    public Response addRouteBetween(
            @PathParam("idFrom") Long idFrom,
            @PathParam("idTo") Long idTo,
            @PathParam("distance") Double distance) {

        Route fromRoute = navigatorService.getRouteById(idFrom);
        Route toRoute = navigatorService.getRouteById(idTo);

        if (fromRoute == null || toRoute == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        FromLocation fromLoc = fromRoute.getFromLocation();
        ToLocation toLoc = toRoute.getToLocation();
        Coordinates coords = fromRoute.getCoordinates();

        String name = String.format("Nav route from %s to %s",
                fromLoc != null ? fromLoc.getName() : "unknown",
                toLoc != null ? toLoc.getName() : "unknown");

        RouteCreateRequest create = new RouteCreateRequest(
                name,
                coords,
                fromLoc,
                toLoc,
                distance
        );

        Route created;
        try {
            created = navigatorService.createRoute(create);
        } catch (Exception e) {
            return Response.serverError()
                    .entity("<error>" + e.getMessage() + "</error>")
                    .build();
        }

        URI location = URI.create(String.format("/navigator/routes/%d", created.getId()));
        return Response.created(location).entity(created).build();
    }
}
