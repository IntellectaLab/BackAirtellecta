package com.airtellecta.repository;

import com.airtellecta.dto.response.PoliticaAplicadaDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SimulacionRepository {

    @Inject
    EntityManager em;

    public Map<String, BigDecimal> obtenerConstantesActivas() {
        @SuppressWarnings("unchecked")
        List<Tuple> rows = em.createNativeQuery("""
            SELECT clave, valor
            FROM constantes_simulador
            WHERE activo = 1
            """, Tuple.class)
            .getResultList();

        Map<String, BigDecimal> mapa = new HashMap<>();
        for (Tuple row : rows) {
            mapa.put((String) row.get("clave"), (BigDecimal) row.get("valor"));
        }
        return mapa;
    }

    public List<PoliticaAplicadaDto> obtenerPoliticas(List<String> claves) {
        @SuppressWarnings("unchecked")
        List<Tuple> rows = em.createNativeQuery("""
            SELECT cve_politica, nombre, efecto_prevalencia_pct
            FROM parametros_politica
            WHERE cve_politica IN (:claves)
            """, Tuple.class)
            .setParameter("claves", claves)
            .getResultList();

        return rows.stream().map(row -> {
            PoliticaAplicadaDto dto = new PoliticaAplicadaDto();
            dto.clave = (String) row.get("cve_politica");
            dto.nombre = (String) row.get("nombre");
            dto.efectoPct = (BigDecimal) row.get("efecto_prevalencia_pct");
            return dto;
        }).toList();
    }

    public List<BigDecimal> obtenerElasticidades() {
        @SuppressWarnings("unchecked")
        List<Tuple> rows = em.createNativeQuery("""
            SELECT elasticidad
            FROM elasticidades_precio
            """, Tuple.class)
            .getResultList();

        return rows.stream()
            .map(row -> (BigDecimal) row.get("elasticidad"))
            .toList();
    }

    public BigDecimal obtenerCostoPromedio() {
        Object result = em.createNativeQuery("""
            SELECT AVG(costo_ajustado_2025) FROM costos_referencia
            """)
            .getSingleResult();
        return result != null ? (BigDecimal) result : BigDecimal.ZERO;
    }
}
