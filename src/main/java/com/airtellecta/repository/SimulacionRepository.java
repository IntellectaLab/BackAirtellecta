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
            SELECT cve_politica, nombre, efecto_prevalencia_pct,
                   efecto_inicio_pct, efecto_cesacion_pct
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
            dto.efectoInicioPct = row.get("efecto_inicio_pct") != null
                ? (BigDecimal) row.get("efecto_inicio_pct") : BigDecimal.ZERO;
            dto.efectoCesacionPct = row.get("efecto_cesacion_pct") != null
                ? (BigDecimal) row.get("efecto_cesacion_pct") : BigDecimal.ZERO;
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

    public record ElasticidadGrupo(String grupoEdad, BigDecimal elasticidad) {}

    public List<ElasticidadGrupo> obtenerElasticidadesConGrupo() {
        @SuppressWarnings("unchecked")
        List<Tuple> rows = em.createNativeQuery("""
            SELECT grupo_edad, elasticidad
            FROM elasticidades_precio
            """, Tuple.class)
            .getResultList();

        return rows.stream()
            .map(row -> new ElasticidadGrupo(
                (String) row.get("grupo_edad"),
                (BigDecimal) row.get("elasticidad")))
            .toList();
    }

    /**
     * Sum national 18+ population for a given year from pob_proyecciones.
     * Returns null if no data for that year.
     */
    public BigDecimal obtenerPoblacionNacional18Plus(int anio) {
        Object result = em.createNativeQuery("""
            SELECT SUM(
                pob_15_19 + pob_20_24 + pob_25_29 + pob_30_34 +
                pob_35_39 + pob_40_44 + pob_45_49 + pob_50_54 +
                pob_55_59 + pob_60_64 + pob_65_69 + pob_70_74 +
                pob_75_79 + pob_80_84 + pob_85_mas
            ) AS pob_total
            FROM pob_proyecciones
            WHERE anio = :anio
            """)
            .setParameter("anio", anio)
            .getSingleResult();
        return result != null ? new BigDecimal(result.toString()) : null;
    }

    public record ConstanteCompleta(BigDecimal valor, String fuente, String fuenteUrl, short anioReferencia) {}

    public Map<String, ConstanteCompleta> obtenerConstantesCompletasActivas() {
        @SuppressWarnings("unchecked")
        List<Tuple> rows = em.createNativeQuery("""
            SELECT clave, valor, fuente, fuente_url, anio_referencia
            FROM constantes_simulador
            WHERE activo = 1
            """, Tuple.class)
            .getResultList();

        Map<String, ConstanteCompleta> mapa = new HashMap<>();
        for (Tuple row : rows) {
            mapa.put(
                (String) row.get("clave"),
                new ConstanteCompleta(
                    (BigDecimal) row.get("valor"),
                    (String) row.get("fuente"),
                    (String) row.get("fuente_url"),
                    ((Number) row.get("anio_referencia")).shortValue()
                )
            );
        }
        return mapa;
    }
}
