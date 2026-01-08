package com.example.routeservice.proxy;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/application.wadl")
public class ApplicationWadlResource {

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getWadl() {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.wadl");
        if (in == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("application.wadl not found").build();
        }
        try {
            byte[] bytes = in.readAllBytes();
            String body = new String(bytes, StandardCharsets.UTF_8);
            return Response.ok(body).build();
        } catch (Exception e) {
            return Response.serverError().entity("Failed to read application.wadl").build();
        }
    }
}

