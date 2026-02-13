package com.example.routeservice.proxy;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Path("/application.wadl")
public class ApplicationWadlResource {
    @GET
    @Produces({"application/xml"})
    public Response getWadl() {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.wadl");
        if (in == null) {
            return Response.status(Status.NOT_FOUND).entity("application.wadl not found").build();
        } else {
            try {
                byte[] bytes = in.readAllBytes();
                String body = new String(bytes, StandardCharsets.UTF_8);
                return Response.ok(body).build();
            } catch (Exception var4) {
                return Response.serverError().entity("Failed to read application.wadl").build();
            }
        }
    }
}
