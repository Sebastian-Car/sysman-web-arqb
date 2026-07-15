/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.bancoproyectos.reportes;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoTresRemote;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * 
 * @version 2, 04/10/2017
 * @author jreina se realizaro el reemplazo por EJB
 * 
 */

public class BancoProyectosReportes {
    
    private static final String AVANCEFISICO = "% AVANCE FÍSICO";
    private static final String PORCENTAJE  = "0.00%";
    private static final String SPORCENTAJE  = "SPORCENTAJE";
    private static final String SVALOR  = "SVALOR";
    private static final String VALORTOTAL  = "VALOR TOTAL";
    
    
    private  BancoProyectosReportes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }
    
    public static StreamedContent generarInformeFES(String proyectoInicial,
        String vigenciaInicial, int modulo,
        FormContinuoService service, EjbBancoProyectoTresRemote ejb, Log logger){
        
        List<Registro> listaConsulta;
        StreamedContent aux=null;
        int i;
        int periocidad;
        int l;
        // creando objetos excel
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("Proyecto");
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
        CellStyle estiloBorder = workbook.createCellStyle();
        estiloBorder.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        estiloBorder.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        estiloBorder.setBorderRight(HSSFCellStyle.BORDER_THIN);
        estiloBorder.setBorderTop(HSSFCellStyle.BORDER_THIN);

        DataFormat formatoPorcentaje = workbook.createDataFormat();
        DataFormat formatoNumero = workbook.createDataFormat();

        CellStyle estiloPorcentaje = workbook.createCellStyle();
        estiloPorcentaje.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        estiloPorcentaje.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        estiloPorcentaje.setBorderRight(HSSFCellStyle.BORDER_THIN);
        estiloPorcentaje.setBorderTop(HSSFCellStyle.BORDER_THIN);
        estiloPorcentaje.setDataFormat(formatoPorcentaje.getFormat(PORCENTAJE));

        CellStyle estiloNumero = workbook.createCellStyle();
        estiloNumero.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        estiloNumero.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        estiloNumero.setBorderRight(HSSFCellStyle.BORDER_THIN);
        estiloNumero.setBorderTop(HSSFCellStyle.BORDER_THIN);
        estiloNumero.setDataFormat(formatoNumero.getFormat("#,##0.00"));

        CellStyle estiloContenido = workbook.createCellStyle();
        estiloContenido.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        estiloContenido.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        estiloContenido.setBorderRight(HSSFCellStyle.BORDER_THIN);
        estiloContenido.setBorderTop(HSSFCellStyle.BORDER_THIN);

        CellStyle estiloE = workbook.createCellStyle();
        estiloE.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        estiloE.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        estiloE.setBorderRight(HSSFCellStyle.BORDER_THIN);
        estiloE.setBorderTop(HSSFCellStyle.BORDER_THIN);

        CellStyle estiloNumeroE = workbook.createCellStyle();
        estiloNumeroE.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        estiloNumeroE.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        estiloNumeroE.setBorderRight(HSSFCellStyle.BORDER_THIN);
        estiloNumeroE.setBorderTop(HSSFCellStyle.BORDER_THIN);
        estiloNumeroE.setDataFormat(formatoNumero.getFormat("#,##0.00"));

        CellStyle estiloPorcentajeE = workbook.createCellStyle();
        estiloPorcentajeE.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        estiloPorcentajeE.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        estiloPorcentajeE.setBorderRight(HSSFCellStyle.BORDER_THIN);
        estiloPorcentajeE.setBorderTop(HSSFCellStyle.BORDER_THIN);
        estiloPorcentajeE.setDataFormat(formatoPorcentaje.getFormat(PORCENTAJE));

        CellStyle estiloFirma = workbook.createCellStyle();

        CellStyle estiloTitulo = workbook.createCellStyle();
        estiloTitulo.setAlignment(CellStyle.ALIGN_CENTER);

        FormulaEvaluator evaluator = workbook.getCreationHelper()
                        .createFormulaEvaluator();

        Font font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontHeightInPoints((short) 7);

        Font fontContenido = workbook.createFont();
        fontContenido.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        fontContenido.setFontHeightInPoints((short) 7);

        Font fontE = workbook.createFont();
        fontE.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        fontE.setFontHeightInPoints((short) 7);
        fontE.setColor(HSSFColor.BLUE.index);

        estiloBorder.setFont(font);
        estiloPorcentaje.setFont(fontContenido);
        estiloNumero.setFont(fontContenido);
        estiloContenido.setFont(fontContenido);
        estiloE.setFont(fontE);
        estiloNumeroE.setFont(fontE);
        estiloPorcentajeE.setFont(fontE);
        estiloFirma.setFont(fontContenido);
        estiloTitulo.setFont(font);

        // Encabezado de la plantilla
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("proyecto", proyectoInicial);
        reemplazar.put("vigencia", vigenciaInicial);
        String strSql = Reporteador.resuelveConsulta("800023InformesProyecto",
                        modulo, reemplazar);
        listaConsulta = service.getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);
        if (listaConsulta.isEmpty()) {
            return null;
        }
        else {

            CellRangeAddress rangoTitulo1 = new CellRangeAddress(1, 1, 0, 15);
            sheet.addMergedRegion(rangoTitulo1);

            Row rowtitulo1 = sheet.createRow(1);

            Cell cellTitulo1 = rowtitulo1.createCell(0);
            cellTitulo1.setCellValue(
                            SessionUtil.getCompaniaIngreso().getNombre());
            cellTitulo1.setCellStyle(estiloTitulo);

            CellRangeAddress rangoTitulo2 = new CellRangeAddress(2, 2, 0, 15);
            sheet.addMergedRegion(rangoTitulo2);

            Row rowTitulo2 = sheet.createRow(2);

            Cell cellTitulo2 = rowTitulo2.createCell(0);
            cellTitulo2.setCellValue(
                            "SEGUIMIENTO Y EVALUACIÓN A LOS PROYECTOS DE INVERSIÓN");
            cellTitulo2.setCellStyle(estiloTitulo);

            i = 2;

            // Fila 1
            Row row1 = sheet.createRow(i + 2);

            CellRangeAddress rango1 = new CellRangeAddress(i + 2, i + 2, 0, 8);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rango1, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rango1, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rango1, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rango1, sheet,
                            workbook);
            sheet.addMergedRegion(rango1);

            Cell cellSecretaria = row1.createCell(0);
            cellSecretaria.setCellValue(
                            "SECRETARIA Y/O INSTITUTO: "
                                + listaConsulta.get(0).getCampos()
                                                .get("NOMBRESECRETARIA"));
            cellSecretaria.setCellStyle(estiloBorder);

            CellRangeAddress rangoProyecto = new CellRangeAddress(i + 2, i + 2,
                            10, 15);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoProyecto,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoProyecto,
                            sheet, workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoProyecto,
                            sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoProyecto, sheet,
                            workbook);
            sheet.addMergedRegion(rangoProyecto);

            Cell cellProyecto = row1.createCell(10);
            cellProyecto.setCellValue("PROYECTO: "
                + listaConsulta.get(0).getCampos().get("NOMBREPROYECTO"));
            cellProyecto.setCellStyle(estiloBorder);

            // Fila 2
            Row row2 = sheet.createRow(i + 3);

            CellRangeAddress rangoCod = new CellRangeAddress(i + 3, i + 3, 10,
                            11);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoCod, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoCod, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoCod, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoCod, sheet,
                            workbook);
            sheet.addMergedRegion(rangoCod);

            Cell cellCod = row2.createCell(10);
            cellCod.setCellValue("CODIGOBPIM: ");
            cellCod.setCellStyle(estiloBorder);

            CellRangeAddress rangoCodbpmi = new CellRangeAddress(i + 3, i + 3,
                            12, 13);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoCodbpmi,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoCodbpmi, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoCodbpmi,
                            sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoCodbpmi, sheet,
                            workbook);
            sheet.addMergedRegion(rangoCodbpmi);

            Cell cellCodBpmi = row2.createCell(12);
            cellCodBpmi.setCellValue((String) listaConsulta.get(0).getCampos()
                            .get("CODIGOBPIM"));
            cellCodBpmi.setCellStyle(estiloBorder);

            Cell cellVig = row2.createCell(14);
            cellVig.setCellValue("VIGENCIA: ");
            cellVig.setCellStyle(estiloBorder);

            Cell cellVigencia = row2.createCell(15);
            cellVigencia.setCellValue(vigenciaInicial);
            cellVigencia.setCellStyle(estiloBorder);

            // Fila 3
            Row row3 = sheet.createRow(i + 4);

            CellRangeAddress rangoProg = new CellRangeAddress(i + 4, i + 4, 0,
                            8);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoProg, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoProg, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoProg, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoProg, sheet,
                            workbook);
            sheet.addMergedRegion(rangoProg);

            Cell cellProg = row3.createCell(0);
            cellProg.setCellValue("PROGRAMA: "
                + listaConsulta.get(0).getCampos().get("NOMBREPROGRAMA"));
            cellProg.setCellStyle(estiloBorder);

            CellRangeAddress rangoVal = new CellRangeAddress(i + 4, i + 4, 10,
                            11);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoVal, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoVal, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoVal, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoVal, sheet,
                            workbook);
            sheet.addMergedRegion(rangoVal);

            Cell cellVal = row3.createCell(10);
            cellVal.setCellValue("VALOR PROYECTO $: ");
            cellVal.setCellStyle(estiloBorder);

            CellRangeAddress rangoValTotal = new CellRangeAddress(i + 4, i + 4,
                            12, 13);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoValTotal,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoValTotal,
                            sheet, workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoValTotal,
                            sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoValTotal, sheet,
                            workbook);
            sheet.addMergedRegion(rangoValTotal);

            Cell cellValTotal = row3.createCell(12);
            cellValTotal.setCellValue(((BigDecimal) listaConsulta.get(0)
                            .getCampos().get("VALORTOTAL")).toString());
            cellValTotal.setCellStyle(estiloNumero);

            Cell cellValProg = row3.createCell(14);
            cellValProg.setCellValue("VALOR PROGRAMADO $: ");
            cellValProg.setCellStyle(estiloBorder);

            Cell cellValProgramado = row3.createCell(15);
            cellValProgramado
                            .setCellValue(((BigDecimal) listaConsulta.get(0)
                                            .getCampos().get("VALORPROGRAMADO"))
                                                            .toString());
            cellValProgramado.setCellStyle(estiloNumero);

            // Fila 4
            Row row4 = sheet.createRow(i + 6);

            CellRangeAddress rangoSubPro = new CellRangeAddress(i + 6, i + 6, 0,
                            8);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoSubPro,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoSubPro, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoSubPro, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoSubPro, sheet,
                            workbook);
            sheet.addMergedRegion(rangoSubPro);

            Cell cellSubPro = row4.createCell(0);
            cellSubPro.setCellValue("SUBPROGRAMA: "
                + listaConsulta.get(0).getCampos().get("NOMBRESUBPROGRAMA"));
            cellSubPro.setCellStyle(estiloBorder);

            CellRangeAddress rangoResp = new CellRangeAddress(i + 6, i + 6, 10,
                            15);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoResp, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoResp, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoResp, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoResp, sheet,
                            workbook);
            sheet.addMergedRegion(rangoResp);

            Cell cellRespo = row4.createCell(10);
            cellRespo.setCellValue("RESPONSABLE: "
                + listaConsulta.get(0).getCampos().get("NOMBRERESPONSABLE"));
            cellRespo.setCellStyle(estiloBorder);

            // Fila 5
            Row row5 = sheet.createRow(i + 8);

            CellRangeAddress rangoMeta = new CellRangeAddress(i + 8, i + 8, 0,
                            4);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoMeta, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoMeta, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoMeta, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoMeta, sheet,
                            workbook);
            sheet.addMergedRegion(rangoMeta);

            Cell cellMeta = row5.createCell(0);
            cellMeta.setCellValue("META - SUBPROGRAMA");
            cellMeta.setCellStyle(estiloBorder);

            Cell cellRubro = row5.createCell(10);
            cellRubro.setCellValue("RUBRO");
            cellRubro.setCellStyle(estiloBorder);

            CellRangeAddress rangoUnidad = new CellRangeAddress(i + 8, i + 9, 5,
                            5);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoUnidad,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoUnidad, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoUnidad, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoUnidad, sheet,
                            workbook);
            sheet.addMergedRegion(rangoUnidad);

            Cell cellUnidad = row5.createCell(5);
            cellUnidad.setCellValue("UNIDAD");
            cellUnidad.setCellStyle(estiloBorder);

            CellRangeAddress rangoValActual = new CellRangeAddress(i + 8, i + 9,
                            6, 6);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoValActual,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoValActual,
                            sheet, workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoValActual,
                            sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoValActual,
                            sheet, workbook);
            sheet.addMergedRegion(rangoValActual);

            Cell cellValActual = row5.createCell(6);
            cellValActual.setCellValue("VALOR ACTUAL");
            cellValActual.setCellStyle(estiloBorder);

            CellRangeAddress rangoMeta1 = new CellRangeAddress(i + 8, i + 9, 7,
                            7);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoMeta1, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoMeta1, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoMeta1, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoMeta1, sheet,
                            workbook);
            sheet.addMergedRegion(rangoMeta1);

            Cell cellMeta1 = row5.createCell(7);
            cellMeta1.setCellValue("META AÑO 1");
            cellMeta1.setCellStyle(estiloBorder);

            // Fila 6
            Row row6 = sheet.createRow(i + 9);

            CellRangeAddress rangoIndicador = new CellRangeAddress(i + 9, i + 9,
                            0, 4);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoIndicador,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoIndicador,
                            sheet, workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoIndicador,
                            sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoIndicador,
                            sheet, workbook);
            sheet.addMergedRegion(rangoIndicador);

            Cell cellIndicador = row6.createCell(0);
            cellIndicador.setCellValue("INDICADOR (PLAN INDICATIVO)");
            cellIndicador.setCellStyle(estiloBorder);

            // Fila 7
            Row row7 = sheet.createRow(i + 11);

            Cell cellUn = row7.createCell(5);
            cellUn.setCellValue("UND");
            cellUn.setCellStyle(estiloContenido);

            Cell cellAux = row7.createCell(6);
            cellAux.setCellValue("0");
            cellAux.setCellStyle(estiloContenido);

            Cell cellAux2 = row7.createCell(7);
            cellAux2.setCellValue("0");
            cellAux2.setCellStyle(estiloContenido);

        }

        // Imprimir componentes
        strSql = Reporteador.resuelveConsulta("800025InformesProyecto", modulo,
                        reemplazar);
        List<Registro> listaComponentes = service
                        .getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);
        String indiceInversion;
        String indiceFisico;
        String indiceEficacia = "";

        if (!listaComponentes.isEmpty()) {
            periocidad = Integer.parseInt(listaComponentes.get(0).getCampos()
                            .get("PERIOCIDAD").toString());

            // Fila 7
            Row row7 = sheet.createRow(i + 13);

            CellRangeAddress rangoComponente = new CellRangeAddress(i + 13,
                            i + 15, 0, 1);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoComponente,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoComponente,
                            sheet, workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoComponente,
                            sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoComponente,
                            sheet, workbook);
            sheet.addMergedRegion(rangoComponente);

            Cell cellComponente = row7.createCell(0);
            cellComponente.setCellValue("COMPONENTES DEL PROYECTO");
            cellComponente.setCellStyle(estiloBorder);

            CellRangeAddress rangoMetas = new CellRangeAddress(i + 13, i + 13,
                            2, 4);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoMetas, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoMetas, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoMetas, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoMetas, sheet,
                            workbook);
            sheet.addMergedRegion(rangoMetas);

            Cell cellMetas = row7.createCell(2);
            cellMetas.setCellValue("METAS PROGRAMADAS Y EJECUTADAS");
            cellMetas.setCellStyle(estiloBorder);

            Cell cellProgramacion = row7.createCell(5);
            cellProgramacion.setCellValue("PROGRAMACIÓN DE ACTIVIDADES");
            cellProgramacion.setCellStyle(estiloBorder);

            CellRangeAddress rangoIndicadores = new CellRangeAddress(i + 13,
                            i + 13, 7, 9);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoIndicadores,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoIndicadores,
                            sheet, workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoIndicadores,
                            sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoIndicadores,
                            sheet, workbook);
            sheet.addMergedRegion(rangoIndicadores);

            // Fila 8
            Row row8 = sheet.createRow(i + 14);

            CellRangeAddress rangoUN = new CellRangeAddress(i + 14, i + 15, 2,
                            2);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoUN, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoUN, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoUN, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoUN, sheet,
                            workbook);
            sheet.addMergedRegion(rangoUN);

            Cell cellUN = row8.createCell(2);
            cellUN.setCellValue("UN");
            cellUN.setCellStyle(estiloBorder);

            CellRangeAddress rangoCAN = new CellRangeAddress(i + 14, i + 15, 3,
                            3);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoCAN, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoCAN, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoCAN, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoCAN, sheet,
                            workbook);
            sheet.addMergedRegion(rangoCAN);

            Cell cellCAN = row8.createCell(3);
            cellCAN.setCellValue("CANT");
            cellCAN.setCellStyle(estiloBorder);

            CellRangeAddress rangoCosto = new CellRangeAddress(i + 14, i + 15,
                            4, 4);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoCosto, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoCosto, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoCosto, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoCosto, sheet,
                            workbook);
            sheet.addMergedRegion(rangoCosto);

            Cell cellCosto = row8.createCell(4);
            cellCosto.setCellValue("COSTO TOTAL");
            cellCosto.setCellStyle(estiloBorder);

            // Fila 9
            Row row9 = sheet.createRow(i + 15);
            int filaFinal = 6;

            for (l = 0; l <= (periocidad - 1) * 2; l = l + 2) {

                CellRangeAddress rangoFisico = new CellRangeAddress(i + 14,
                                i + 14, 5 + l, 6 + l);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoFisico,
                                sheet, workbook);
                RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoFisico,
                                sheet, workbook);
                RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoFisico,
                                sheet, workbook);
                RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoFisico,
                                sheet, workbook);
                sheet.addMergedRegion(rangoFisico);

                Cell cellProg = row8.createCell(5 + l);                
               
                    cellProg.setCellValue(ejb.nombrePeriodicidadBancoProy(periocidad, l / 2 + 1));
                
                cellProg.setCellStyle(estiloBorder);

                Cell cellAvance = row9.createCell(5 + l);
                cellAvance.setCellValue(AVANCEFISICO);
                cellAvance.setCellStyle(estiloBorder);

                Cell cellVTotal = row9.createCell(6 + l);
                cellVTotal.setCellValue(VALORTOTAL);
                cellVTotal.setCellStyle(estiloBorder);
                filaFinal = 6 + l;
            }

            CellRangeAddress rangoProgramacion = new CellRangeAddress(i + 13,
                            i + 13, 5, filaFinal);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoProgramacion,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoProgramacion,
                            sheet, workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoProgramacion,
                            sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoProgramacion,
                            sheet, workbook);
            sheet.addMergedRegion(rangoProgramacion);

            // celda de indicadores
            Cell cellIndicadores = row7.createCell(filaFinal + 1);
            cellIndicadores.setCellValue("INDICADORES DE GESTIÓN");
            cellIndicadores.setCellStyle(estiloBorder);

            CellRangeAddress rangoAux = new CellRangeAddress(i + 13, i + 13,
                            filaFinal + 1, filaFinal + 3);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoAux, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoAux, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoAux, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoAux, sheet,
                            workbook);
            sheet.addMergedRegion(rangoAux);
            int c = 5 + l;

            CellRangeAddress rangoFisico = new CellRangeAddress(i + 14, i + 14,
                            c, c);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoFisico,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoFisico, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoFisico, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoFisico, sheet,
                            workbook);
            sheet.addMergedRegion(rangoFisico);

            Cell cellFisico = row8.createCell(c);
            cellFisico.setCellValue("FISICO");
            cellFisico.setCellStyle(estiloBorder);

            Cell cellInversion = row8.createCell(c + 1);
            cellInversion.setCellValue("INVERSIÓN");
            cellInversion.setCellStyle(estiloBorder);

            Cell cellEficiencia = row8.createCell(c + 2);
            cellEficiencia.setCellValue("EFICIENCIA");
            cellEficiencia.setCellStyle(estiloBorder);

            Cell cellAvance = row9.createCell(5);
            cellAvance.setCellValue(AVANCEFISICO);
            cellAvance.setCellStyle(estiloBorder);

            Cell cellVTotal = row9.createCell(6);
            cellVTotal.setCellValue(VALORTOTAL);
            cellVTotal.setCellStyle(estiloBorder);

            Cell cellAvance2 = row9.createCell(c);
            cellAvance2.setCellValue(
                            "Meta física ejecutada / meta física programada");
            cellAvance2.setCellStyle(estiloContenido);

            Cell cellVTotal2 = row9.createCell(c + 1);
            cellVTotal2.setCellValue(
                            "Recursos financieros ejecutados / recursos financieros programados");
            cellVTotal2.setCellStyle(estiloContenido);

            Cell cellAvance3 = row9.createCell(c + 2);
            cellAvance3.setCellValue(
                            "Índice física / Índice de inversión X Índice físico");
            cellAvance3.setCellStyle(estiloContenido);

            Cell cellVTotal3 = row9.createCell(10);
            cellVTotal3.setCellValue(VALORTOTAL);
            cellVTotal3.setCellStyle(estiloBorder);

            Cell cellAvance4 = row9.createCell(11);
            cellAvance4.setCellValue(AVANCEFISICO);
            cellAvance4.setCellStyle(estiloBorder);

            Cell cellVTotal4 = row9.createCell(12);
            cellVTotal4.setCellValue(VALORTOTAL);
            cellVTotal4.setCellStyle(estiloBorder);

            int contadorFilas = i + 15;
            StringBuilder sumatoriaCostoP = new StringBuilder("");
            StringBuilder sumatoriaCostoE = new StringBuilder("");
            String[] sumatoriaAvanceFisicoP = new String[periocidad];
            String[] sumatoriaAvanceFisicoE = new String[periocidad];
            String[] sumatoriaTotalP = new String[periocidad];
            String[] sumatoriaTotalE = new String[periocidad];

            String componenteAnterior = null;

            for (Registro lista : listaComponentes) {
                if (componenteAnterior == null
                    || !componenteAnterior.equals(lista.getCampos()
                                    .get(GeneralParameterEnum.CODIGO.getName()).toString())) {
                    // Asigna los componentes tipo P
                    contadorFilas += 1;

                    componenteAnterior = lista.getCampos().get(GeneralParameterEnum.CODIGO.getName())
                                    .toString();

                    CellRangeAddress rangoComponente1 = new CellRangeAddress(
                                    contadorFilas, contadorFilas + 1, 0, 0);
                    RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                                    rangoComponente1, sheet, workbook);
                    RegionUtil.setBorderLeft(CellStyle.BORDER_THIN,
                                    rangoComponente1, sheet, workbook);
                    RegionUtil.setBorderRight(CellStyle.BORDER_THIN,
                                    rangoComponente1, sheet, workbook);
                    RegionUtil.setBorderTop(CellStyle.BORDER_THIN,
                                    rangoComponente1, sheet, workbook);
                    sheet.addMergedRegion(rangoComponente1);

                    Row rowA = sheet.createRow(contadorFilas);
                    Cell cellComponente1 = rowA.createCell(0);
                    cellComponente1.setCellValue(lista.getCampos()
                                    .get("NOMBRECOMPONENTE").toString());
                    cellComponente1.setCellStyle(estiloContenido);

                    Cell cellEstado = rowA.createCell(1);
                    cellEstado.setCellValue(
                                    lista.getCampos().get(GeneralParameterEnum.ESTADO.getName()).toString());
                    cellEstado.setCellStyle(estiloContenido);

                    CellRangeAddress rangoUnidad = new CellRangeAddress(
                                    contadorFilas, contadorFilas + 1, 2, 2);
                    RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                                    rangoUnidad, sheet, workbook);
                    RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoUnidad,
                                    sheet, workbook);
                    RegionUtil.setBorderRight(CellStyle.BORDER_THIN,
                                    rangoUnidad, sheet, workbook);
                    RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoUnidad,
                                    sheet, workbook);
                    sheet.addMergedRegion(rangoUnidad);

                    Cell cellUnidad = rowA.createCell(2);
                    cellUnidad.setCellValue(
                                    lista.getCampos().get("UNIDAD").toString());
                    cellUnidad.setCellStyle(estiloContenido);

                    Cell cellCantidad = rowA.createCell(3);
                    cellCantidad.setCellValue(lista.getCampos().get("CANTIDAD")
                                    .toString());
                    cellCantidad.setCellStyle(estiloContenido);

                    Cell cellCosto1 = rowA.createCell(4);
                    cellCosto1.setCellValue(lista.getCampos()
                                    .get("VALORPROGRAMADO").toString());
                    cellCosto1.setCellStyle(estiloNumero);
                    sumatoriaCostoP.append("+")
                        .append(SysmanFunciones.nombreColumna(5))
                        .append(contadorFilas + 1);
                    sumatoriaCostoE.append("+")
                        .append(SysmanFunciones.nombreColumna(5))
                        .append(contadorFilas + 2);

                    int contador = 0;

                    for (int j = 0; j <= (periocidad - 1) * 2; j += 2) {
                        Cell cellFisico1 = rowA.createCell(5 + j);
                        cellFisico1.setCellType(Cell.CELL_TYPE_NUMERIC);
                        double valor = Double
                                        .parseDouble(lista.getCampos()
                                                        .get(SPORCENTAJE
                                                            + ((j / 2) + 1))
                                                        .toString())
                            / 100;
                        if (sumatoriaAvanceFisicoP[contador] == null) {
                            sumatoriaAvanceFisicoP[contador] = SysmanFunciones
                                            .nombreColumna(6 + j)
                                + (contadorFilas + 1);
                        }
                        else {
                            sumatoriaAvanceFisicoP[contador] = sumatoriaAvanceFisicoP[contador]
                                + "+"
                                + SysmanFunciones.nombreColumna(6 + j)
                                + (contadorFilas + 1);
                        }
                        if (sumatoriaAvanceFisicoE[contador] == null) {
                            sumatoriaAvanceFisicoE[contador] = SysmanFunciones
                                            .nombreColumna(6 + j)
                                + (contadorFilas + 2);
                        }
                        else {
                            sumatoriaAvanceFisicoE[contador] = sumatoriaAvanceFisicoE[contador]
                                + "+"
                                + SysmanFunciones.nombreColumna(6 + j)
                                + (contadorFilas + 2);
                        }

                        cellFisico1.setCellValue(valor);
                        cellFisico1.setCellStyle(estiloPorcentaje);

                        Cell cellTotal = rowA.createCell(6 + j);
                        cellTotal.setCellType(Cell.CELL_TYPE_NUMERIC);
                        double valTotal = Double
                                        .parseDouble(lista.getCampos()
                                                        .get(SVALOR
                                                            + ((j / 2) + 1))
                                                        .toString());
                        cellTotal.setCellValue(valTotal);
                        cellTotal.setCellStyle(estiloNumero);
                        if (sumatoriaTotalP[contador] == null) {
                            sumatoriaTotalP[contador] = SysmanFunciones
                                            .nombreColumna(7)
                                + (contadorFilas + 1);
                        }
                        else {
                            sumatoriaTotalP[contador] = sumatoriaTotalP[contador]
                                + "+"
                                + SysmanFunciones.nombreColumna(7 + j)
                                + (contadorFilas + 1);
                        }
                        if (sumatoriaTotalE[contador] == null) {
                            sumatoriaTotalE[contador] = SysmanFunciones
                                            .nombreColumna(7 + j)
                                + (contadorFilas + 2);
                        }
                        else {
                            sumatoriaTotalE[contador] = sumatoriaTotalE[contador]
                                + "+"
                                + SysmanFunciones.nombreColumna(7 + j)
                                + (contadorFilas + 2);
                        }
                        filaFinal = 6 + j;
                        contador += 1;
                    }

                    CellRangeAddress rangoInFisico = new CellRangeAddress(
                                    contadorFilas, contadorFilas + 1,
                                    filaFinal + 1, filaFinal + 1);
                    RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                                    rangoInFisico, sheet, workbook);
                    RegionUtil.setBorderLeft(CellStyle.BORDER_THIN,
                                    rangoInFisico, sheet, workbook);
                    RegionUtil.setBorderRight(CellStyle.BORDER_THIN,
                                    rangoInFisico, sheet, workbook);
                    RegionUtil.setBorderTop(CellStyle.BORDER_THIN,
                                    rangoInFisico, sheet, workbook);
                    sheet.addMergedRegion(rangoInFisico);

                    Cell cellInFisico = rowA.createCell(filaFinal + 1);
                    cellInFisico.setCellType(Cell.CELL_TYPE_FORMULA);
                    cellInFisico.setCellFormula("+"
                        + SysmanFunciones.nombreColumna(filaFinal + 1)
                        + (contadorFilas + 2)
                        + "/" + SysmanFunciones.nombreColumna(filaFinal + 1)
                        + (contadorFilas + 1));
                    cellInFisico.setCellStyle(estiloPorcentaje);

                    CellRangeAddress rangoInversion = new CellRangeAddress(
                                    contadorFilas, contadorFilas + 1,
                                    filaFinal + 2, filaFinal + 2);
                    RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                                    rangoInversion, sheet, workbook);
                    RegionUtil.setBorderLeft(CellStyle.BORDER_THIN,
                                    rangoInversion, sheet, workbook);
                    RegionUtil.setBorderRight(CellStyle.BORDER_THIN,
                                    rangoInversion, sheet, workbook);
                    RegionUtil.setBorderTop(CellStyle.BORDER_THIN,
                                    rangoInversion, sheet, workbook);
                    sheet.addMergedRegion(rangoInversion);

                    Cell cellInversion1 = rowA.createCell(filaFinal + 2);
                    cellInversion1.setCellType(Cell.CELL_TYPE_FORMULA);
                    cellInversion1.setCellStyle(estiloNumero);
                    cellInversion1
                                    .setCellFormula("+"
                                        + SysmanFunciones.nombreColumna(
                                                        filaFinal + 1)
                                        + (contadorFilas + 2)
                                        + "/"
                                        + SysmanFunciones.nombreColumna(
                                                        filaFinal + 1)
                                        + (contadorFilas + 1));

                    CellRangeAddress rangoEficiencia = new CellRangeAddress(
                                    contadorFilas, contadorFilas + 1,
                                    filaFinal + 3, filaFinal + 3);
                    RegionUtil.setBorderBottom(CellStyle.BORDER_THIN,
                                    rangoEficiencia, sheet, workbook);
                    RegionUtil.setBorderLeft(CellStyle.BORDER_THIN,
                                    rangoEficiencia, sheet, workbook);
                    RegionUtil.setBorderRight(CellStyle.BORDER_THIN,
                                    rangoEficiencia, sheet, workbook);
                    RegionUtil.setBorderTop(CellStyle.BORDER_THIN,
                                    rangoEficiencia, sheet, workbook);
                    sheet.addMergedRegion(rangoEficiencia);

                    Cell cellEficiencia1 = rowA.createCell(filaFinal + 3);
                    cellEficiencia1.setCellType(Cell.CELL_TYPE_FORMULA);
                    cellEficiencia1.setCellFormula(rowA.getCell(filaFinal + 1)
                                    .getNumericCellValue()
                        + "*"
                        + rowA.getCell(filaFinal + 2).getNumericCellValue()
                        + "/"
                        + rowA.getCell(filaFinal + 1).getNumericCellValue());
                    evaluator.evaluate(cellEficiencia1);
                    double valor = cellEficiencia1.getNumericCellValue();
                    cellEficiencia1.setCellValue(valor);
                    cellEficiencia1.setCellStyle(estiloPorcentaje);
                    cellEficiencia1
                                    .setCellFormula("+"
                                        + SysmanFunciones.nombreColumna(
                                                        filaFinal + 2)
                                        + (contadorFilas + 1)
                                        + "*"
                                        + SysmanFunciones.nombreColumna(
                                                        filaFinal + 3)
                                        + (contadorFilas + 1) + "/"
                                        + SysmanFunciones.nombreColumna(
                                                        filaFinal + 2)
                                        + (contadorFilas + 1));
                    contadorFilas += 1;
                }
                else {
                    Row rowComponenteE = sheet.createRow(contadorFilas);

                    Cell cellEstado = rowComponenteE.createCell(1);
                    cellEstado.setCellValue(
                                    lista.getCampos().get(GeneralParameterEnum.ESTADO.getName()).toString());
                    cellEstado.setCellStyle(estiloE);

                    Cell cellCantidad = rowComponenteE.createCell(3);
                    cellCantidad.setCellValue(lista.getCampos().get("CANTIDAD")
                                    .toString());
                    cellCantidad.setCellStyle(estiloE);

                    Cell cellCosto1 = rowComponenteE.createCell(4);
                    cellCosto1.setCellValue("0");
                    cellCosto1.setCellStyle(estiloE);

                    for (int j = 0; j <= (periocidad - 1) * 2; j += 2) {
                        Cell cellAvance1 = rowComponenteE.createCell(5);
                        cellAvance1.setCellValue(lista.getCampos()
                                        .get(SPORCENTAJE + ((j / 2) + 1))
                                        .toString());
                        cellAvance1.setCellStyle(estiloPorcentajeE);

                        Cell cellValorTotal = rowComponenteE.createCell(6);
                        cellValorTotal.setCellValue(lista.getCampos()
                                        .get(SVALOR + ((j / 2) + 1))
                                        .toString());
                        cellValorTotal.setCellStyle(estiloNumeroE);
                    }
                    componenteAnterior = lista.getCampos().get(GeneralParameterEnum.CODIGO.getName())
                                    .toString();
                }

            }

            Row rowTotales = sheet.createRow(contadorFilas + 2);
            Row rowTotal = sheet.createRow(contadorFilas + 3);

            CellRangeAddress rangoTotal = new CellRangeAddress(
                            contadorFilas + 2, contadorFilas + 3, 0, 3);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoTotal, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoTotal, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoTotal, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoTotal, sheet,
                            workbook);
            sheet.addMergedRegion(rangoTotal);

            Cell cellTotal = rowTotales.createCell(0);
            cellTotal.setCellValue("TOTAL");
            cellTotal.setCellStyle(estiloBorder);

            Cell cellCostoP = rowTotales.createCell(4);
            cellCostoP.setCellType(Cell.CELL_TYPE_FORMULA);
            cellCostoP.setCellFormula(sumatoriaCostoP.toString());
            cellCostoP.setCellStyle(estiloNumero);

            Cell cellCostoE = rowTotal.createCell(4);
            cellCostoE.setCellType(Cell.CELL_TYPE_FORMULA);
            cellCostoE.setCellFormula(sumatoriaCostoE.toString());
            cellCostoE.setCellStyle(estiloNumeroE);

            for (int j = 0; j < sumatoriaAvanceFisicoE.length; j++) {
                Cell cellAvanceP = rowTotales.createCell(5 + (j * 2));
                cellAvanceP.setCellType(Cell.CELL_TYPE_FORMULA);
                cellAvanceP.setCellFormula(sumatoriaAvanceFisicoP[j]);
                cellAvanceP.setCellStyle(estiloPorcentaje);

                Cell cellTotalP = rowTotales.createCell(6 + (j * 2));
                cellTotalP.setCellType(Cell.CELL_TYPE_FORMULA);
                cellTotalP.setCellFormula(sumatoriaTotalP[j]);
                cellTotalP.setCellStyle(estiloNumero);

                Cell cellAvanceE = rowTotal.createCell(5 + (j * 2));
                cellAvanceE.setCellType(Cell.CELL_TYPE_FORMULA);
                cellAvanceE.setCellFormula(sumatoriaAvanceFisicoE[j]);
                cellAvanceE.setCellStyle(estiloPorcentajeE);

                Cell cellTotalE = rowTotal.createCell(6 + (j * 2));
                cellTotalE.setCellType(Cell.CELL_TYPE_FORMULA);
                cellTotalE.setCellFormula(sumatoriaTotalE[j]);
                cellTotalE.setCellStyle(estiloNumeroE);

            }

            int columnaFin = 5 + (sumatoriaAvanceFisicoE.length * 2);

            CellRangeAddress rangoFisico1 = new CellRangeAddress(
                            contadorFilas + 2, contadorFilas + 3, columnaFin,
                            columnaFin);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoFisico1,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoFisico1, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoFisico1,
                            sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoFisico1, sheet,
                            workbook);
            sheet.addMergedRegion(rangoFisico1);

            Cell cellFisico1 = rowTotales.createCell(columnaFin);
            cellFisico1.setCellType(Cell.CELL_TYPE_FORMULA);
            cellFisico1.setCellFormula(
                            "+" + SysmanFunciones.nombreColumna(columnaFin - 1)
                                + (contadorFilas + 4) + "/"
                                + SysmanFunciones.nombreColumna(columnaFin - 1)
                                + (contadorFilas + 3));
            cellFisico1.setCellStyle(estiloPorcentaje);

            CellRangeAddress rangoInversion = new CellRangeAddress(
                            contadorFilas + 2, contadorFilas + 3,
                            columnaFin + 1,
                            columnaFin + 1);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoInversion,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoInversion,
                            sheet, workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoInversion,
                            sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoInversion,
                            sheet, workbook);
            sheet.addMergedRegion(rangoInversion);

            Cell cellInversion1 = rowTotales.createCell(columnaFin + 1);
            cellInversion1.setCellType(Cell.CELL_TYPE_FORMULA);
            cellInversion1.setCellFormula(
                            "+" + SysmanFunciones.nombreColumna(columnaFin)
                                + (contadorFilas + 4) + "/"
                                + SysmanFunciones.nombreColumna(columnaFin)
                                + (contadorFilas + 3));
            cellInversion1.setCellStyle(estiloNumero);
            indiceInversion = SysmanFunciones.nombreColumna(columnaFin + 2)
                + (contadorFilas + 3);
            indiceFisico = SysmanFunciones.nombreColumna(columnaFin + 1)
                + (contadorFilas + 3);

            CellRangeAddress rangoEficiencia = new CellRangeAddress(
                            contadorFilas + 2, contadorFilas + 3,
                            columnaFin + 2, columnaFin + 2);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoEficiencia,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoEficiencia,
                            sheet, workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoEficiencia,
                            sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoEficiencia,
                            sheet, workbook);
            sheet.addMergedRegion(rangoEficiencia);

            Cell cellEficiencia1 = rowTotales.createCell(columnaFin + 2);
            cellEficiencia1.setCellType(Cell.CELL_TYPE_FORMULA);
            cellEficiencia1.setCellFormula(
                            "+" + SysmanFunciones.nombreColumna(columnaFin + 2)
                                + (contadorFilas + 3)
                                + "*"
                                + SysmanFunciones.nombreColumna(columnaFin + 1)
                                + (contadorFilas + 3) + "/"
                                + SysmanFunciones.nombreColumna(columnaFin + 2)
                                + (contadorFilas + 3));
            cellEficiencia1.setCellStyle(estiloPorcentaje);

            contadorFilas = contadorFilas + 5;
            Row rowA2 = sheet.createRow(contadorFilas);

            CellRangeAddress rangoProducto = new CellRangeAddress(contadorFilas,
                            contadorFilas + 1, 0, 0);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoProducto,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoProducto,
                            sheet, workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoProducto,
                            sheet, workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoProducto, sheet,
                            workbook);
            sheet.addMergedRegion(rangoProducto);

            Cell cellProducto = rowA2.createCell(0);
            cellProducto.setCellValue("PRODUCTO DEL PROYECTO");
            cellProducto.setCellStyle(estiloBorder);

            CellRangeAddress rangoEstado = new CellRangeAddress(contadorFilas,
                            contadorFilas + 1, 1, 1);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoEstado,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoEstado, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoEstado, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoEstado, sheet,
                            workbook);
            sheet.addMergedRegion(rangoEstado);

            Cell cellEstado = rowA2.createCell(1);
            cellEstado.setCellValue("");
            cellEstado.setCellStyle(estiloBorder);

            CellRangeAddress rangoUnidad = new CellRangeAddress(contadorFilas,
                            contadorFilas + 1, 2, 2);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoUnidad,
                            sheet, workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoUnidad, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoUnidad, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoUnidad, sheet,
                            workbook);
            sheet.addMergedRegion(rangoUnidad);

            Cell cellUnidad = rowA2.createCell(2);
            cellUnidad.setCellValue("UN");
            cellUnidad.setCellStyle(estiloBorder);

            CellRangeAddress rangoCan = new CellRangeAddress(contadorFilas,
                            contadorFilas + 1, 3, 3);
            RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoCan, sheet,
                            workbook);
            RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoCan, sheet,
                            workbook);
            RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoCan, sheet,
                            workbook);
            RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoCan, sheet,
                            workbook);
            sheet.addMergedRegion(rangoCan);

            Cell cellCan = rowA2.createCell(3);
            cellCan.setCellValue("CAN");
            cellCan.setCellStyle(estiloBorder);

            Row rowA3 = sheet.createRow(contadorFilas + 1);

            for (int j = 0; j <= (periocidad - 1) * 2; j = j + 2) {
                CellRangeAddress rangoPeriodos = new CellRangeAddress(
                                contadorFilas, contadorFilas, j + 4, j + 5);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoPeriodos,
                                sheet, workbook);
                RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoPeriodos,
                                sheet, workbook);
                RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoPeriodos,
                                sheet, workbook);
                RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoPeriodos,
                                sheet, workbook);
                sheet.addMergedRegion(rangoPeriodos);

                Cell cellMetas1 = rowA2.createCell(j + 4);
                cellMetas1.setCellValue(ejb.nombrePeriodicidadBancoProy(periocidad, j / 2 + 1));

                cellMetas1.setCellStyle(estiloE);

                Cell cellAvance1 = rowA3.createCell(4 + j);
                cellAvance1.setCellValue(AVANCEFISICO);
                cellAvance1.setCellStyle(estiloBorder);

                Cell cellValTotal = rowA3.createCell(5 + j);
                cellValTotal.setCellValue(VALORTOTAL);
                cellValTotal.setCellStyle(estiloBorder);

            }

            contadorFilas += 1;

            strSql = Reporteador.resuelveConsulta("800024InformesProyecto",
                            modulo, reemplazar);
            List<Registro> listaProducto = service
                            .getListado(ConectorPool.ESQUEMA_SYSMAN, strSql);
            if (!listaProducto.isEmpty()) {
                boolean primerFila = true;
                Row rowProducto = sheet.createRow(contadorFilas + 1);

                CellRangeAddress rangoNombre = new CellRangeAddress(
                                contadorFilas + 1, contadorFilas + 2, 0, 0);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoNombre,
                                sheet, workbook);
                RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoNombre,
                                sheet, workbook);
                RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoNombre,
                                sheet, workbook);
                RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoNombre,
                                sheet, workbook);
                sheet.addMergedRegion(rangoNombre);

                Cell cellNombre = rowProducto.createCell(0);
                cellNombre.setCellValue(listaProducto.get(0).getCampos()
                                .get("PRODUCTO_PROYECTO").toString());
                cellNombre.setCellStyle(estiloContenido);

                CellRangeAddress rangoUn = new CellRangeAddress(
                                contadorFilas + 1, contadorFilas + 2, 2, 2);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoUn,
                                sheet, workbook);
                RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoUn, sheet,
                                workbook);
                RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoUn, sheet,
                                workbook);
                RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoUn, sheet,
                                workbook);
                sheet.addMergedRegion(rangoUn);

                Cell cellUn = rowProducto.createCell(2);
                cellUn.setCellValue(listaProducto.get(0).getCampos()
                                .get("UNIDAD_PROYECTO").toString());
                cellUn.setCellStyle(estiloContenido);

                CellRangeAddress rangoCantidad = new CellRangeAddress(
                                contadorFilas + 1, contadorFilas + 2, 3, 3);
                RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rangoCantidad,
                                sheet, workbook);
                RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, rangoCantidad,
                                sheet, workbook);
                RegionUtil.setBorderRight(CellStyle.BORDER_THIN, rangoCantidad,
                                sheet, workbook);
                RegionUtil.setBorderTop(CellStyle.BORDER_THIN, rangoCantidad,
                                sheet, workbook);
                sheet.addMergedRegion(rangoCantidad);

                Cell cellCantidad = rowProducto.createCell(3);
                cellCantidad.setCellValue(listaProducto.get(0).getCampos()
                                .get("CANTIDAD_PROYECTO").toString());
                cellCantidad.setCellStyle(estiloContenido);

                for (Registro listaProducto1 : listaProducto) {
                    int j;
                    if (primerFila) {
                        Cell cellEst = rowProducto.createCell(1);
                        cellEst.setCellValue(listaProducto1.getCampos()
                                        .get(GeneralParameterEnum.ESTADO.getName()).toString());
                        cellEst.setCellStyle(estiloContenido);

                        for (j = 0; j <= (periocidad - 1) * 2; j += 2) {
                            Cell cellAvanFisico = rowProducto.createCell(4 + j);
                            cellAvanFisico.setCellValue(Double.parseDouble(
                                            listaProducto1.getCampos()
                                                            .get(SPORCENTAJE
                                                                + (j / 2 + 1))
                                                            .toString()));
                            cellAvanFisico.setCellStyle(estiloContenido);

                            Cell cellValorTotal = rowProducto.createCell(5 + j);
                            cellValorTotal.setCellValue(Double
                                            .parseDouble(listaProducto1
                                                            .getCampos()
                                                            .get(SVALOR
                                                                + (j / 2 + 1))
                                                            .toString()));
                            cellValorTotal.setCellStyle(estiloNumero);

                        }
                        primerFila = false;
                        indiceEficacia = SysmanFunciones.nombreColumna(3 + j)
                            + (contadorFilas + 2) + "/"
                            + SysmanFunciones.nombreColumna(3 + j)
                            + (contadorFilas + 3);
                        contadorFilas += 2;
                    }
                    else {

                        Row rowAux = sheet.createRow(contadorFilas);

                        Cell cellEst = rowAux.createCell(1);
                        cellEst.setCellValue(listaProducto1.getCampos()
                                        .get(GeneralParameterEnum.ESTADO.getName()).toString());
                        cellEst.setCellStyle(estiloContenido);

                        for (j = 0; j <= (periocidad - 1) * 2; j += 2) {
                            Cell cellAvanFisico = rowAux.createCell(4 + j);
                            cellAvanFisico.setCellValue(Double.parseDouble(
                                            listaProducto1.getCampos()
                                                            .get(SPORCENTAJE
                                                                + (j / 2 + 1))
                                                            .toString()));
                            cellAvanFisico.setCellStyle(estiloPorcentaje);

                            Cell cellValorTotal = rowAux.createCell(5 + j);
                            cellValorTotal.setCellValue(Double
                                            .parseDouble(listaProducto1
                                                            .getCampos()
                                                            .get(SVALOR
                                                                + (j / 2 + 1))
                                                            .toString()));
                            cellValorTotal.setCellStyle(estiloNumero);

                        }
                        indiceEficacia = SysmanFunciones.nombreColumna(3 + j)
                            + (contadorFilas) + "/"
                            + SysmanFunciones.nombreColumna(3 + j)
                            + (contadorFilas + 1);
                        contadorFilas += 1;
                    }
                }
            }

            // pie del informe
            contadorFilas += 2;
            Row rowIndice = sheet.createRow(contadorFilas);

            CellRangeAddress rangoIndiceInversion = new CellRangeAddress(
                            contadorFilas, contadorFilas, 0, 4);
            sheet.addMergedRegion(rangoIndiceInversion);

            Cell cellIndiceInversion = rowIndice.createCell(0);
            cellIndiceInversion
                            .setCellValue("INDICE DE INVERSIÓN DEL PROYETO: ");
            cellIndiceInversion.setCellStyle(estiloFirma);

            Cell cellIndice = rowIndice.createCell(6);
            cellIndice.setCellType(Cell.CELL_TYPE_FORMULA);
            cellIndice.setCellFormula(indiceInversion);
            evaluator.evaluate(cellIndice);
            double valor = cellIndice.getNumericCellValue();
            cellIndice.setCellValue(valor);
            cellIndice.setCellFormula(indiceInversion);
            cellIndice.setCellStyle(estiloNumero);
            CellRangeAddress rangoIndiceEficiencia = new CellRangeAddress(
                            contadorFilas, contadorFilas, 10, 13);
            sheet.addMergedRegion(rangoIndiceEficiencia);

            Cell cellIndiceEficiencia = rowIndice.createCell(10);
            cellIndiceEficiencia
                            .setCellValue("INDICE DE EFICIENCIA DEL PROYECTO");
            cellIndiceEficiencia.setCellStyle(estiloFirma);

            Cell cellEfic = rowIndice.createCell(15);
            cellEfic.setCellType(Cell.CELL_TYPE_FORMULA);
            cellEfic.setCellFormula("+" + SysmanFunciones.nombreColumna(7)
                + (contadorFilas + 1) + "*"
                + SysmanFunciones.nombreColumna(7) + (contadorFilas + 3) + "*"
                + SysmanFunciones.nombreColumna(7)
                + (contadorFilas + 1));
            cellEfic.setCellStyle(estiloContenido);

            contadorFilas += 2;
            Row rowMetas = sheet.createRow(contadorFilas);

            CellRangeAddress rangoMetas1 = new CellRangeAddress(contadorFilas,
                            contadorFilas, 0, 4);
            sheet.addMergedRegion(rangoMetas1);

            Cell cellMetaFisica = rowMetas.createCell(0);
            cellMetaFisica.setCellValue(
                            "INDICE DE METAS FÍSICAS DEL PROYECTO (COMPONENTES): ");
            cellMetaFisica.setCellStyle(estiloFirma);

            Cell cellValMeta = rowMetas.createCell(6);
            cellValMeta.setCellType(Cell.CELL_TYPE_FORMULA);
            cellValMeta.setCellFormula(indiceFisico);
            cellValMeta.setCellStyle(estiloPorcentaje);
            CellRangeAddress rangoEficacia = new CellRangeAddress(contadorFilas,
                            contadorFilas, 10, 13);
            sheet.addMergedRegion(rangoEficacia);

            Cell cellEficacia = rowMetas.createCell(10);
            cellEficacia.setCellValue("INDICE DE EFICACIA DEL PROYECTO");
            cellEficacia.setCellStyle(estiloFirma);

            Cell cellValEficacia = rowMetas.createCell(15);
            cellValEficacia.setCellType(Cell.CELL_TYPE_FORMULA);
            cellValEficacia.setCellFormula(indiceEficacia);
            cellValEficacia.setCellStyle(estiloContenido);

            contadorFilas += 2;

            Row rowEfectividad = sheet.createRow(contadorFilas);

            CellRangeAddress rangoEfectividad = new CellRangeAddress(
                            contadorFilas, contadorFilas, 10, 13);
            sheet.addMergedRegion(rangoEfectividad);

            Cell cellEfectividad = rowEfectividad.createCell(10);
            cellEfectividad.setCellValue(
                            "INDICE DE EFECTIVIDAD DEL PROYECTO: ");
            cellEfectividad.setCellStyle(estiloFirma);

            Cell cellValEfectividad = rowEfectividad.createCell(15);
            cellValEfectividad.setCellValue(PORCENTAJE);
            cellValEfectividad.setCellStyle(estiloPorcentaje);

            contadorFilas += 2;
            CellRangeAddress rangoFirma = new CellRangeAddress(contadorFilas,
                            contadorFilas, 0, 6);
            sheet.addMergedRegion(rangoFirma);

            Row rowFirma1 = sheet.createRow(contadorFilas);

            Cell cellFirma1 = rowFirma1.createCell(0);
            cellFirma1.setCellValue(
                            "Profesional responsable del proyecto: ____________________________________");
            cellFirma1.setCellStyle(estiloFirma);

            CellRangeAddress rangoFirma2 = new CellRangeAddress(contadorFilas,
                            contadorFilas, 10, 15);
            sheet.addMergedRegion(rangoFirma2);

            Cell cellFirma2 = rowFirma1.createCell(10);
            cellFirma2.setCellValue(
                            "Firma de secretario responsable: ___________________________________");
            cellFirma2.setCellStyle(estiloFirma);

            contadorFilas += 2;

            CellRangeAddress rangoFirma3 = new CellRangeAddress(contadorFilas,
                            contadorFilas, 0, 6);
            sheet.addMergedRegion(rangoFirma3);

            Row rowFirma2 = sheet.createRow(contadorFilas);
            Cell cellFirma3 = rowFirma2.createCell(0);
            cellFirma3.setCellValue(
                            "Revisó BPPIM: __________________________________________");
            cellFirma3.setCellStyle(estiloFirma);
        }
        workbook.write(out);
        out.close();     
        aux = JsfUtil.getArchivoDescarga(
                        new ByteArrayInputStream(out.toByteArray()),
                        "SYSMANPLANTILLA_SEGUIMIENTO_PROYECTO_"
                            + proyectoInicial + "_" + vigenciaInicial + ".xls");
        
        }
        catch (SystemException | IOException | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }finally {
            if(workbook != null){
                try {
                    workbook.close();
                }
                catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
            }
        }
        return aux;

    }

}
