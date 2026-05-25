package com.airtellecta.service;

import com.airtellecta.dto.response.CostoPatologiaDto;
import com.airtellecta.dto.response.PanelEjecutivoDto;
import com.airtellecta.dto.response.PoliticaAplicadaDto;
import com.airtellecta.dto.response.ProyeccionAnualDto;
import com.airtellecta.dto.response.SimulacionResultadoDto;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class ExcelExportService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    public byte[] generarSimulacionExcel(SimulacionResultadoDto resultado) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFCellStyle titleStyle = buildTitleStyle(wb);
            XSSFCellStyle headerStyle = buildHeaderStyle(wb);
            XSSFCellStyle dateStyle = buildDateStyle(wb);

            buildSheetParametros(wb, resultado, titleStyle, headerStyle, dateStyle);
            buildSheetProyeccion(wb, resultado, titleStyle, headerStyle);
            buildSheetFuentes(wb, titleStyle, headerStyle);

            wb.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generarPanelEjecutivoExcel(PanelEjecutivoDto panel) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XSSFCellStyle titleStyle = buildTitleStyle(wb);
            XSSFCellStyle headerStyle = buildHeaderStyle(wb);
            XSSFCellStyle dateStyle = buildDateStyle(wb);

            buildSheetIndicadores(wb, panel, titleStyle, headerStyle, dateStyle);
            buildSheetCostosPatologia(wb, panel, titleStyle, headerStyle);

            wb.write(out);
            return out.toByteArray();
        }
    }

    // -------------------------------------------------------------------------
    // Simulacion sheets
    // -------------------------------------------------------------------------

    private void buildSheetParametros(XSSFWorkbook wb, SimulacionResultadoDto resultado,
                                       XSSFCellStyle titleStyle, XSSFCellStyle headerStyle,
                                       XSSFCellStyle dateStyle) {
        XSSFSheet sheet = wb.createSheet("Parámetros");

        // Row 0 — title
        XSSFRow r0 = sheet.createRow(0);
        XSSFCell c0 = r0.createCell(0);
        c0.setCellValue("AirTellecta — Simulación de Políticas");
        c0.setCellStyle(titleStyle);

        // Row 1 — date
        XSSFRow r1 = sheet.createRow(1);
        XSSFCell c1 = r1.createCell(0);
        c1.setCellValue("Generado: " + LocalDateTime.now().format(FORMATTER));
        c1.setCellStyle(dateStyle);

        // Row 3 — header
        XSSFRow r3 = sheet.createRow(3);
        setCellHeader(r3, 0, "Parámetro", headerStyle);
        setCellHeader(r3, 1, "Valor", headerStyle);
        setCellHeader(r3, 2, "Fuente", headerStyle);

        int rowIdx = 4;

        // Data rows
        if (resultado.parametrosBase != null) {
            rowIdx = addDataRow(sheet, rowIdx, "Prevalencia base (%)",
                    resultado.parametrosBase.prevalenciaBasePct != null
                            ? resultado.parametrosBase.prevalenciaBasePct.toPlainString() : "",
                    "ENCODAT 2025");
            rowIdx = addDataRow(sheet, rowIdx, "Población 18+",
                    String.valueOf(resultado.parametrosBase.poblacion18Plus),
                    "CONAPO 2025");
            rowIdx = addDataRow(sheet, rowIdx, "Fumadores base",
                    String.valueOf(resultado.parametrosBase.fumadoresBase),
                    "Calculado");
            rowIdx = addDataRow(sheet, rowIdx, "Defunciones atrib./año",
                    String.valueOf(resultado.parametrosBase.defuncionesAtribuiblesBase),
                    "GBD + INEGI");
            rowIdx = addDataRow(sheet, rowIdx, "Impuesto actual (% precio)",
                    resultado.parametrosBase.impuestoActualPctPrecio != null
                            ? resultado.parametrosBase.impuestoActualPctPrecio.toPlainString() : "",
                    "SHCP/IEPS");
        }

        // Policies block
        if (resultado.politicasAplicadas != null && !resultado.politicasAplicadas.isEmpty()) {
            rowIdx++; // blank row
            XSSFRow rh = sheet.createRow(rowIdx++);
            setCellHeader(rh, 0, "Política aplicada", headerStyle);
            setCellHeader(rh, 1, "Efecto (%)", headerStyle);

            for (PoliticaAplicadaDto p : resultado.politicasAplicadas) {
                XSSFRow rp = sheet.createRow(rowIdx++);
                rp.createCell(0).setCellValue(p.nombre != null ? p.nombre : "");
                rp.createCell(1).setCellValue(p.efectoPct != null ? p.efectoPct.toPlainString() : "");
            }
        }

        autoSizeColumns(sheet, 3);
    }

    private void buildSheetProyeccion(XSSFWorkbook wb, SimulacionResultadoDto resultado,
                                       XSSFCellStyle titleStyle, XSSFCellStyle headerStyle) {
        XSSFSheet sheet = wb.createSheet("Proyección");

        // Row 0 — title
        XSSFRow r0 = sheet.createRow(0);
        XSSFCell c0 = r0.createCell(0);
        c0.setCellValue("AirTellecta — Simulación de Políticas");
        c0.setCellStyle(titleStyle);

        // Row 2 — header
        XSSFRow r2 = sheet.createRow(2);
        setCellHeader(r2, 0, "Año", headerStyle);
        setCellHeader(r2, 1, "Prevalencia %", headerStyle);
        setCellHeader(r2, 2, "Fumadores", headerStyle);
        setCellHeader(r2, 3, "Muertes evitadas", headerStyle);
        setCellHeader(r2, 4, "Ahorro MDP", headerStyle);

        int rowIdx = 3;
        if (resultado.proyeccion != null) {
            for (ProyeccionAnualDto p : resultado.proyeccion) {
                XSSFRow row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.anio);
                row.createCell(1).setCellValue(p.prevalenciaPct != null ? p.prevalenciaPct.doubleValue() : 0);
                row.createCell(2).setCellValue(p.fumadoresAbsolutos);
                row.createCell(3).setCellValue(p.defuncionesEvitadas);
                row.createCell(4).setCellValue(p.ahorroMdp != null ? p.ahorroMdp.doubleValue() : 0);
            }
        }

        autoSizeColumns(sheet, 5);
    }

    private void buildSheetFuentes(XSSFWorkbook wb, XSSFCellStyle titleStyle, XSSFCellStyle headerStyle) {
        XSSFSheet sheet = wb.createSheet("Fuentes");

        // Row 0 — title
        XSSFRow r0 = sheet.createRow(0);
        XSSFCell c0 = r0.createCell(0);
        c0.setCellValue("AirTellecta — Simulación de Políticas");
        c0.setCellStyle(titleStyle);

        // Row 2 — header
        XSSFRow r2 = sheet.createRow(2);
        setCellHeader(r2, 0, "Dato", headerStyle);
        setCellHeader(r2, 1, "Fuente", headerStyle);

        int rowIdx = 3;
        rowIdx = addDataRow(sheet, rowIdx, "Modelo", "SimSmoke/Levy");
        rowIdx = addDataRow(sheet, rowIdx, "Prevalencia", "ENCODAT");
        rowIdx = addDataRow(sheet, rowIdx, "Defunciones", "GBD + INEGI");
        rowIdx = addDataRow(sheet, rowIdx, "Elasticidades", "SimSmoke Table 1");
        rowIdx = addDataRow(sheet, rowIdx, "Población", "CONAPO");
        addDataRow(sheet, rowIdx, "Costos", "Reynales-Shigematsu");

        autoSizeColumns(sheet, 2);
    }

    // -------------------------------------------------------------------------
    // Panel Ejecutivo sheets
    // -------------------------------------------------------------------------

    private void buildSheetIndicadores(XSSFWorkbook wb, PanelEjecutivoDto panel,
                                        XSSFCellStyle titleStyle, XSSFCellStyle headerStyle,
                                        XSSFCellStyle dateStyle) {
        XSSFSheet sheet = wb.createSheet("Indicadores");

        // Row 0 — title
        XSSFRow r0 = sheet.createRow(0);
        XSSFCell c0 = r0.createCell(0);
        c0.setCellValue("AirTellecta — Panel Ejecutivo");
        c0.setCellStyle(titleStyle);

        // Row 1 — date
        XSSFRow r1 = sheet.createRow(1);
        XSSFCell c1 = r1.createCell(0);
        c1.setCellValue("Generado: " + LocalDateTime.now().format(FORMATTER));
        c1.setCellStyle(dateStyle);

        // Row 3 — header
        XSSFRow r3 = sheet.createRow(3);
        setCellHeader(r3, 0, "Indicador", headerStyle);
        setCellHeader(r3, 1, "Valor", headerStyle);
        setCellHeader(r3, 2, "Fuente", headerStyle);

        int rowIdx = 4;

        if (panel.cargaEconomica != null) {
            rowIdx = addDataRow(sheet, rowIdx, "Costo directo anual (MDP)",
                    panel.cargaEconomica.costoDirectoAnualMdp != null
                            ? panel.cargaEconomica.costoDirectoAnualMdp.toPlainString() : "",
                    "Carga Económica");
            rowIdx = addDataRow(sheet, rowIdx, "Costo social anual (MDP)",
                    panel.cargaEconomica.costoSocialAnualMdp != null
                            ? panel.cargaEconomica.costoSocialAnualMdp.toPlainString() : "",
                    "Carga Económica");
            rowIdx = addDataRow(sheet, rowIdx, "Inversión prevención (MDP)",
                    panel.cargaEconomica.inversionPrevencionMdp != null
                            ? panel.cargaEconomica.inversionPrevencionMdp.toPlainString() : "",
                    "Carga Económica");
        }

        if (panel.recaudacion != null) {
            rowIdx = addDataRow(sheet, rowIdx, "Recaudación IEPS (MDP)",
                    panel.recaudacion.iepsMasRecienteMdp != null
                            ? panel.recaudacion.iepsMasRecienteMdp.toPlainString() : "",
                    panel.recaudacion.fuente != null ? panel.recaudacion.fuente : "SHCP/IEPS");
        }

        if (panel.epidemiologia != null) {
            rowIdx = addDataRow(sheet, rowIdx, "Prevalencia actual (%)",
                    panel.epidemiologia.prevalenciaActualPct != null
                            ? panel.epidemiologia.prevalenciaActualPct.toPlainString() : "",
                    "ENCODAT");
            rowIdx = addDataRow(sheet, rowIdx, "Defunciones atribuibles/año",
                    String.valueOf(panel.epidemiologia.defuncionesAtribuiblesAnual),
                    "GBD + INEGI");
            addDataRow(sheet, rowIdx, "Población 18+",
                    String.valueOf(panel.epidemiologia.poblacion18Plus),
                    "CONAPO");
        }

        autoSizeColumns(sheet, 3);
    }

    private void buildSheetCostosPatologia(XSSFWorkbook wb, PanelEjecutivoDto panel,
                                            XSSFCellStyle titleStyle, XSSFCellStyle headerStyle) {
        XSSFSheet sheet = wb.createSheet("Costos por Patología");

        // Row 0 — title
        XSSFRow r0 = sheet.createRow(0);
        XSSFCell c0 = r0.createCell(0);
        c0.setCellValue("AirTellecta — Panel Ejecutivo");
        c0.setCellStyle(titleStyle);

        // Row 2 — header
        XSSFRow r2 = sheet.createRow(2);
        setCellHeader(r2, 0, "Código", headerStyle);
        setCellHeader(r2, 1, "Trastorno", headerStyle);
        setCellHeader(r2, 2, "Costo 2025 (MDP)", headerStyle);
        setCellHeader(r2, 3, "Fuente", headerStyle);

        int rowIdx = 3;
        if (panel.costosPorPatologia != null) {
            for (CostoPatologiaDto cp : panel.costosPorPatologia) {
                XSSFRow row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(cp.codigo != null ? cp.codigo : "");
                row.createCell(1).setCellValue(cp.trastorno != null ? cp.trastorno : "");
                row.createCell(2).setCellValue(cp.costoAjustado2025 != null ? cp.costoAjustado2025.doubleValue() : 0);
                row.createCell(3).setCellValue(cp.fuente != null ? cp.fuente : "");
            }
        }

        autoSizeColumns(sheet, 4);
    }

    // -------------------------------------------------------------------------
    // Style helpers
    // -------------------------------------------------------------------------

    private XSSFCellStyle buildTitleStyle(XSSFWorkbook wb) {
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);

        XSSFCellStyle style = wb.createCellStyle();
        style.setFont(font);
        return style;
    }

    private XSSFCellStyle buildDateStyle(XSSFWorkbook wb) {
        XSSFCellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setItalic(true);
        style.setFont(font);
        return style;
    }

    private XSSFCellStyle buildHeaderStyle(XSSFWorkbook wb) {
        // Dark blue background (#1F497D), white bold text
        XSSFFont font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());

        XSSFCellStyle style = wb.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 0x1F, (byte) 0x49, (byte) 0x7D}, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    // -------------------------------------------------------------------------
    // Row / cell helpers
    // -------------------------------------------------------------------------

    private void setCellHeader(XSSFRow row, int col, String value, XSSFCellStyle style) {
        XSSFCell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    /** addDataRow with 3-column variant (Parámetro / Valor / Fuente) */
    private int addDataRow(XSSFSheet sheet, int rowIdx, String param, String value, String fuente) {
        XSSFRow row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(param);
        row.createCell(1).setCellValue(value);
        row.createCell(2).setCellValue(fuente);
        return rowIdx + 1;
    }

    /** addDataRow with 2-column variant (Dato / Fuente) */
    private int addDataRow(XSSFSheet sheet, int rowIdx, String dato, String fuente) {
        XSSFRow row = sheet.createRow(rowIdx);
        row.createCell(0).setCellValue(dato);
        row.createCell(1).setCellValue(fuente);
        return rowIdx + 1;
    }

    private void autoSizeColumns(XSSFSheet sheet, int numCols) {
        for (int i = 0; i < numCols; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
