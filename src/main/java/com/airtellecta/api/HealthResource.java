package com.airtellecta.api;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api")
public class HealthResource {

    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public String ping() {
        return "{\"status\":\"ok\",\"app\":\"AirTellecta\",\"version\":\"1.0.0-SNAPSHOT\"}";
    }
}
