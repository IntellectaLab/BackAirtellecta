package com.airtellecta.repository;

import com.airtellecta.dto.response.CostoPatologiaDto;
import com.airtellecta.dto.response.RecaudacionPanelDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class PanelEjecutivoRepository {

    @Inject
    EntityManager em;

    public RecaudacionPanelDto obtenerIepsMasReciente() {
        @SuppressWarnings("unchecked")
        List<Tuple> rows = em.createNativeQuery("""
            SELECT anio, monto_mdp, fuente, url_informe, nivel_verificacion
            FROM recaudacion_ieps_anual
            ORDER BY anio DESC LIMIT 1
            """, Tuple.class)
            .getResultList();

        if (rows.isEmpty()) {
            throw new IllegalStateException(
                "Error de configuracion: recaudacion IEPS no disponible");
        }

        Tuple row = rows.getFirst();
        RecaudacionPanelDto dto = new RecaudacionPanelDto();
        dto.iepsMasRecienteMdp = (BigDecimal) row.get("monto_mdp");
        dto.anio = ((Number) row.get("anio")).shortValue();
        dto.fuente = (String) row.get("fuente");
        dto.fuenteUrl = (String) row.get("url_informe");
        dto.nivelVerificacion = (String) row.get("nivel_verificacion");
        return dto;
    }

    public List<CostoPatologiaDto> obtenerCostosPorPatologia() {
        @SuppressWarnings("unchecked")
        List<Tuple> rows = em.createNativeQuery("""
            SELECT c.codigo, c.trastorno, cr.costo_ajustado_2025,
                   cr.anio_base, cr.fuente, cr.fuente_doi
            FROM costos_referencia cr
            JOIN cat_cie10 c ON cr.cve_cie10 = c.codigo
            ORDER BY cr.id
            """, Tuple.class)
            .getResultList();

        return rows.stream().map(row -> {
            CostoPatologiaDto dto = new CostoPatologiaDto();
            dto.codigo = (String) row.get("codigo");
            dto.trastorno = (String) row.get("trastorno");
            dto.costoAjustado2025 = (BigDecimal) row.get("costo_ajustado_2025");
            dto.anioBase = ((Number) row.get("anio_base")).shortValue();
            dto.fuente = (String) row.get("fuente");
            dto.fuenteDoi = (String) row.get("fuente_doi");
            return dto;
        }).toList();
    }
}
