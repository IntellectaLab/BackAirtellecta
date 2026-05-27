package com.airtellecta.dto.response;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Datos de mortalidad y urgencias atribuibles al tabaquismo")
public class MortalidadDto {

    @Schema(description = "Serie histórica anual de defunciones y urgencias (F17 y todas las causas).")
    public List<SerieAnualDto> series;

    @Schema(description = "Desglose de defunciones y urgencias por código CIE-10.")
    public List<DesgloseCie10Dto> desgloseCie10;
}
