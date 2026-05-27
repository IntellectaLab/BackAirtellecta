package com.airtellecta.dto.response;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Información de elasticidades de precio usadas para calcular el efecto fiscal en la simulación")
public class ElasticidadesAplicadasDto {

    @Schema(description = "Nuevo porcentaje de impuesto sobre el precio aplicado en la simulación.", example = "0.75")
    public BigDecimal impuestoNuevoPctPrecio;

    @Schema(description = "Incremento porcentual en el precio de venta resultante del ajuste fiscal.", example = "44.4")
    public BigDecimal incrementoPrecioPct;

    @Schema(description = "Efecto promedio ponderado de la elasticidad precio sobre la prevalencia (%).", example = "-3.1")
    public BigDecimal efectoPromedioPct;
}
