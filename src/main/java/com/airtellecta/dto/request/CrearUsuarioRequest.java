package com.airtellecta.dto.request;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Datos requeridos para crear un nuevo usuario")
public class CrearUsuarioRequest {

    @Schema(description = "Correo electrónico del usuario. Se usará como login en Firebase.", required = true, example = "juan.perez@salud.gob.mx")
    public String email;

    @Schema(description = "Nombre completo del usuario.", required = true, example = "Juan Pérez López")
    public String nombreCompleto;

    @Schema(description = "Cargo o puesto del usuario dentro de su institución.", example = "Director de Epidemiología")
    public String cargo;

    @Schema(description = "Institución u organización a la que pertenece el usuario.", example = "Secretaría de Salud")
    public String institucion;

    @Schema(description = "Rol asignado al usuario en el sistema. Valores posibles: ADMIN, USER.", required = true, example = "USER")
    public String rol;

    @Schema(description = "Clave de la entidad federativa (1-32). Null si el usuario tiene alcance nacional.", example = "9")
    public Byte entidadId;
}
