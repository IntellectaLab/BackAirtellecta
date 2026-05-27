package com.airtellecta.dto.response;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Indicadores nacionales de tabaquismo en México")
public class ResumenNacionalDto {

    @Schema(description = "Prevalencia de fumadores actuales en porcentaje (ej. 17.6 = 17.6%).", example = "17.6")
    public BigDecimal prevalenciaFumadores;

    @Schema(description = "Población estimada de fumadores.", example = "14800000")
    public Long poblacionFumadores;

    @Schema(description = "Número estimado de usuarios de dispositivos de vapeo/IQOS.", example = "1200000")
    public Integer usuariosVapeo;

    @Schema(description = "Número estimado de usuarios con uso dual (cigarrillos + vapeo).", example = "450000")
    public Integer usoDual;

    @Schema(description = "Defunciones anuales atribuibles al tabaquismo (código F17).", example = "65000")
    public Long defuncionesF17;

    @Schema(description = "Urgencias hospitalarias anuales atribuibles al tabaquismo (código F17).", example = "320000")
    public Long urgenciasF17;

    @Schema(description = "Población total de México.", example = "128455567")
    public Long poblacionTotal;
}
