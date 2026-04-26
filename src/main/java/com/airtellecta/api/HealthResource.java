package com.airtellecta.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api")
public class HealthResource {

    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public String ping() {
        return "{\"status\":\"ok\",\"app\":\"AirTellecta\",\"version\":\"1.0.0-SNAPSHOT\"}";
    }
}
