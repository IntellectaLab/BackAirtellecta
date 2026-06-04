package com.airtellecta.service;

import com.airtellecta.dto.response.AuditLogPageDto;
import com.airtellecta.repository.AuditRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuditLogService {

    @Inject
    AuditRepository auditRepository;

    public AuditLogPageDto obtenerPagina(
            int page, int size,
            String accion,
            Integer usuarioId,
            String fechaInicio,
            String fechaFin,
            String emailBusqueda) {

        int  safeSize  = Math.min(Math.max(size, 1), 200);
        int  safePage  = Math.max(page, 0);

        long total      = auditRepository.contar(accion, usuarioId, fechaInicio, fechaFin, emailBusqueda);
        int  totalPages = (int) Math.ceil((double) total / safeSize);

        AuditLogPageDto dto = new AuditLogPageDto();
        dto.items      = auditRepository.buscarPaginado(safePage, safeSize, accion, usuarioId, fechaInicio, fechaFin, emailBusqueda);
        dto.totalItems = total;
        dto.totalPages = totalPages;
        dto.page       = safePage;
        dto.size       = safeSize;
        return dto;
    }
}
