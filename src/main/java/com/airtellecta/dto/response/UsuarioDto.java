package com.airtellecta.dto.response;

import java.time.LocalDateTime;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Datos del usuario registrado en el sistema")
public class UsuarioDto {

    @Schema(description = "Identificador único del usuario en la base de datos.", example = "42")
    public Integer id;

    @Schema(description = "Correo electrónico del usuario (también su login en Firebase).", example = "juan.perez@salud.gob.mx")
    public String email;

    @Schema(description = "Nombre completo del usuario.", example = "Juan Pérez López")
    public String nombreCompleto;

    @Schema(description = "Cargo o puesto del usuario.", example = "Director de Epidemiología")
    public String cargo;

    @Schema(description = "Institución u organización del usuario.", example = "Secretaría de Salud")
    public String institucion;

    @Schema(description = "Rol del usuario en el sistema: ADMIN o USER.", example = "USER")
    public String rol;

    @Schema(description = "Clave de entidad federativa asignada (1-32). Null si tiene alcance nacional.", example = "9")
    public Byte entidadId;

    @Schema(description = "Indica si la cuenta está activa.", example = "true")
    public boolean activo;

    @Schema(description = "Enlace de activación de cuenta generado por Firebase (solo en creación).")
    public String activationLink;

    @Schema(description = "Fecha y hora del último acceso al sistema.")
    public LocalDateTime ultimoAcceso;

    @Schema(description = "Fecha y hora de creación del registro.")
    public LocalDateTime createdAt;
}
