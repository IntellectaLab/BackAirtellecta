package com.airtellecta.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class EpidemiologiaPanelDto {

    public BigDecimal prevalenciaActualPct;
    public BigDecimal prevalenciaHistoricaPct;
    public BigDecimal deltaPp;
    public long fumadoresEstimados;
    public long defuncionesAtribuiblesAnual;
    public long poblacion18Plus;
    public List<FuenteDatoDto> fuentes;
}
