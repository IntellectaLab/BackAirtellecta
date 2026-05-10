package com.airtellecta.repository;

import com.airtellecta.dto.response.TendenciasDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;

@ApplicationScoped
public class TendenciasRepository {

    @Inject
    EntityManager em;

    public TendenciasDto obtenerComparativo() {
        Tuple row = (Tuple) em.createNativeQuery("""
            SELECT
                e16.prevalencia AS prev16,
                e25.prevalencia AS prev25,
                ROUND(e25.prevalencia - e16.prevalencia, 2) AS delta_pp,
                e16.fumadores AS fum16,
                e25.fumadores AS fum25,
                e16.vapeo AS vap16,
                e25.vapeo AS vap25,
                e16.dual_use AS dual16,
                e25.dual_use AS dual25
            FROM (
                SELECT
                    ROUND(SUM(CASE WHEN fumador_actual = 1 THEN ponde_ss ELSE 0 END)
                        / SUM(ponde_ss) * 100, 2) AS prevalencia,
                    ROUND(SUM(CASE WHEN fumador_actual = 1 THEN ponde_ss ELSE 0 END)) AS fumadores,
                    SUM(CASE WHEN usa_vape = 1 THEN 1 ELSE 0 END) AS vapeo,
                    SUM(CASE WHEN uso_dual = 1 THEN 1 ELSE 0 END) AS dual_use
                FROM encuesta_encodat
            ) e16
            CROSS JOIN (
                SELECT
                    ROUND(SUM(CASE WHEN fumador_actual = 1 THEN ponde_f ELSE 0 END)
                        / SUM(ponde_f) * 100, 2) AS prevalencia,
                    ROUND(SUM(CASE WHEN fumador_actual = 1 THEN ponde_f ELSE 0 END)) AS fumadores,
                    SUM(CASE WHEN usa_vape = 1 THEN 1 ELSE 0 END) AS vapeo,
                    SUM(CASE WHEN uso_dual = 1 THEN 1 ELSE 0 END) AS dual_use
                FROM encuesta_encodat_2025
            ) e25
            """, Tuple.class)
            .getSingleResult();

        TendenciasDto dto = new TendenciasDto();
        dto.prevalencia2016 = (BigDecimal) row.get("prev16");
        dto.prevalencia2025 = (BigDecimal) row.get("prev25");
        dto.deltaPp = (BigDecimal) row.get("delta_pp");
        dto.fumadores2016 = ((Number) row.get("fum16")).longValue();
        dto.fumadores2025 = ((Number) row.get("fum25")).longValue();
        dto.vapeo2016 = ((Number) row.get("vap16")).intValue();
        dto.vapeo2025 = ((Number) row.get("vap25")).intValue();
        dto.dual2016 = ((Number) row.get("dual16")).intValue();
        dto.dual2025 = ((Number) row.get("dual25")).intValue();
        return dto;
    }
}
