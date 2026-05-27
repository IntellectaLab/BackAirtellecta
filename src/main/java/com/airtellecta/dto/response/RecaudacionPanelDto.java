package com.airtellecta.dto.response;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Dato de recaudación IEPS de tabaco más reciente disponible")
public class RecaudacionPanelDto {

    @Schema(description = "Monto recaudado por IEPS de tabaco en el año más reciente (millones de pesos).", example = "48312.5")
    public BigDecimal iepsMasRecienteMdp;

    @Schema(description = "Año al que corresponde la recaudación reportada.", example = "2023")
    public short anio;

    @Schema(description = "Fuente oficial del dato.", example = "SAT – Estadísticas de Recaudación")
    public String fuente;

    @Schema(description = "URL de la fuente.")
    public String fuenteUrl;

    @Schema(description = "Nivel de verificación del dato: OFICIAL, ESTIMADO, PROYECTADO.", example = "OFICIAL")
    public String nivelVerificacion;
}
