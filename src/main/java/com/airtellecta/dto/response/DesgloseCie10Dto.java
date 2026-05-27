package com.airtellecta.dto.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Acumulado histórico de defunciones y urgencias para un código CIE-10 específico")
public class DesgloseCie10Dto {

    @Schema(description = "Código CIE-10.", example = "F17")
    public String codigo;

    @Schema(description = "Nombre del trastorno.", example = "Trastornos mentales y del comportamiento debidos al tabaco")
    public String trastorno;

    @Schema(description = "Total acumulado de defunciones en el período disponible.", example = "320000")
    public Long totalDefunciones;

    @Schema(description = "Total acumulado de urgencias en el período disponible.", example = "1850000")
    public Long totalUrgencias;
}
