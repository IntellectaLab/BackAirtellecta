package com.airtellecta.repository;

import com.airtellecta.dto.response.RecaudacionAnualDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class RecaudacionRepository {

    @Inject
    EntityManager em;

    public List<RecaudacionAnualDto> obtenerSerie() {
        @SuppressWarnings("unchecked")
        List<Tuple> rows = em.createNativeQuery("""
            SELECT anio, monto_mdp, fuente
            FROM recaudacion_ieps_anual
            ORDER BY anio
            """, Tuple.class)
            .getResultList();

        return rows.stream().map(row -> {
            RecaudacionAnualDto dto = new RecaudacionAnualDto();
            dto.anio = ((Number) row.get("anio")).shortValue();
            dto.montoMdp = (BigDecimal) row.get("monto_mdp");
            dto.fuente = (String) row.get("fuente");
            return dto;
        }).toList();
    }
}
