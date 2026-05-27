package com.airtellecta.dto.response;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Resultado completo de una simulación de políticas de control de tabaco")
public class SimulacionResultadoDto {

    @Schema(description = "Parámetros epidemiológicos y fiscales usados como punto de partida.")
    public ParametrosBaseDto parametrosBase;

    @Schema(description = "Proyección año a año durante el horizonte temporal configurado.")
    public List<ProyeccionAnualDto> proyeccion;

    @Schema(description = "Resumen acumulado al final del horizonte temporal.")
    public ResumenFinalDto resumenFinal;

    @Schema(description = "Detalle de cada política aplicada y su efecto estimado sobre la prevalencia.")
    public List<PoliticaAplicadaDto> politicasAplicadas;

    @Schema(description = "Elasticidades de precio utilizadas para modelar el efecto fiscal.")
    public ElasticidadesAplicadasDto elasticidadesAplicadas;
}
