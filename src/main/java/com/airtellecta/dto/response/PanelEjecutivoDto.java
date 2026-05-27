package com.airtellecta.dto.response;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Resumen ejecutivo consolidado con carga económica, recaudación y epidemiología")
public class PanelEjecutivoDto {

    @Schema(description = "Datos de carga económica directa e indirecta del tabaquismo.")
    public CargaEconomicaDto cargaEconomica;

    @Schema(description = "Datos de recaudación IEPS más reciente.")
    public RecaudacionPanelDto recaudacion;

    @Schema(description = "Indicadores epidemiológicos clave.")
    public EpidemiologiaPanelDto epidemiologia;

    @Schema(description = "Lista de costos por patología relacionada con el tabaquismo.")
    public List<CostoPatologiaDto> costosPorPatologia;
}
