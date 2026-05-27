package com.airtellecta.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class CargaEconomicaDto {

    public BigDecimal costoDirectoAnualMdp;
    public BigDecimal costoSocialAnualMdp;
    public BigDecimal inversionPrevencionMdp;
    public List<FuenteDatoDto> fuentes;
}
