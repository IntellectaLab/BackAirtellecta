package com.airtellecta.repository;

import com.airtellecta.dto.response.CostoCie10Dto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class CostosRepository {

    @Inject
    EntityManager em;

    public List<CostoCie10Dto> obtenerCostos() {
        @SuppressWarnings("unchecked")
        List<Tuple> rows = em.createNativeQuery("""
            SELECT
                cr.cve_cie10   AS codigo,
                c.trastorno,
                cr.costo_promedio_paciente_mxn AS costo_por_paciente,
                cr.costo_ajustado_2025,
                cr.anio_base,
                cr.factor_inflacion,
                cr.fuente,
                cr.fuente_doi
            FROM costos_referencia cr
            JOIN cat_cie10 c ON cr.cve_cie10 = c.codigo
            ORDER BY cr.id
            """, Tuple.class)
            .getResultList();

        return rows.stream().map(row -> {
            CostoCie10Dto dto = new CostoCie10Dto();
            dto.codigo = (String) row.get("codigo");
            dto.trastorno = (String) row.get("trastorno");
            dto.costoPorPaciente = (BigDecimal) row.get("costo_por_paciente");
            dto.costoAjustado2025 = (BigDecimal) row.get("costo_ajustado_2025");
            dto.anioBase = ((Number) row.get("anio_base")).shortValue();
            dto.factorInflacion = (BigDecimal) row.get("factor_inflacion");
            dto.fuente = (String) row.get("fuente");
            dto.fuenteDoi = (String) row.get("fuente_doi");
            return dto;
        }).toList();
    }
}
