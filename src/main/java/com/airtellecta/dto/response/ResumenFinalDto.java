package com.airtellecta.dto.response;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Resumen acumulado al final del horizonte temporal de la simulación")
public class ResumenFinalDto {

    @Schema(description = "Prevalencia proyectada al final del horizonte (%).", example = "12.4")
    public BigDecimal prevalenciaFinalPct;

    @Schema(description = "Reducción total en puntos porcentuales desde el año base.", example = "5.2")
    public BigDecimal reduccionPuntosPct;

    @Schema(description = "Total de fumadores evitados al final del horizonte.", example = "4500000")
    public long fumadoresEvitadosTotal;

    @Schema(description = "Total de defunciones evitadas durante el horizonte completo.", example = "28000")
    public long defuncionesEvitadasTotal;

    @Schema(description = "Ahorro total acumulado en costos de salud (millones de pesos).", example = "98500.75")
    public BigDecimal ahorroAcumuladoMdp;
}
