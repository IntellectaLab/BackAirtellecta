package com.airtellecta.dto.response;

import java.math.BigDecimal;

public class PoliticaAplicadaDto {

    public String clave;
    public String nombre;
    public BigDecimal efectoPct;

    // v2 fields
    public BigDecimal efectoInicioPct;
    public BigDecimal efectoCesacionPct;
}
