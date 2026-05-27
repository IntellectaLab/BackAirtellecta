package com.airtellecta.dto.request;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Campos actualizables de un usuario existente. Solo se modifican los campos enviados.")
public class ActualizarUsuarioRequest {

    @Schema(description = "Nuevo nombre completo del usuario.", example = "Juan Pérez García")
    public String nombreCompleto;

    @Schema(description = "Nuevo cargo o puesto del usuario.", example = "Subdirector de Análisis")
    public String cargo;

    @Schema(description = "Nueva institución del usuario.", example = "IMSS")
    public String institucion;

    @Schema(description = "Nuevo rol del usuario. Valores posibles: ADMIN, USER.", example = "ADMIN")
    public String rol;

    @Schema(description = "Clave de la nueva entidad federativa (1-32). Null para alcance nacional.", example = "14")
    public Byte entidadId;

    @Schema(description = "Estado de la cuenta. false desactiva el acceso del usuario.", example = "true")
    public Boolean activo;
}
