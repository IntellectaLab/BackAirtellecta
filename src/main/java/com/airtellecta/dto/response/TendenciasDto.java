package com.airtellecta.dto.response;

import java.math.BigDecimal;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Comparativo de indicadores de tabaquismo entre ENCODAT 2016 y ENCODAT 2025")
public class TendenciasDto {

    @Schema(description = "Prevalencia de fumadores en 2016 (%).", example = "20.4")
    public BigDecimal prevalencia2016;

    @Schema(description = "Prevalencia de fumadores en 2025 (%).", example = "17.6")
    public BigDecimal prevalencia2025;

    @Schema(description = "Delta en puntos porcentuales (prevalencia2025 - prevalencia2016). Negativo indica reducción.", example = "-2.8")
    public BigDecimal deltaPp;

    @Schema(description = "Número absoluto de fumadores en 2016.", example = "17300000")
    public Long fumadores2016;

    @Schema(description = "Número absoluto de fumadores en 2025.", example = "14800000")
    public Long fumadores2025;

    @Schema(description = "Número de usuarios de vapeo en 2016.", example = "200000")
    public Integer vapeo2016;

    @Schema(description = "Número de usuarios de vapeo en 2025.", example = "1200000")
    public Integer vapeo2025;

    @Schema(description = "Número de usuarios con uso dual en 2016.", example = "100000")
    public Integer dual2016;

    @Schema(description = "Número de usuarios con uso dual en 2025.", example = "450000")
    public Integer dual2025;
}
