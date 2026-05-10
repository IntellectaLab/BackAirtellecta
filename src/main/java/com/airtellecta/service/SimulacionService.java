package com.airtellecta.service;

import com.airtellecta.dto.SimulacionRequestDto;
import com.airtellecta.dto.response.ElasticidadesAplicadasDto;
import com.airtellecta.dto.response.ParametrosBaseDto;
import com.airtellecta.dto.response.PoliticaAplicadaDto;
import com.airtellecta.dto.response.ProyeccionAnualDto;
import com.airtellecta.dto.response.ResumenFinalDto;
import com.airtellecta.dto.response.SimulacionResultadoDto;
import com.airtellecta.repository.SimulacionRepository;
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

    @Inject
    SimulacionRepository repository;

    @Inject
    ConstantesService constantesService;

    private record EfectoPrecio(BigDecimal prevalenciaFinal, ElasticidadesAplicadasDto dto) {}

    private record ResultadoProyeccion(
        List<ProyeccionAnualDto> serie,
        BigDecimal defuncionesEvitadasTotal,
        BigDecimal ahorroTotal) {}

    public SimulacionResultadoDto simular(SimulacionRequestDto request) {
        Map<String, BigDecimal> constantes = constantesService.cargarConstantes();

        List<String> clavesPoliticas = request.politicas != null
            ? request.politicas : List.of();
        int horizonteAnios = request.horizonteAnios != null
            ? request.horizonteAnios
            : constantes.get("SIM_HORIZONTE_ANIOS_DEFAULT").intValue();

        validar(request, horizonteAnios);

        BigDecimal prevalenciaBase = constantes.get("SIM_PREVALENCIA_BASE_PCT");
        BigDecimal poblacion = constantes.get("SIM_POBLACION_18_PLUS");
        BigDecimal fumadoresBase = poblacion.multiply(prevalenciaBase)
            .divide(CIEN, 0, RoundingMode.HALF_UP);
        BigDecimal defuncionesAtrib = constantesService.calcularDefuncionesAtribuibles(constantes);
        BigDecimal impuestoActualPct = constantesService.calcularImpuestoActualPct(constantes);

        List<PoliticaAplicadaDto> politicasAplicadas = cargarPoliticas(clavesPoliticas);
        BigDecimal prevalenciaPostPol = aplicarPoliticas(prevalenciaBase, politicasAplicadas);

        EfectoPrecio efectoPrecio = aplicarElasticidades(
            prevalenciaPostPol, impuestoActualPct, request.impuestoPctPrecio);

        BigDecimal prevalenciaFinal = efectoPrecio.prevalenciaFinal;
        BigDecimal costoPromedio = repository.obtenerCostoPromedio();

        ResultadoProyeccion proyeccion = proyectar(
            prevalenciaBase, prevalenciaFinal, poblacion,
            defuncionesAtrib, costoPromedio, horizonteAnios);

        return construirResultado(
            prevalenciaBase, prevalenciaFinal, poblacion, fumadoresBase,
            defuncionesAtrib, impuestoActualPct, horizonteAnios,
            proyeccion, politicasAplicadas, efectoPrecio.dto);
    }

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

    private BigDecimal aplicarPoliticas(BigDecimal prevalenciaBase,
            List<PoliticaAplicadaDto> politicas) {
        BigDecimal resultado = prevalenciaBase;
        for (PoliticaAplicadaDto pol : politicas) {
            BigDecimal factor = BigDecimal.ONE.add(
                pol.efectoPct.divide(CIEN, 6, RoundingMode.HALF_UP));
            resultado = resultado.multiply(factor).setScale(4, RoundingMode.HALF_UP);
        }
        return resultado;
    }

    private EfectoPrecio aplicarElasticidades(BigDecimal prevalenciaPostPol,
            BigDecimal impuestoActualPct, BigDecimal impuestoPctPrecio) {
        if (impuestoPctPrecio == null) {
            return new EfectoPrecio(prevalenciaPostPol, null);
        }

        List<BigDecimal> elasticidades = repository.obtenerElasticidades();

        BigDecimal incrementoPrecioPct = impuestoPctPrecio
            .subtract(impuestoActualPct)
            .divide(impuestoActualPct, 6, RoundingMode.HALF_UP)
            .multiply(CIEN).setScale(2, RoundingMode.HALF_UP);

        BigDecimal sumaElast = elasticidades.stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal promedioElast = sumaElast.divide(
            new BigDecimal(elasticidades.size()), 6, RoundingMode.HALF_UP);
        BigDecimal efectoPrecio = promedioElast.multiply(incrementoPrecioPct)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal prevalenciaFinal = prevalenciaPostPol.multiply(
            BigDecimal.ONE.add(efectoPrecio.divide(CIEN, 6, RoundingMode.HALF_UP)))
            .setScale(4, RoundingMode.HALF_UP);

        ElasticidadesAplicadasDto dto = new ElasticidadesAplicadasDto();
        dto.impuestoNuevoPctPrecio = impuestoPctPrecio;
        dto.incrementoPrecioPct = incrementoPrecioPct;
        dto.efectoPromedioPct = efectoPrecio;

        return new EfectoPrecio(prevalenciaFinal, dto);
    }

    private ResultadoProyeccion proyectar(BigDecimal prevalenciaBase,
            BigDecimal prevalenciaFinal, BigDecimal poblacion,
            BigDecimal defuncionesAtrib, BigDecimal costoPromedio, int horizonteAnios) {
        List<ProyeccionAnualDto> serie = new ArrayList<>();
        BigDecimal defEvitadasAcum = BigDecimal.ZERO;
        BigDecimal ahorroAcum = BigDecimal.ZERO;

        BigDecimal fumadoresT = poblacion.multiply(prevalenciaFinal)
            .divide(CIEN, 0, RoundingMode.HALF_UP);
        BigDecimal defEvitadasT = prevalenciaBase.subtract(prevalenciaFinal)
            .divide(prevalenciaBase, 6, RoundingMode.HALF_UP)
            .multiply(defuncionesAtrib)
            .setScale(0, RoundingMode.HALF_UP);
        BigDecimal ahorroT = defEvitadasT.multiply(costoPromedio)
            .divide(MILLON, 2, RoundingMode.HALF_UP);

        for (int t = 1; t <= horizonteAnios; t++) {
            ProyeccionAnualDto anual = new ProyeccionAnualDto();
            anual.anio = t;
            anual.prevalenciaPct = prevalenciaFinal.setScale(2, RoundingMode.HALF_UP);
            anual.fumadoresAbsolutos = fumadoresT.longValue();
            anual.defuncionesEvitadas = defEvitadasT.longValue();
            anual.ahorroMdp = ahorroT;
            serie.add(anual);

            defEvitadasAcum = defEvitadasAcum.add(defEvitadasT);
            ahorroAcum = ahorroAcum.add(ahorroT);
        }

        return new ResultadoProyeccion(serie, defEvitadasAcum, ahorroAcum);
    }

    private SimulacionResultadoDto construirResultado(
            BigDecimal prevalenciaBase, BigDecimal prevalenciaFinal,
            BigDecimal poblacion, BigDecimal fumadoresBase,
            BigDecimal defuncionesAtrib, BigDecimal impuestoActualPct,
            int horizonteAnios, ResultadoProyeccion proyeccion,
            List<PoliticaAplicadaDto> politicasAplicadas,
            ElasticidadesAplicadasDto elasticidadesDto) {

        ParametrosBaseDto parametrosBase = new ParametrosBaseDto();
        parametrosBase.prevalenciaBasePct = prevalenciaBase.setScale(2, RoundingMode.HALF_UP);
        parametrosBase.poblacion18Plus = poblacion.longValue();
        parametrosBase.fumadoresBase = fumadoresBase.longValue();
        parametrosBase.defuncionesAtribuiblesBase = defuncionesAtrib.longValue();
        parametrosBase.impuestoActualPctPrecio = impuestoActualPct;

        long fumadoresFinales = poblacion.multiply(prevalenciaFinal)
            .divide(CIEN, 0, RoundingMode.HALF_UP).longValue();

        ResumenFinalDto resumen = new ResumenFinalDto();
        resumen.prevalenciaFinalPct = prevalenciaFinal.setScale(2, RoundingMode.HALF_UP);
        resumen.reduccionPuntosPct = prevalenciaBase.subtract(prevalenciaFinal)
            .setScale(2, RoundingMode.HALF_UP);
        resumen.fumadoresEvitadosTotal = (fumadoresBase.longValue() - fumadoresFinales)
            * horizonteAnios;
        resumen.defuncionesEvitadasTotal = proyeccion.defuncionesEvitadasTotal.longValue();
        resumen.ahorroAcumuladoMdp = proyeccion.ahorroTotal;

        SimulacionResultadoDto resultado = new SimulacionResultadoDto();
        resultado.parametrosBase = parametrosBase;
        resultado.proyeccion = proyeccion.serie;
        resultado.resumenFinal = resumen;
        resultado.politicasAplicadas = politicasAplicadas;
        resultado.elasticidadesAplicadas = elasticidadesDto;

        return resultado;
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
