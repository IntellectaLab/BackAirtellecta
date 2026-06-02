package com.airtellecta.dto.response;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Parámetros del año base usados como punto de partida en la simulación")
public class ParametrosBaseDto {

    @Schema(description = "Prevalencia de tabaquismo en el año base (%).", example = "17.6")
    public BigDecimal prevalenciaBasePct;

    @Schema(description = "Población de 18 años o más en México (año base).", example = "96500000")
    public long poblacion18Plus;

    @Schema(description = "Número de fumadores estimados en el año base.", example = "16984000")
    public long fumadoresBase;

    @Schema(description = "Defunciones atribuibles al tabaquismo en el año base.", example = "65000")
    public long defuncionesAtribuiblesBase;

    @Schema(description = "Porcentaje del impuesto actual sobre el precio de venta (IEPS vigente).", example = "0.55")
    public BigDecimal impuestoActualPctPrecio;
}
