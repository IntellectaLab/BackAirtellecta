package com.airtellecta.dto.response;

import java.math.BigDecimal;

public class ProyeccionAnualDto {

    public int anio;
    public BigDecimal prevalenciaPct;
    public long fumadoresAbsolutos;
    public long defuncionesEvitadas;
    public BigDecimal ahorroMdp;

    // v2 fields — backwards compatible (null if not set)
    public BigDecimal prevalenciaBaselinePct;
    public long defuncionesEvitadasAcumuladas;
}
