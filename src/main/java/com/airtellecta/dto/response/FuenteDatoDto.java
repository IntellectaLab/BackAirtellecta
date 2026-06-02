package com.airtellecta.dto.response;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Referencia a la fuente de un dato específico con su nivel de verificación")
public class FuenteDatoDto {

    @Schema(description = "Nombre del campo o indicador al que corresponde la fuente.", example = "costoDirectoAnualMdp")
    public String campo;

    @Schema(description = "Nombre de la fuente oficial.", example = "IMSS – Memoria Estadística 2022")
    public String fuente;

    @Schema(description = "URL de la fuente.", example = "https://www.imss.gob.mx/sites/all/statics/pdf/memorias/2022.pdf")
    public String fuenteUrl;

    @Schema(description = "Año de referencia del dato.", example = "2022")
    public short anioReferencia;

    @Schema(description = "Nivel de verificación: OFICIAL, ESTIMADO, PROYECTADO.", example = "OFICIAL")
    public String nivelVerificacion;
}
