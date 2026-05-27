package com.airtellecta.dto.response;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Política de control de tabaco aplicada en la simulación y su efecto estimado")
public class PoliticaAplicadaDto {

    @Schema(description = "Clave interna de la política.", example = "AMBIENTES_LIBRES")
    public String clave;

    @Schema(description = "Nombre descriptivo de la política.", example = "Ambientes 100% libres de humo")
    public String nombre;

    @Schema(description = "Reducción estimada en prevalencia atribuida a esta política (puntos porcentuales).", example = "1.5")
    public BigDecimal efectoPct;
}
