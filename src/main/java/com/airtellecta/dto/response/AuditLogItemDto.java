package com.airtellecta.dto.response;

public class AuditLogItemDto {
    public Long    id;
    public Integer usuarioId;
    public String  usuarioEmail;
    public String  accion;
    public String  entidadAfectada;
    public String  registroId;
    public String  detalle;        // JSON string
    public String  ipAddress;
    public String  createdAt;      // ISO-8601 string from DATE_FORMAT
}
