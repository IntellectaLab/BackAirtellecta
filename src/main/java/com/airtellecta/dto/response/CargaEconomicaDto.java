package com.airtellecta.dto.response;

import java.math.BigDecimal;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Carga económica del tabaquismo en México (costos directos e indirectos)")
public class CargaEconomicaDto {

    @Schema(description = "Costo directo anual de atención médica atribuible al tabaquismo (millones de pesos).", example = "75000.0")
    public BigDecimal costoDirectoAnualMdp;

    @Schema(description = "Costo social anual (pérdida de productividad, mortalidad prematura) en millones de pesos.", example = "43000.0")
    public BigDecimal costoSocialAnualMdp;

    @Schema(description = "Inversión requerida en programas de prevención y cesación (millones de pesos).", example = "2500.0")
    public BigDecimal inversionPrevencionMdp;

    @Schema(description = "Lista de fuentes de datos con nivel de verificación.")
    public List<FuenteDatoDto> fuentes;
}
