package com.example.navigator.resource;

import java.util.List;

import com.example.navigator.dto.Route;
import com.example.navigator.dto.RouteListResponse;
import com.example.navigator.service.NavigatorService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

    @POST
    @Path("/route/add/{idFrom}/{idTo}/{distance}")
    public Response addRouteBetween(
            @PathParam("idFrom") Long idFrom,
            @PathParam("idTo") Long idTo,
            @PathParam("distance") Double distance) {

        String targetUrl = String.format(
                "http://localhost:8080/route-management-service/routes/add/%d/%d/%s",
                idFrom, idTo, distance.toString()
        );

        try {
            jakarta.ws.rs.client.Client client = jakarta.ws.rs.client.ClientBuilder.newClient();

            Response response = client
                    .target(targetUrl)
                    .request(MediaType.APPLICATION_XML)
                    .post(null);

            return Response.status(response.getStatus())
                    .entity(response.readEntity(String.class))
                    .type(MediaType.APPLICATION_XML)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity("<error>" + e.getMessage() + "</error>")
                    .build();
        }
    }
//
//    /**
//     * Добавить новый маршрут между локациями, взятыми из маршрутов idFrom и idTo (копируем дескрипторы from/to).
//     */
//    @POST
//    @Path("/route/add/{idFrom}/{idTo}/{distance}")
//    public Response addRouteBetween(
//            @PathParam("idFrom") Long idFrom,
//            @PathParam("idTo") Long idTo,
//            @PathParam("distance") Double distance) {
//
//        Route fromRoute = navigatorService.getRouteById(idFrom);
//        Route toRoute = navigatorService.getRouteById(idTo);
//
//        if (fromRoute == null || toRoute == null) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//
//        try {
//            Route created = navigatorService.createRoute(distance, fromRoute, toRoute);
//            URI location = URI.create(String.format("/navigator/route/%d", created.getId()));
//            return Response.created(location).entity(created).build();
//        } catch (Exception e) {
//            return Response.serverError()
//                    .entity("<error>" + e.getMessage() + "</error>")
//                    .build();
//        }
//    }
}
