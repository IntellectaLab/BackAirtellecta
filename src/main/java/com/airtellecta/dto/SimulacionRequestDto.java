package com.airtellecta.dto;

import java.math.BigDecimal;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Parámetros para ejecutar una simulación de impacto de políticas de control de tabaco")
public class SimulacionRequestDto {

    @Schema(
        description = "Lista de claves de políticas a aplicar en la simulación. " +
                      "Ejemplo de claves: 'AMBIENTES_LIBRES', 'PUBLICIDAD', 'ADVERTENCIAS', 'CESACION', 'MINIMO_PRECIO'.",
        example = "[\"AMBIENTES_LIBRES\", \"ADVERTENCIAS\"]"
    )
    public List<String> politicas;

    @Schema(
        description = "Porcentaje de impuesto sobre el precio de venta al consumidor (en tanto por uno). " +
                      "Ejemplo: 0.75 equivale a 75% del precio. Usar null para mantener el impuesto actual.",
        example = "0.75"
    )
    public BigDecimal impuestoPctPrecio;

    @Schema(
        description = "Número de años a proyectar a partir del año base (2025). Rango recomendado: 1-20.",
        example = "10",
        minimum = "1",
        maximum = "20"
    )
    public Integer horizonteAnios;
}
