/*-
 * InfClaseContratoTipoGastoReporteador.java
 *
 * 1.0
 * 
 * 14/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contratos.reports;

import static net.sf.dynamicreports.report.builder.DynamicReports.cht;
import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.export;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import com.sysman.controladores.SessionUtil;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperXlsExporterBuilder;
import net.sf.dynamicreports.jasper.constant.JasperProperty;
import net.sf.dynamicreports.report.builder.chart.Bar3DChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.exception.DRException;

/**
 * Esta clase es una auxiliar para el controlador
 * InfClaseContratoTipoGastoControlador, desde esta se delega la
 * creacion de una hoja de datos la cual esta muy personalizada,
 * buscado desacoplarlo del controlador en si.
 * 
 * @see {@link com.sysman.contratos.InfClaseContratoTipoGastoControlador}
 * 
 * @version 1.0, 14/08/2017
 * @author pespitia
 *
 */
public class InfClaseContratoTipoGastoReporteador {

    private final String cClaseContrato;
    private final String cCompania;
    private final String cAnioInicial;
    private final String cAnioFinal;

    /**
     * Constente a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario inicio sesion
     */
    private final String modulo;

    public InfClaseContratoTipoGastoReporteador() {
        modulo = SessionUtil.getModulo();
        cClaseContrato = "claseContrato";
        cCompania = "compania";
        cAnioInicial = "anioInicial";
        cAnioFinal = "anioFinal";

    }

    public boolean exportarHojaDatosExcel(OutputStream stream, String compania,
        String anioInicial, String anioFinal, String claseContrato,
        String descripcionContrato)
                        throws IOException, DRException, SysmanException {
        boolean respuesta;
        FontBuilder boldFont = stl.fontArialBold().setFontSize(12);

        Map<String, Object> reemplazar = new HashMap<>();
        reemplazar.put(cClaseContrato, claseContrato);
        reemplazar.put(cCompania, compania);
        reemplazar.put(cAnioInicial, anioInicial);
        reemplazar.put(cAnioFinal, anioFinal);

        String consulta = Reporteador.resuelveConsulta(
                        "800001ClaseContratoTipoGasto",
                        Integer.parseInt(modulo),
                        reemplazar);

        String consulta1 = Reporteador.resuelveConsulta(
                        "800002ClaseContratoTipoGasto",
                        Integer.parseInt(modulo), reemplazar);

        String consulta2 = Reporteador.resuelveConsulta(
                        "800003ClaseContratoTipoGasto",
                        Integer.parseInt(modulo), reemplazar);

        Collection<Map<String, Object>> dummyCollection = new ArrayList<>();
        Collection<Map<String, Object>> dummyCollection1 = new ArrayList<>();
        Collection<Map<String, Object>> dummyCollection2 = new ArrayList<>();

        TextColumnBuilder[] arr = JsfUtil.getDataSource(consulta,
                        ConectorPool.ESQUEMA_SYSMAN, dummyCollection);

        TextColumnBuilder[] arr1 = JsfUtil.getDataSource(consulta1,
                        ConectorPool.ESQUEMA_SYSMAN, dummyCollection1);

        TextColumnBuilder[] arr2 = JsfUtil.getDataSource(consulta2,
                        ConectorPool.ESQUEMA_SYSMAN, dummyCollection2);

        if (!dummyCollection.isEmpty() && !dummyCollection1.isEmpty()
            && !dummyCollection2.isEmpty()) {
            respuesta = true;

            JasperXlsExporterBuilder xlsExporter = export.xlsExporter(stream)
                            .setDetectCellType(true)
                            .setIgnorePageMargins(true)
                            .setWhitePageBackground(false)
                            .setRemoveEmptySpaceBetweenColumns(true);

            Bar3DChartBuilder chart = cht.bar3DChart()
                            .setTitle(descripcionContrato).setFixedWidth(800)
                            .setTitleFont(boldFont).setCategory(arr1[0])
                            .series(cht.serie(arr1[1]), cht.serie(arr1[2]));

            Bar3DChartBuilder chart2 = cht.bar3DChart().setTitle(claseContrato)
                            .setFixedWidth(800)
                            .setTitleFont(boldFont).setCategory(arr2[0])
                            .series(cht.serie(arr2[1]), cht.serie(arr2[2]));

            JasperReportBuilder subReport2 = report().ignorePageWidth()
                            .ignorePagination().addPageFooter(chart)
                            .setDataSource(dummyCollection1);

            JasperReportBuilder subReport4 = report().ignorePageWidth()
                            .ignorePagination().addPageFooter(chart2)
                            .setDataSource(dummyCollection2);

            JasperReportBuilder subReport1 = report()
                            .setColumnTitleStyle(stl
                                            .style(stl.style(stl.style()
                                                            .setPadding(2))
                                                            .setVerticalAlignment(
                                                                            VerticalAlignment.MIDDLE))
                                            .setBorder(stl.pen1Point())
                                            .setHorizontalAlignment(
                                                            HorizontalAlignment.CENTER)
                                            .setBackgroundColor(
                                                            Color.LIGHT_GRAY)
                                            .bold())
                            .setColumnStyle(stl
                                            .style(stl.style(stl.style()
                                                            .setPadding(2))
                                                            .setVerticalAlignment(
                                                                            VerticalAlignment.MIDDLE))
                                            .setBorder(stl.pen1Point())
                                            .setHorizontalAlignment(
                                                            HorizontalAlignment.CENTER)
                                            .setBackgroundColor(Color.WHITE)
                                            .bold())
                            .ignorePageWidth().ignorePagination().columns(arr1)
                            .sortBy(arr1[0]).setDataSource(dummyCollection1);

            JasperReportBuilder subReport3 = report()
                            .setColumnTitleStyle(stl
                                            .style(stl.style(stl.style()
                                                            .setPadding(2))
                                                            .setVerticalAlignment(
                                                                            VerticalAlignment.MIDDLE))
                                            .setBorder(stl.pen1Point())
                                            .setHorizontalAlignment(
                                                            HorizontalAlignment.CENTER)
                                            .setBackgroundColor(
                                                            Color.LIGHT_GRAY)
                                            .bold())
                            .setColumnStyle(stl
                                            .style(stl.style(stl.style()
                                                            .setPadding(2))
                                                            .setVerticalAlignment(
                                                                            VerticalAlignment.MIDDLE))
                                            .setBorder(stl.pen1Point())
                                            .setHorizontalAlignment(
                                                            HorizontalAlignment.CENTER)
                                            .setBackgroundColor(Color.WHITE)
                                            .bold())
                            .ignorePageWidth().ignorePagination().columns(arr2)
                            .sortBy(arr2[0]).setDataSource(dummyCollection2);

            report().setColumnTitleStyle(
                            stl.style(stl.style(stl.style().setPadding(2))
                                            .setVerticalAlignment(
                                                            VerticalAlignment.MIDDLE))
                                            .setBorder(stl.pen1Point())
                                            .setHorizontalAlignment(
                                                            HorizontalAlignment.CENTER)
                                            .setBackgroundColor(
                                                            Color.LIGHT_GRAY)
                                            .bold())
                            .setColumnStyle(stl
                                            .style(stl.style(stl.style()
                                                            .setPadding(2))
                                                            .setVerticalAlignment(
                                                                            VerticalAlignment.MIDDLE))
                                            .setBorder(stl.pen1Point())
                                            .setHorizontalAlignment(
                                                            HorizontalAlignment.CENTER)
                                            .setBackgroundColor(Color.WHITE)
                                            .bold())
                            .addProperty(JasperProperty.EXPORT_XLS_FREEZE_ROW,
                                            "2")
                            .addProperty(JasperProperty.EXPORT_XLS_FIT_WIDTH,
                                            "1")
                            .addProperty(JasperProperty.EXPORT_XLS_FIT_HEIGHT,
                                            "1")
                            .addProperty(JasperProperty.EXPORT_XLS_DETECT_CELL_TYPE,
                                            "1")
                            .ignorePageWidth().ignorePagination()
                            .summary(
                                            cht.bar3DChart().setTitle(
                                                            claseContrato)
                                                            .setFixedWidth(800)
                                                            .setTitleFont(boldFont)
                                                            .setCategory(arr[0])
                                                            .series(cht.serie(
                                                                            arr[1]),
                                                                            cht.serie(arr[2])),
                                            cmp.text(descripcionContrato)
                                                            .setStyle(stl
                                                                            .style(stl.style(
                                                                                            stl.style().setPadding(
                                                                                                            2))
                                                                                            .setVerticalAlignment(
                                                                                                            VerticalAlignment.MIDDLE))
                                                                            .setBorder(stl.pen1Point())
                                                                            .setHorizontalAlignment(
                                                                                            HorizontalAlignment.CENTER)
                                                                            .setBackgroundColor(
                                                                                            Color.WHITE)
                                                                            .bold()),
                                            cmp.subreport(subReport2),
                                            cmp.subreport(subReport1),
                                            cmp.text("TOTAL CONTRATADO")
                                                            .setStyle(stl
                                                                            .style(stl.style(
                                                                                            stl.style().setPadding(
                                                                                                            2))
                                                                                            .setVerticalAlignment(
                                                                                                            VerticalAlignment.MIDDLE))
                                                                            .setBorder(stl.pen1Point())
                                                                            .setHorizontalAlignment(
                                                                                            HorizontalAlignment.CENTER)
                                                                            .setBackgroundColor(
                                                                                            Color.WHITE)
                                                                            .bold()),
                                            cmp.subreport(subReport4),
                                            cmp.subreport(subReport3))
                            .columns(arr).setDataSource(dummyCollection)
                            .toXls(xlsExporter);
        }
        else {
            respuesta = false;
        }

        return respuesta;
    }
}
