package com.airtellecta.dto.response;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Costo de atención médica por patología (vista simplificada para el panel ejecutivo)")
public class CostoPatologiaDto {

    @Schema(description = "Código CIE-10 del diagnóstico.", example = "C34")
    public String codigo;

    @Schema(description = "Nombre de la patología.", example = "Cáncer de pulmón")
    public String trastorno;

    @Schema(description = "Costo por paciente ajustado a pesos de 2025 (MDP).", example = "412300.50")
    public BigDecimal costoAjustado2025;

    @Schema(description = "Año base del costo original.", example = "2018")
    public short anioBase;

    @Schema(description = "Fuente bibliográfica.", example = "IMSS – Costos de Atención Médica 2018")
    public String fuente;

    @Schema(description = "DOI o URL de la fuente.", example = "https://doi.org/10.1234/ejemplo")
    public String fuenteDoi;
}
