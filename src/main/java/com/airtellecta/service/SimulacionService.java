package com.airtellecta.service;

import com.airtellecta.dto.SimulacionRequestDto;
import com.airtellecta.dto.response.ElasticidadesAplicadasDto;
import com.airtellecta.dto.response.ParametrosBaseDto;
import com.airtellecta.dto.response.PoliticaAplicadaDto;
import com.airtellecta.dto.response.ProyeccionAnualDto;
import com.airtellecta.dto.response.ResumenFinalDto;
import com.airtellecta.dto.response.SimulacionResultadoDto;
import com.airtellecta.repository.SimulacionRepository;
import com.airtellecta.repository.SimulacionRepository.ElasticidadGrupo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class SimulacionService {

    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final BigDecimal MILLON = new BigDecimal("1000000");
    private static final BigDecimal FLOOR_PREVALENCIA = new BigDecimal("0.5");

    @Inject
    SimulacionRepository repository;

    @Inject
    ConstantesService constantesService;

    public SimulacionResultadoDto simular(SimulacionRequestDto request) {
        Map<String, BigDecimal> constantes = constantesService.cargarConstantes();

        List<String> clavesPoliticas = request.politicas != null
            ? request.politicas : List.of();
        int horizonteAnios = request.horizonteAnios != null
            ? request.horizonteAnios
            : constantes.get("SIM_HORIZONTE_ANIOS_DEFAULT").intValue();

        validar(request, horizonteAnios);

        // ── Base parameters ──
        BigDecimal prevalenciaBase = constantes.get("SIM_PREVALENCIA_BASE_PCT");
        BigDecimal poblacionBase = constantes.get("SIM_POBLACION_18_PLUS");
        BigDecimal fumadoresBase = poblacionBase.multiply(prevalenciaBase)
            .divide(CIEN, 0, RoundingMode.HALF_UP);
        BigDecimal defuncionesAtrib = constantesService.calcularDefuncionesAtribuibles(constantes);
        BigDecimal impuestoActualPct = constantesService.calcularImpuestoActualPct(constantes);

        BigDecimal tendenciaPpAnual = constantes.getOrDefault(
            "SIM_TENDENCIA_NATURAL_PP_ANUAL", new BigDecimal("-0.34"));
        BigDecimal tau = constantes.getOrDefault(
            "SIM_TAU_CONVERGENCIA", new BigDecimal("2.5"));
        int anioBase = constantes.getOrDefault(
            "SIM_ANIO_BASE", new BigDecimal("2025")).intValue();

        // ── Mortality rate per smoker ──
        BigDecimal tasaMortalidadPorFumador = defuncionesAtrib
            .divide(fumadoresBase, 10, RoundingMode.HALF_UP);

        // ── Policy effects ──
        List<PoliticaAplicadaDto> politicasAplicadas = cargarPoliticas(clavesPoliticas);

        // First-year prevalence shift (multiplicative)
        BigDecimal shiftMultiplicativo = BigDecimal.ONE;
        for (PoliticaAplicadaDto pol : politicasAplicadas) {
            BigDecimal factor = BigDecimal.ONE.add(
                pol.efectoPct.divide(CIEN, 6, RoundingMode.HALF_UP));
            shiftMultiplicativo = shiftMultiplicativo.multiply(factor)
                .setScale(6, RoundingMode.HALF_UP);
        }

        // Ongoing cessation/initiation effect (sum of all policies, applied gradually)
        BigDecimal efectoOngoingTotal = BigDecimal.ZERO;
        for (PoliticaAplicadaDto pol : politicasAplicadas) {
            BigDecimal cesacion = pol.efectoCesacionPct != null ? pol.efectoCesacionPct : BigDecimal.ZERO;
            BigDecimal inicio = pol.efectoInicioPct != null ? pol.efectoInicioPct : BigDecimal.ZERO;
            efectoOngoingTotal = efectoOngoingTotal.add(cesacion.abs()).add(inicio.abs());
        }

        // ── Tax/price elasticity (age-weighted) ──
        ElasticidadesAplicadasDto elasticidadesDto = null;
        BigDecimal efectoPrecioMultiplicador = BigDecimal.ONE;
        if (request.impuestoPctPrecio != null) {
            BigDecimal incrementoPrecioPct = request.impuestoPctPrecio
                .subtract(impuestoActualPct)
                .divide(impuestoActualPct, 6, RoundingMode.HALF_UP)
                .multiply(CIEN).setScale(2, RoundingMode.HALF_UP);

            BigDecimal efectoPrecioPct = calcularEfectoPrecioPonderado(incrementoPrecioPct);

            efectoPrecioMultiplicador = BigDecimal.ONE.add(
                efectoPrecioPct.divide(CIEN, 6, RoundingMode.HALF_UP));

            elasticidadesDto = new ElasticidadesAplicadasDto();
            elasticidadesDto.impuestoNuevoPctPrecio = request.impuestoPctPrecio;
            elasticidadesDto.incrementoPrecioPct = incrementoPrecioPct;
            elasticidadesDto.efectoPromedioPct = efectoPrecioPct;
        }

        // ── Year-by-year projection ──
        List<ProyeccionAnualDto> serie = new ArrayList<>();
        BigDecimal defEvitadasAcum = BigDecimal.ZERO;
        BigDecimal ahorroAcum = BigDecimal.ZERO;
        BigDecimal costoPromedio = repository.obtenerCostoPromedio();

        BigDecimal prevBaseline = prevalenciaBase;  // tracks baseline (no intervention)
        BigDecimal prevIntervencion = prevalenciaBase;  // tracks with-intervention

        for (int t = 1; t <= horizonteAnios; t++) {
            int anioActual = anioBase + t;

            // 1. Natural trend (both lines decline)
            prevBaseline = prevBaseline.add(tendenciaPpAnual)
                .max(FLOOR_PREVALENCIA);

            // 2. Intervention prevalence starts from baseline trend
            prevIntervencion = prevIntervencion.add(tendenciaPpAnual);

            // 3. Year 1: apply first-year policy shift + tax effect
            if (t == 1) {
                prevIntervencion = prevIntervencion.multiply(shiftMultiplicativo)
                    .setScale(4, RoundingMode.HALF_UP);
                prevIntervencion = prevIntervencion.multiply(efectoPrecioMultiplicador)
                    .setScale(4, RoundingMode.HALF_UP);
            }

            // 4. Years 2+: ongoing cessation/initiation effect with exponential adoption
            if (t >= 2 && efectoOngoingTotal.compareTo(BigDecimal.ZERO) > 0) {
                // adopcion(t) = 1 - e^(-(t-1)/tau) — starts at t=2, ramps up
                double adopcion = 1.0 - Math.exp(-(t - 1) / tau.doubleValue());
                BigDecimal efectoAnual = efectoOngoingTotal
                    .multiply(new BigDecimal(adopcion))
                    .divide(CIEN, 6, RoundingMode.HALF_UP);
                // Apply as additional prevalence reduction (ongoing effect is incremental each year)
                BigDecimal incrementoVsPrevio = efectoAnual.subtract(
                    t >= 3
                        ? efectoOngoingTotal.multiply(new BigDecimal(1.0 - Math.exp(-(t - 2) / tau.doubleValue())))
                            .divide(CIEN, 6, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO);
                prevIntervencion = prevIntervencion.subtract(incrementoVsPrevio)
                    .max(FLOOR_PREVALENCIA);
            }

            prevIntervencion = prevIntervencion.max(FLOOR_PREVALENCIA)
                .setScale(4, RoundingMode.HALF_UP);

            // 5. Dynamic population
            BigDecimal poblacionT = obtenerPoblacionParaAnio(anioActual, poblacionBase);

            // 6. Compute smokers
            BigDecimal fumadoresBaseline = poblacionT.multiply(prevBaseline)
                .divide(CIEN, 0, RoundingMode.HALF_UP);
            BigDecimal fumadoresIntervencion = poblacionT.multiply(prevIntervencion)
                .divide(CIEN, 0, RoundingMode.HALF_UP);

            // 7. Deaths avoided (difference baseline vs intervention)
            BigDecimal muertesBaseline = fumadoresBaseline.multiply(tasaMortalidadPorFumador)
                .setScale(0, RoundingMode.HALF_UP);
            BigDecimal muertesIntervencion = fumadoresIntervencion.multiply(tasaMortalidadPorFumador)
                .setScale(0, RoundingMode.HALF_UP);
            BigDecimal defEvitadasAnuales = muertesBaseline.subtract(muertesIntervencion)
                .max(BigDecimal.ZERO);
            defEvitadasAcum = defEvitadasAcum.add(defEvitadasAnuales);

            BigDecimal ahorroAnual = defEvitadasAnuales.multiply(costoPromedio)
                .divide(MILLON, 2, RoundingMode.HALF_UP);
            ahorroAcum = ahorroAcum.add(ahorroAnual);

            // 8. Build row
            ProyeccionAnualDto anual = new ProyeccionAnualDto();
            anual.anio = anioActual;
            anual.prevalenciaPct = prevIntervencion.setScale(2, RoundingMode.HALF_UP);
            anual.fumadoresAbsolutos = fumadoresIntervencion.longValue();
            anual.defuncionesEvitadas = defEvitadasAnuales.longValue();
            anual.ahorroMdp = ahorroAnual;
            // v2 fields
            anual.prevalenciaBaselinePct = prevBaseline.setScale(2, RoundingMode.HALF_UP);
            anual.defuncionesEvitadasAcumuladas = defEvitadasAcum.longValue();
            serie.add(anual);
        }

        // ── Build result ──
        ProyeccionAnualDto ultimoAnio = serie.get(serie.size() - 1);

        ParametrosBaseDto parametrosBase = new ParametrosBaseDto();
        parametrosBase.prevalenciaBasePct = prevalenciaBase.setScale(2, RoundingMode.HALF_UP);
        parametrosBase.poblacion18Plus = poblacionBase.longValue();
        parametrosBase.fumadoresBase = fumadoresBase.longValue();
        parametrosBase.defuncionesAtribuiblesBase = defuncionesAtrib.longValue();
        parametrosBase.impuestoActualPctPrecio = impuestoActualPct;

        ResumenFinalDto resumen = new ResumenFinalDto();
        resumen.prevalenciaFinalPct = ultimoAnio.prevalenciaPct;
        resumen.reduccionPuntosPct = prevalenciaBase.subtract(ultimoAnio.prevalenciaPct)
            .setScale(2, RoundingMode.HALF_UP);
        resumen.fumadoresEvitadosTotal = fumadoresBase.longValue() - ultimoAnio.fumadoresAbsolutos;
        resumen.defuncionesEvitadasTotal = defEvitadasAcum.longValue();
        resumen.ahorroAcumuladoMdp = ahorroAcum;

        SimulacionResultadoDto resultado = new SimulacionResultadoDto();
        resultado.parametrosBase = parametrosBase;
        resultado.proyeccion = serie;
        resultado.resumenFinal = resumen;
        resultado.politicasAplicadas = politicasAplicadas;
        resultado.elasticidadesAplicadas = elasticidadesDto;
        resultado.metodoVersion = "v2-enhanced";

        return resultado;
    }

    // ── Age-weighted price elasticity ──

    private static final Map<String, BigDecimal> PESOS_GRUPO_EDAD = Map.of(
        "15-17", new BigDecimal("0.05"),
        "18-24", new BigDecimal("0.20"),
        "25-34", new BigDecimal("0.25"),
        "35-44", new BigDecimal("0.25"),
        "45+",   new BigDecimal("0.25")
    );

    private BigDecimal calcularEfectoPrecioPonderado(BigDecimal incrementoPrecioPct) {
        List<ElasticidadGrupo> elasticidades = repository.obtenerElasticidadesConGrupo();

        BigDecimal sumaPonderada = BigDecimal.ZERO;
        BigDecimal sumaPesos = BigDecimal.ZERO;

        for (ElasticidadGrupo eg : elasticidades) {
            BigDecimal peso = PESOS_GRUPO_EDAD.getOrDefault(eg.grupoEdad(), new BigDecimal("0.20"));
            sumaPonderada = sumaPonderada.add(eg.elasticidad().multiply(peso));
            sumaPesos = sumaPesos.add(peso);
        }

        BigDecimal elasticidadPonderada = sumaPesos.compareTo(BigDecimal.ZERO) > 0
            ? sumaPonderada.divide(sumaPesos, 6, RoundingMode.HALF_UP)
            : new BigDecimal("-0.24");

        return elasticidadPonderada.multiply(incrementoPrecioPct)
            .setScale(2, RoundingMode.HALF_UP);
    }

    // ── Dynamic population lookup ──

    private BigDecimal obtenerPoblacionParaAnio(int anio, BigDecimal fallback) {
        BigDecimal poblacion = repository.obtenerPoblacionNacional18Plus(anio);
        return poblacion != null ? poblacion : fallback;
    }

    // ── Load and validate policies ──

    private List<PoliticaAplicadaDto> cargarPoliticas(List<String> claves) {
        if (claves.isEmpty()) {
            return List.of();
        }
        List<PoliticaAplicadaDto> encontradas = repository.obtenerPoliticas(claves);
        if (encontradas.size() != claves.size()) {
            List<String> clavesEncontradas = encontradas.stream()
                .map(p -> p.clave).toList();
            String faltante = claves.stream()
                .filter(c -> !clavesEncontradas.contains(c))
                .findFirst().orElse("desconocida");
            throw new IllegalArgumentException(
                "Politica no encontrada: " + faltante);
        }
        return encontradas;
    }

    private void validar(SimulacionRequestDto request, int horizonteAnios) {
        if (request.impuestoPctPrecio != null) {
            if (request.impuestoPctPrecio.compareTo(BigDecimal.ZERO) < 0
                    || request.impuestoPctPrecio.compareTo(CIEN) > 0) {
                throw new IllegalArgumentException(
                    "Porcentaje de impuesto debe estar entre 0 y 100");
            }
        }
        if (horizonteAnios < 1 || horizonteAnios > 40) {
            throw new IllegalArgumentException(
                "Horizonte debe estar entre 1 y 40 anios");
        }
    }
}
