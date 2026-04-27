package com.airtellecta.repository;

import com.airtellecta.dto.response.ResumenNacionalDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;

@ApplicationScoped
public class ResumenNacionalRepository {

    @Inject
    EntityManager em;

    public ResumenNacionalDto obtenerResumen() {
        Tuple row = (Tuple) em.createNativeQuery("""
            SELECT
                ROUND(SUM(CASE WHEN e.fumador_actual = 1 THEN e.ponde_ss ELSE 0 END)
                    / SUM(e.ponde_ss) * 100, 2) AS prevalencia,
                ROUND(SUM(CASE WHEN e.fumador_actual = 1 THEN e.ponde_ss ELSE 0 END)) AS pob_fumadores,
                SUM(CASE WHEN e.usa_vape = 1 THEN 1 ELSE 0 END) AS vapeo,
                SUM(CASE WHEN e.uso_dual = 1 THEN 1 ELSE 0 END) AS dual_use,
                (SELECT SUM(f17) FROM defunciones) AS def_f17,
                (SELECT SUM(f17) FROM urgencias) AS urg_f17,
                (SELECT SUM(pob_total) FROM pob_proyecciones WHERE anio = 2023) AS pob_total
            FROM encuesta_encodat e
            """, Tuple.class)
            .getSingleResult();

        ResumenNacionalDto dto = new ResumenNacionalDto();
        dto.prevalenciaFumadores = (BigDecimal) row.get("prevalencia");
        dto.poblacionFumadores = ((Number) row.get("pob_fumadores")).longValue();
        dto.usuariosVapeo = ((Number) row.get("vapeo")).intValue();
        dto.usoDual = ((Number) row.get("dual_use")).intValue();
        dto.defuncionesF17 = ((Number) row.get("def_f17")).longValue();
        dto.urgenciasF17 = ((Number) row.get("urg_f17")).longValue();
        dto.poblacionTotal = ((Number) row.get("pob_total")).longValue();
        return dto;
    }
}
