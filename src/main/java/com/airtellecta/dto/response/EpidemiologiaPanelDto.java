package com.airtellecta.dto.response;

import java.math.BigDecimal;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Indicadores epidemiológicos del tabaquismo para el panel ejecutivo")
public class EpidemiologiaPanelDto {

    @Schema(description = "Prevalencia de tabaquismo actual (ENCODAT 2025, %).", example = "17.6")
    public BigDecimal prevalenciaActualPct;

    @Schema(description = "Prevalencia de tabaquismo histórica de referencia (ENCODAT 2016, %).", example = "20.4")
    public BigDecimal prevalenciaHistoricaPct;

    @Schema(description = "Variación en puntos porcentuales entre la medición histórica y la actual.", example = "-2.8")
    public BigDecimal deltaPp;

    @Schema(description = "Número estimado de fumadores actuales.", example = "14800000")
    public long fumadoresEstimados;

    @Schema(description = "Defunciones anuales atribuibles al tabaquismo.", example = "65000")
    public long defuncionesAtribuiblesAnual;

    @Schema(description = "Población de 18 años o más (denominador para prevalencia).", example = "96500000")
    public long poblacion18Plus;

    @Schema(description = "Fuentes de datos de los indicadores epidemiológicos.")
    public List<FuenteDatoDto> fuentes;
}
