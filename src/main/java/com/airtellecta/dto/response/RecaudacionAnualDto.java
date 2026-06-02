package com.airtellecta.dto.response;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Recaudación IEPS de tabaco para un año específico")
public class RecaudacionAnualDto {

    @Schema(description = "Año de la recaudación.", example = "2023")
    public Short anio;

    @Schema(description = "Monto recaudado en millones de pesos mexicanos (MDP).", example = "48312.5")
    public BigDecimal montoMdp;

    @Schema(description = "Fuente oficial del dato.", example = "SAT - Estadísticas de Recaudación")
    public String fuente;
}
