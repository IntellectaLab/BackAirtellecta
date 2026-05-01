package com.airtellecta.repository;

import com.airtellecta.dto.response.EntidadPrevalenciaDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MapaEstatalRepository {

    @Inject
    EntityManager em;

    public List<EntidadPrevalenciaDto> obtenerPrevalenciaPorEntidad(Optional<Byte> sexo) {
        String filtroSexo = sexo.isPresent() ? " AND e.cve_sexo = :sexo" : "";

        @SuppressWarnings("unchecked")
        var query = em.createNativeQuery("""
            SELECT
                ce.cve_entidad,
                ce.nombre,
                ce.abreviatura,
                ROUND(SUM(CASE WHEN e.fumador_actual = 1 THEN e.ponde_ss ELSE 0 END)) AS fumadores_est,
                p.pob_total,
                ROUND(SUM(CASE WHEN e.fumador_actual = 1 THEN e.ponde_ss ELSE 0 END)
                    / p.pob_total * 100000, 2) AS tasa,
                ROUND(SUM(CASE WHEN e.fumador_actual = 1 THEN e.ponde_ss ELSE 0 END)
                    / SUM(e.ponde_ss) * 100, 2) AS prev_pct
            FROM encuesta_encodat e
            JOIN cat_entidades ce ON e.cve_entidad = ce.cve_entidad
            JOIN (SELECT cve_entidad, SUM(pob_total) AS pob_total
                  FROM pob_proyecciones WHERE anio = 2023
                  GROUP BY cve_entidad) p ON ce.cve_entidad = p.cve_entidad
            WHERE ce.cve_entidad <= 32
            """ + filtroSexo + """
             GROUP BY ce.cve_entidad, ce.nombre, ce.abreviatura, p.pob_total
             ORDER BY prev_pct DESC
            """, Tuple.class);

        if (sexo.isPresent()) {
            query.setParameter("sexo", sexo.get());
        }

        List<Tuple> rows = query.getResultList();

        return rows.stream().map(row -> {
            EntidadPrevalenciaDto dto = new EntidadPrevalenciaDto();
            dto.cveEntidad = ((Number) row.get("cve_entidad")).byteValue();
            dto.nombre = (String) row.get("nombre");
            dto.abreviatura = (String) row.get("abreviatura");
            dto.fumadoresEstimados = (BigDecimal) row.get("fumadores_est");
            dto.pobTotal = ((Number) row.get("pob_total")).longValue();
            dto.tasa100k = (BigDecimal) row.get("tasa");
            dto.prevalencia = (BigDecimal) row.get("prev_pct");
            return dto;
        }).toList();
    }
}
