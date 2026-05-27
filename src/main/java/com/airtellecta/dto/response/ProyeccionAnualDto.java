package com.airtellecta.dto.response;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Proyección de indicadores para un año específico dentro del horizonte de simulación")
public class ProyeccionAnualDto {

    @Schema(description = "Año de la proyección.", example = "2027")
    public int anio;

    @Schema(description = "Prevalencia proyectada de fumadores (%).", example = "15.9")
    public BigDecimal prevalenciaPct;

    @Schema(description = "Número absoluto proyectado de fumadores.", example = "15350000")
    public long fumadoresAbsolutos;

    @Schema(description = "Defunciones evitadas acumuladas respecto al escenario sin política.", example = "3200")
    public long defuncionesEvitadas;

    @Schema(description = "Ahorro acumulado en costos de atención médica (millones de pesos).", example = "12400.5")
    public BigDecimal ahorroMdp;
}
