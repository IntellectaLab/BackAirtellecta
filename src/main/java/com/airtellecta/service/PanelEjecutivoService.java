package com.airtellecta.service;

import com.airtellecta.dto.response.CargaEconomicaDto;
import com.airtellecta.dto.response.CostoPatologiaDto;
import com.airtellecta.dto.response.EpidemiologiaPanelDto;
import com.airtellecta.dto.response.FuenteDatoDto;
import com.airtellecta.dto.response.PanelEjecutivoDto;
import com.airtellecta.dto.response.RecaudacionPanelDto;
import com.airtellecta.repository.PanelEjecutivoRepository;
import com.airtellecta.repository.SimulacionRepository.ConstanteCompleta;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PanelEjecutivoService {

    private static final BigDecimal CIEN = new BigDecimal("100");

    @Inject
    ConstantesService constantesService;

    @Inject
    PanelEjecutivoRepository repository;

    public PanelEjecutivoDto obtenerPanel() {
        Map<String, ConstanteCompleta> completas = constantesService.cargarConstantesCompletas();
        Map<String, BigDecimal> valores = constantesService.cargarConstantes();

        PanelEjecutivoDto panel = new PanelEjecutivoDto();
        panel.cargaEconomica = construirCargaEconomica(completas);
        panel.recaudacion = repository.obtenerIepsMasReciente();
        panel.epidemiologia = construirEpidemiologia(completas, valores);
        panel.costosPorPatologia = repository.obtenerCostosPorPatologia();
        return panel;
    }

    private CargaEconomicaDto construirCargaEconomica(Map<String, ConstanteCompleta> completas) {
        CargaEconomicaDto dto = new CargaEconomicaDto();
        dto.costoDirectoAnualMdp = completas.get("PANEL_COSTO_DIRECTO_ANUAL_MDP").valor();
        dto.costoSocialAnualMdp = completas.get("PANEL_COSTO_SOCIAL_ANUAL_MDP").valor();
        dto.inversionPrevencionMdp = completas.get("PANEL_INVERSION_PREVENCION_MDP").valor();

        dto.fuentes = List.of(
            crearFuente("costoDirectoAnualMdp", completas.get("PANEL_COSTO_DIRECTO_ANUAL_MDP"), "OFICIAL"),
            crearFuente("costoSocialAnualMdp", completas.get("PANEL_COSTO_SOCIAL_ANUAL_MDP"), "OFICIAL"),
            crearFuente("inversionPrevencionMdp", completas.get("PANEL_INVERSION_PREVENCION_MDP"), "ESTIMACION_PRENSA")
        );

        return dto;
    }

    private EpidemiologiaPanelDto construirEpidemiologia(
            Map<String, ConstanteCompleta> completas,
            Map<String, BigDecimal> valores) {

        EpidemiologiaPanelDto dto = new EpidemiologiaPanelDto();
        BigDecimal prevalenciaActual = valores.get("SIM_PREVALENCIA_BASE_PCT");
        BigDecimal prevalenciaHistorica = valores.get("SIM_PREVALENCIA_HISTORICA_PCT");

        dto.prevalenciaActualPct = prevalenciaActual.setScale(2, RoundingMode.HALF_UP);
        dto.prevalenciaHistoricaPct = prevalenciaHistorica.setScale(2, RoundingMode.HALF_UP);
        dto.deltaPp = prevalenciaActual.subtract(prevalenciaHistorica)
            .setScale(2, RoundingMode.HALF_UP);
        dto.fumadoresEstimados = constantesService.calcularFumadoresEstimados(valores);
        dto.defuncionesAtribuiblesAnual = constantesService
            .calcularDefuncionesAtribuibles(valores).longValue();
        dto.poblacion18Plus = valores.get("SIM_POBLACION_18_PLUS").longValue();

        dto.fuentes = List.of(
            crearFuente("prevalenciaActualPct", completas.get("SIM_PREVALENCIA_BASE_PCT"), "OFICIAL"),
            crearFuente("prevalenciaHistoricaPct", completas.get("SIM_PREVALENCIA_HISTORICA_PCT"), "OFICIAL"),
            crearFuente("defuncionesAtribuiblesAnual", completas.get("SIM_FRACCION_MORTALIDAD_TABACO_PCT"), "OFICIAL"),
            crearFuente("poblacion18Plus", completas.get("SIM_POBLACION_18_PLUS"), "OFICIAL")
        );

        return dto;
    }

    private FuenteDatoDto crearFuente(String campo, ConstanteCompleta constante,
            String nivelVerificacion) {
        FuenteDatoDto fuente = new FuenteDatoDto();
        fuente.campo = campo;
        fuente.fuente = constante.fuente();
        fuente.fuenteUrl = constante.fuenteUrl();
        fuente.anioReferencia = constante.anioReferencia();
        fuente.nivelVerificacion = nivelVerificacion;
        return fuente;
    }
}
