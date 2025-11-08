package com.example.navigator.resource;

import com.example.navigator.dto.Route;
import com.example.navigator.dto.RouteListResponse;
import com.example.navigator.service.NavigatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/navigator")
public class NavigatorController {

    @Autowired
    private NavigatorService navigatorService;

    @GetMapping(value = "/routes/{idFrom}/{idTo}/{orderBy}",
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> findRoutesBetween(@PathVariable Long idFrom, @PathVariable Long idTo,
                                                               @PathVariable String orderBy) {
        System.out.println("NavigatorController findRoutesBetween. Params: " + idFrom + " " + idTo + " " + orderBy);
        try {
            RouteListResponse response = navigatorService.findRoutesBetween(idFrom, idTo, orderBy);
            for (Route r : response.getRoutes()) {
                System.out.println("Id: " + r.getId());
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String errorXml = String.format("<error>%s</error>", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(errorXml);
        }
    }

    @PostMapping(value = "/route/add/{idFrom}/{idTo}/{distance}",
            produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> addRouteBetween(@PathVariable Long idFrom, @PathVariable Long idTo,
                                                  @PathVariable Double distance) {
        try {
            String responseBody = navigatorService.addRouteBetween(idFrom, idTo, distance);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            String errorXml = String.format("<error>%s</error>", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_XML)
                    .body(errorXml);
        }
    }
}