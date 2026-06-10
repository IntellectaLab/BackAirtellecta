package com.airtellecta;

import com.airtellecta.dto.response.*;
import com.airtellecta.service.ExcelExportService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelExportServiceTest {

    private final ExcelExportService service = new ExcelExportService();

    @Test
    void generarPanelEjecutivoExcel_produceBytesValidos() throws IOException {
        PanelEjecutivoDto panel = buildPanel();

        byte[] bytes = service.generarPanelEjecutivoExcel(panel);

        assertNotNull(bytes);
        assertTrue(bytes.length > 0, "El archivo no debe estar vacío");

        // Verifica que los bytes son un XLSX válido
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(bytes))) {
            assertEquals(2, wb.getNumberOfSheets(), "Debe tener 2 hojas");
            assertEquals("Indicadores",        wb.getSheetAt(0).getSheetName());
            assertEquals("Costos por Patología", wb.getSheetAt(1).getSheetName());

            // Hoja Indicadores: título en fila 0
            String titulo = wb.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
            assertEquals("AirTellecta — Panel Ejecutivo", titulo);

            // Hoja Costos: al menos una fila de datos (fila 3)
            assertNotNull(wb.getSheetAt(1).getRow(3), "Debe haber datos de costos");
        }
    }

    // ── datos de prueba ───────────────────────────────────────────────────────

    private PanelEjecutivoDto buildPanel() {
        PanelEjecutivoDto panel = new PanelEjecutivoDto();

        CargaEconomicaDto carga = new CargaEconomicaDto();
        carga.costoDirectoAnualMdp    = new BigDecimal("145792");
        carga.costoSocialAnualMdp     = new BigDecimal("89000");
        carga.inversionPrevencionMdp  = new BigDecimal("2500");
        panel.cargaEconomica = carga;

        RecaudacionPanelDto rec = new RecaudacionPanelDto();
        rec.iepsMasRecienteMdp  = new BigDecimal("49522");
        rec.anio                = 2025;
        rec.fuente              = "SAT";
        panel.recaudacion = rec;

        EpidemiologiaPanelDto epi = new EpidemiologiaPanelDto();
        epi.prevalenciaActualPct       = new BigDecimal("15.1");
        epi.prevalenciaHistoricaPct    = new BigDecimal("16.9");
        epi.deltaPp                    = new BigDecimal("-1.8");
        epi.fumadoresEstimados         = 14_800_000L;
        epi.defuncionesAtribuiblesAnual = 60_000L;
        epi.poblacion18Plus            = 98_000_000L;
        panel.epidemiologia = epi;

        CostoPatologiaDto costo = new CostoPatologiaDto();
        costo.codigo          = "I21";
        costo.trastorno       = "Infarto agudo de miocardio";
        costo.costoAjustado2025 = new BigDecimal("307781.1");
        costo.anioBase        = 2018;
        costo.fuente          = "IMSS 2018";
        panel.costosPorPatologia = List.of(costo);

        return panel;
    }
}
