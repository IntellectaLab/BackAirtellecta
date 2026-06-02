package com.airtellecta.api;

import com.airtellecta.dto.ApiResponse;
import com.airtellecta.dto.response.UsuarioDto;
import com.airtellecta.repository.UsuarioRepository;
import com.airtellecta.service.UsuarioService;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/me")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({"ADMIN", "USER"})
@Tag(name = "Perfil", description = "Datos del usuario autenticado actualmente")
@SecurityRequirement(name = "bearerAuth")
public class MeResource {

    @Inject
    UsuarioService usuarioService;

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    SecurityIdentity identity;

    @GET
    @Operation(summary = "Obtener perfil del usuario autenticado", description = "Retorna los datos del usuario que realiza la petición según su token JWT. También actualiza la fecha de último acceso.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Perfil del usuario autenticado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = UsuarioDto.class))),
        @APIResponse(responseCode = "401", description = "Token JWT ausente o inválido"),
        @APIResponse(responseCode = "403", description = "El usuario no tiene rol ADMIN o USER"),
        @APIResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ApiResponse<UsuarioDto> me() {
        String uid = identity.getPrincipal().getName();

        // Update ultimo_acceso
        usuarioService.registrarAcceso(uid);

        // Return current user profile
        var usuario = usuarioRepository.buscarPorFirebaseUid(uid);
        if (usuario == null) {
            return ApiResponse.ok(null);
        }
        return ApiResponse.ok(usuarioRepository.buscarPorId(usuario.id));
    }
}
