package com.airtellecta.repository;

import com.airtellecta.dto.response.DesgloseCie10Dto;
import com.airtellecta.dto.response.SerieAnualDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import java.util.List;

@ApplicationScoped
public class MortalidadRepository {

    @Inject
    EntityManager em;

    public List<SerieAnualDto> obtenerSeriesAnuales() {
        @SuppressWarnings("unchecked")
        List<Tuple> rows = em.createNativeQuery("""
            SELECT
                d.anio,
                SUM(d.f10 + d.f11 + d.f12 + d.f14 + d.f15 + d.f17 + d.f18 + d.f19) AS defunciones,
                u.urgencias,
                SUM(d.f17) AS def_f17,
                u.urg_f17
            FROM defunciones d
            JOIN (SELECT anio,
                    SUM(f10+f11+f12+f13+f14+f15+f16+f17+f18+f19) AS urgencias,
                    SUM(f17) AS urg_f17
                  FROM urgencias GROUP BY anio) u ON d.anio = u.anio
            GROUP BY d.anio, u.urgencias, u.urg_f17
            ORDER BY d.anio
            """, Tuple.class)
            .getResultList();

        return rows.stream().map(row -> {
            SerieAnualDto dto = new SerieAnualDto();
            dto.anio = ((Number) row.get("anio")).shortValue();
            dto.defunciones = ((Number) row.get("defunciones")).longValue();
            dto.urgencias = ((Number) row.get("urgencias")).longValue();
            dto.defuncionesF17 = ((Number) row.get("def_f17")).longValue();
            dto.urgenciasF17 = ((Number) row.get("urg_f17")).longValue();
            return dto;
        }).toList();
    }

    public List<DesgloseCie10Dto> obtenerDesgloseCie10() {
        @SuppressWarnings("unchecked")
        List<Tuple> rows = em.createNativeQuery("""
            SELECT
                c.codigo,
                c.trastorno,
                COALESCE(d.total, 0) AS total_def,
                COALESCE(u.total, 0) AS total_urg
            FROM cat_cie10 c
            LEFT JOIN (SELECT 'F10' AS codigo, SUM(f10) AS total FROM defunciones
                       UNION ALL SELECT 'F11', SUM(f11) FROM defunciones
                       UNION ALL SELECT 'F12', SUM(f12) FROM defunciones
                       UNION ALL SELECT 'F14', SUM(f14) FROM defunciones
                       UNION ALL SELECT 'F15', SUM(f15) FROM defunciones
                       UNION ALL SELECT 'F17', SUM(f17) FROM defunciones
                       UNION ALL SELECT 'F18', SUM(f18) FROM defunciones
                       UNION ALL SELECT 'F19', SUM(f19) FROM defunciones) d ON c.codigo = d.codigo
            LEFT JOIN (SELECT 'F10' AS codigo, SUM(f10) AS total FROM urgencias
                       UNION ALL SELECT 'F11', SUM(f11) FROM urgencias
                       UNION ALL SELECT 'F12', SUM(f12) FROM urgencias
                       UNION ALL SELECT 'F13', SUM(f13) FROM urgencias
                       UNION ALL SELECT 'F14', SUM(f14) FROM urgencias
                       UNION ALL SELECT 'F15', SUM(f15) FROM urgencias
                       UNION ALL SELECT 'F16', SUM(f16) FROM urgencias
                       UNION ALL SELECT 'F17', SUM(f17) FROM urgencias
                       UNION ALL SELECT 'F18', SUM(f18) FROM urgencias
                       UNION ALL SELECT 'F19', SUM(f19) FROM urgencias) u ON c.codigo = u.codigo
            ORDER BY c.cve
            """, Tuple.class)
            .getResultList();

        return rows.stream().map(row -> {
            DesgloseCie10Dto dto = new DesgloseCie10Dto();
            dto.codigo = (String) row.get("codigo");
            dto.trastorno = (String) row.get("trastorno");
            dto.totalDefunciones = ((Number) row.get("total_def")).longValue();
            dto.totalUrgencias = ((Number) row.get("total_urg")).longValue();
            return dto;
        }).toList();
    }
}
