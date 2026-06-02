package com.airtellecta.dto.response;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Datos de prevalencia de tabaquismo para una entidad federativa")
public class EntidadPrevalenciaDto {

    @Schema(description = "Clave de la entidad federativa (1-32, conforme al INEGI).", example = "9")
    public Byte cveEntidad;

    @Schema(description = "Nombre completo de la entidad federativa.", example = "Ciudad de México")
    public String nombre;

    @Schema(description = "Abreviatura de la entidad.", example = "CDMX")
    public String abreviatura;

    @Schema(description = "Número estimado de fumadores en la entidad.", example = "1540000")
    public BigDecimal fumadoresEstimados;

    @Schema(description = "Población total de la entidad federativa.", example = "9209944")
    public Long pobTotal;

    @Schema(description = "Tasa de tabaquismo por cada 100,000 habitantes.", example = "16720.5")
    public BigDecimal tasa100k;

    @Schema(description = "Prevalencia de tabaquismo en la entidad (%).", example = "19.3")
    public BigDecimal prevalencia;
}
