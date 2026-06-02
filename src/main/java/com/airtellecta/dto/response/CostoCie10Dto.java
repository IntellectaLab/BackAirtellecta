package com.airtellecta.dto.response;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Costo de atención médica por diagnóstico CIE-10 atribuible al tabaquismo")
public class CostoCie10Dto {

    @Schema(description = "Código CIE-10 del diagnóstico.", example = "C34")
    public String codigo;

    @Schema(description = "Nombre del trastorno o patología.", example = "Cáncer de pulmón")
    public String trastorno;

    @Schema(description = "Costo por paciente en la fuente original (pesos mexicanos del año base).", example = "285000.00")
    public BigDecimal costoPorPaciente;

    @Schema(description = "Costo por paciente ajustado a pesos mexicanos de 2025.", example = "412300.50")
    public BigDecimal costoAjustado2025;

    @Schema(description = "Año base del costo original.", example = "2018")
    public Short anioBase;

    @Schema(description = "Factor de inflación aplicado para actualizar a 2025.", example = "1.447")
    public BigDecimal factorInflacion;

    @Schema(description = "Fuente bibliográfica del costo.", example = "IMSS – Costos de Atención Médica 2018")
    public String fuente;

    @Schema(description = "DOI o URL de la fuente bibliográfica.", example = "https://doi.org/10.1234/ejemplo")
    public String fuenteDoi;
}
