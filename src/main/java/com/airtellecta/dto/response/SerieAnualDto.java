package com.airtellecta.dto.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Registro anual de defunciones y urgencias atribuibles al tabaquismo")
public class SerieAnualDto {

    @Schema(description = "Año del registro.", example = "2022")
    public Short anio;

    @Schema(description = "Total de defunciones atribuibles al tabaquismo (todas las causas).", example = "68000")
    public Long defunciones;

    @Schema(description = "Total de urgencias atribuibles al tabaquismo (todas las causas).", example = "350000")
    public Long urgencias;

    @Schema(description = "Defunciones con diagnóstico principal F17 (trastornos por tabaco).", example = "65000")
    public Long defuncionesF17;

    @Schema(description = "Urgencias con diagnóstico principal F17.", example = "320000")
    public Long urgenciasF17;
}
