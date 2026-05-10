package com.airtellecta.exception;

import com.airtellecta.dto.ApiResponse;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class GlobalExceptionHandler {

    @ServerExceptionMapper(IllegalArgumentException.class)
    public Response handleBadRequest(IllegalArgumentException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.fail(e.getMessage()))
                .build();
    }

    @ServerExceptionMapper(Exception.class)
    public Response handleGeneral(Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.fail("Error interno del servidor"))
                .build();
    }
}
