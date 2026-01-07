package com.example.routeservice.soap;

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
import jakarta.ejb.EJB;
import jakarta.jws.WebService;
import jakarta.xml.ws.BindingType;
import jakarta.xml.ws.soap.SOAPBinding;

@WebService(
        name = "RouteSoapService",
        targetNamespace = "http://routeservice.example.com/soap",
        serviceName = "RouteSoapService",
        portName = "RouteSoapServicePort"
)
@BindingType(SOAPBinding.SOAP12HTTP_BINDING)
public class RouteSoapServiceImpl {

    @EJB(lookup = "java:global/route-ear-1.0-SNAPSHOT/com.example-route-ejb-1.0-SNAPSHOT/RouteServiceImpl!com.example.routeservice.service.RouteService")
    private RouteService routeService;

    @jakarta.jws.WebMethod
    @jakarta.jws.WebResult(name = "routeListResponse")
    public RouteListResponse getRoutes(
            @jakarta.jws.WebParam(name = "page") Integer page,
            @jakarta.jws.WebParam(name = "size") Integer size,
            @jakarta.jws.WebParam(name = "sort") String[] sort,
            @jakarta.jws.WebParam(name = "filterParams") Map<String, String> filterParams) {
        if (page == null) page = 0;
        if (size == null) size = 20;
        if (sort == null) sort = new String[0];
        if (filterParams == null) filterParams = Map.of();

        RouteFilter filter = new RouteFilter(page, size, sort, filterParams);
        return routeService.getRoutes(filter);
    }

    @jakarta.jws.WebMethod
    @jakarta.jws.WebResult(name = "route")
    public Route getRouteById(@jakarta.jws.WebParam(name = "id") Long id) {
        return routeService.getRouteById(id);
    }

    @jakarta.jws.WebMethod
    @jakarta.jws.WebResult(name = "route")
    public Route createRoute(@jakarta.jws.WebParam(name = "request") RouteCreateRequest request) {
        return routeService.createRoute(request);
    }

    @jakarta.jws.WebMethod
    @jakarta.jws.WebResult(name = "route")
    public Route updateRoute(
            @jakarta.jws.WebParam(name = "id") Long id,
            @jakarta.jws.WebParam(name = "request") RouteUpdateRequest request) {
        return routeService.updateRoute(id, request);
    }

    @jakarta.jws.WebMethod
    public void deleteRoute(@jakarta.jws.WebParam(name = "id") Long id) {
        routeService.deleteRoute(id);
    }

    @jakarta.jws.WebMethod
    @jakarta.jws.WebResult(name = "distanceSumResponse")
    public DistanceSumResponse getDistanceSum() {
        return routeService.getDistanceSum();
    }

    @jakarta.jws.WebMethod
    @jakarta.jws.WebResult(name = "distanceGroupResponse")
    public DistanceGroupResponse groupByDistance() {
        return routeService.groupByDistance();
    }

    @jakarta.jws.WebMethod
    @jakarta.jws.WebResult(name = "filteredRoutesResponse")
    public FilteredRoutesResponse getRoutesWithDistanceGreaterThan(@jakarta.jws.WebParam(name = "minDistance") Double minDistance) {
        return routeService.getRoutesWithDistanceGreaterThan(minDistance);
    }

    @jakarta.jws.WebMethod
    @jakarta.jws.WebResult(name = "route")
    public Route createRouteBetweenLocations(
            @jakarta.jws.WebParam(name = "idFrom") Long idFrom,
            @jakarta.jws.WebParam(name = "idTo") Long idTo,
            @jakarta.jws.WebParam(name = "distance") Double distance) {
        try {
            var fromLoc = routeService.getFromLocationById(idFrom);
            var toLoc = routeService.getToLocationById(idTo);

            if (fromLoc == null || toLoc == null) {
                throw new RuntimeException("One or both locations not found");
            }

            var coords = routeService.getMidCoords(fromLoc, toLoc);
            String name = String.format("Route from %s to %s", fromLoc.getName(), toLoc.getName());
            var request = new RouteCreateRequest(name, coords, fromLoc, toLoc, distance);
            return routeService.createRoute(request);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

