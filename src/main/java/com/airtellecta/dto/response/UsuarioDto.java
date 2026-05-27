package com.airtellecta.dto.response;

import java.time.LocalDateTime;

public class UsuarioDto {
    public Integer id;
    public String email;
    public String nombreCompleto;
    public String cargo;
    public String institucion;
    public String rol;
    public Byte entidadId;
    public boolean activo;
    public String activationLink;
    public LocalDateTime ultimoAcceso;
    public LocalDateTime createdAt;
}
