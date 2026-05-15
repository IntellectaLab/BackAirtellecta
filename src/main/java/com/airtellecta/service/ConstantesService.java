package com.airtellecta.service;

import com.airtellecta.repository.SimulacionRepository;
import com.airtellecta.repository.SimulacionRepository.ConstanteCompleta;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@ApplicationScoped
public class ConstantesService {

    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final BigDecimal VEINTE = new BigDecimal("20");

    @Inject
    SimulacionRepository repository;

    public Map<String, BigDecimal> cargarConstantes() {
        Map<String, BigDecimal> constantes = repository.obtenerConstantesActivas();
        if (constantes.isEmpty()) {
            throw new IllegalStateException(
                "Error de configuracion: constantes no disponibles");
        }
        return constantes;
    }

    public Map<String, ConstanteCompleta> cargarConstantesCompletas() {
        Map<String, ConstanteCompleta> constantes = repository.obtenerConstantesCompletasActivas();
        if (constantes.isEmpty()) {
            throw new IllegalStateException(
                "Error de configuracion: constantes no disponibles");
        }
        return constantes;
    }

    public BigDecimal calcularDefuncionesAtribuibles(Map<String, BigDecimal> constantes) {
        return constantes.get("SIM_DEFUNCIONES_TOTALES_ANUAL")
            .multiply(constantes.get("SIM_FRACCION_MORTALIDAD_TABACO_PCT"))
            .divide(CIEN, 0, RoundingMode.HALF_UP);
    }

    public BigDecimal calcularImpuestoActualPct(Map<String, BigDecimal> constantes) {
        BigDecimal precioCajetilla = constantes.get("SIM_PRECIO_CAJETILLA_BASE_MXN");
        BigDecimal iepsAdvalorem = constantes.get("SIM_IEPS_ADVALOREM_PCT");
        BigDecimal iepsEspPack = constantes.get("SIM_IEPS_ESPECIFICO_POR_CIGARRO")
            .multiply(VEINTE);

        BigDecimal precioFabricante = precioCajetilla.subtract(iepsEspPack)
            .divide(BigDecimal.ONE.add(iepsAdvalorem.divide(CIEN, 6, RoundingMode.HALF_UP)),
                4, RoundingMode.HALF_UP);
        BigDecimal iepsAdvMonto = precioFabricante.multiply(iepsAdvalorem)
            .divide(CIEN, 4, RoundingMode.HALF_UP);

        return iepsEspPack.add(iepsAdvMonto)
            .divide(precioCajetilla, 4, RoundingMode.HALF_UP)
            .multiply(CIEN).setScale(2, RoundingMode.HALF_UP);
    }

    public long calcularFumadoresEstimados(Map<String, BigDecimal> constantes) {
        return constantes.get("SIM_POBLACION_18_PLUS")
            .multiply(constantes.get("SIM_PREVALENCIA_BASE_PCT"))
            .divide(CIEN, 0, RoundingMode.HALF_UP)
            .longValue();
    }
}
