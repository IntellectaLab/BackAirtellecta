package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.request.ActualizarUsuarioRequest;
import com.airtellecta.dto.request.CrearUsuarioRequest;
import com.airtellecta.dto.response.UsuarioDto;
import com.airtellecta.service.UsuarioService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import io.vertx.ext.web.RoutingContext;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/admin/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
@Tag(name = "Usuarios", description = "Gestión de usuarios (solo ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioResource {

    @Inject
    UsuarioService service;

    @Inject
    SecurityIdentity identity;

    @Inject
    RoutingContext routingContext;

    @GET
    @Operation(summary = "Listar usuarios", description = "Retorna la lista completa de usuarios registrados en el sistema.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = UsuarioDto.class))),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ApiResponse<List<UsuarioDto>> listar() {
        return ApiResponse.ok(service.listarTodos());
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Retorna los datos de un usuario específico.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = UsuarioDto.class))),
        @APIResponse(responseCode = "400", description = "ID inválido o usuario no encontrado"),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ApiResponse<UsuarioDto> obtener(
        @Parameter(description = "ID del usuario", required = true)
        @PathParam("id") int id) {
        return ApiResponse.ok(service.obtenerPorId(id));
    }

    @POST
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en Firebase Auth y lo registra en la base de datos.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Usuario creado correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = UsuarioDto.class))),
        @APIResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ApiResponse<UsuarioDto> crear(
        @RequestBody(description = "Datos del nuevo usuario", required = true,
            content = @Content(schema = @Schema(implementation = CrearUsuarioRequest.class)))
        CrearUsuarioRequest request) {
        String adminUid = identity.getPrincipal().getName();
        String ip = routingContext.request().remoteAddress().host();
        return ApiResponse.ok(service.crear(request, adminUid, ip));
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Usuario actualizado correctamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = UsuarioDto.class))),
        @APIResponse(responseCode = "400", description = "Datos de entrada inválidos o usuario no encontrado"),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario autenticado no tiene rol ADMIN"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ApiResponse<UsuarioDto> actualizar(
        @Parameter(description = "ID del usuario a actualizar", required = true)
        @PathParam("id") int id,
        @RequestBody(description = "Campos a actualizar", required = true,
            content = @Content(schema = @Schema(implementation = ActualizarUsuarioRequest.class)))
        ActualizarUsuarioRequest request) {
        String adminUid = identity.getPrincipal().getName();
        String ip = routingContext.request().remoteAddress().host();
        return ApiResponse.ok(service.actualizar(id, request, adminUid, ip));
    }
}
