package com.airtellecta.exception;

import com.airtellecta.dto.ApiResponse;
import io.quarkus.security.ForbiddenException;
import io.quarkus.security.UnauthorizedException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class GlobalExceptionHandler {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @ServerExceptionMapper(IllegalArgumentException.class)
    public Response handleBadRequest(IllegalArgumentException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(ApiResponse.fail(e.getMessage()))
                .build();
    }

    @ServerExceptionMapper(UnauthorizedException.class)
    public Response handleUnauthorized(UnauthorizedException e) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(ApiResponse.fail("Authentication required"))
                .build();
    }

    @ServerExceptionMapper(ForbiddenException.class)
    public Response handleForbidden(ForbiddenException e) {
        return Response.status(Response.Status.FORBIDDEN)
                .entity(ApiResponse.fail("Insufficient permissions"))
                .build();
    }

    @ServerExceptionMapper(WebApplicationException.class)
    public Response handleWebException(WebApplicationException e) {
        return Response.status(e.getResponse().getStatus())
                .entity(ApiResponse.fail(e.getMessage()))
                .build();
    }

    @ServerExceptionMapper(Exception.class)
    public Response handleGeneral(Exception e) {
        LOG.error("Unhandled exception", e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ApiResponse.fail("Error interno del servidor"))
                .build();
    }
}
