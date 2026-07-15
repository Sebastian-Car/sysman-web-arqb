/*-
 * InfDependenciasClaseContratoReporteador.java
 *
 * 1.0
 * 
 * 11/08/2017
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;

import java.awt.Color;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperXlsExporterBuilder;
import net.sf.dynamicreports.jasper.constant.JasperProperty;
import net.sf.dynamicreports.report.builder.chart.Bar3DChartBuilder;
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.VerticalAlignment;

/**
 * 
 * Esta clase es una auxiliar para el controlador
 * InfDependenciasClaseContratoControlador, desde esta se delega la
 * creacion de una hoja de datos la cual esta muy personalizada,
 * buscado desacoplarlo del controlador en si.
 * 
 * @see {@link com.sysman.contratos.InfDependTipoGastoModSeleccControlador}
 * 
 * @version 1.0, 14/09/2017
 * @author jreina
 *
 */
public class InfDependTipoGastoModSeleccReporteador {
    private final String anioInicialCons;
    private final String anioFinalCons;
    private final String dependenciaCons;
    private final String companiaCons;
    Log logger;

    public InfDependTipoGastoModSeleccReporteador() {
        anioFinalCons = "anioFinal";
        anioInicialCons = "anioInicial";
        dependenciaCons = "dependencia";
        companiaCons = "compania";
    }

    public boolean exportarHojaDatosExcel(String consulta, OutputStream stream,
        String tipoGasto,
        String compania, String dependencia, String modalidad,
        String anioInicial, String anioFinal, String nombreDependencia) {

        boolean respuesta = false;
        FontBuilder boldFont = stl.fontArialBold().setFontSize(12);
        try {
            String destino = "";

            if ("I".equals(tipoGasto)) {
                destino = "'INVERSION'";
            }
            else if ("F".equals(tipoGasto)) {
                destino = "'FUNCIONAMIENTO'";
            }

            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(companiaCons, compania);
            reemplazar.put(dependenciaCons, dependencia);
            reemplazar.put("tipoGasto", tipoGasto);
            reemplazar.put("modalidad", modalidad);
            reemplazar.put(anioInicialCons, anioInicial);
            reemplazar.put(anioFinalCons, anioFinal);
            reemplazar.put("destino", destino);
            String consulta1 = Reporteador.resuelveConsulta(
                            "800018DependTipoGastoMod",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            Map<String, Object> reemplazar2 = new HashMap<>();
            reemplazar2.put(companiaCons, compania);
            reemplazar2.put(dependenciaCons, dependencia);
            reemplazar2.put("tipoGasto", tipoGasto);
            reemplazar2.put("modalidad", modalidad);
            reemplazar2.put(anioInicialCons, anioInicial);
            reemplazar2.put(anioFinalCons, anioFinal);
            reemplazar2.put("destino", destino);
            String consulta2 = Reporteador.resuelveConsulta(
                            "800019DependTipoGastoMod",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar2);

            Collection<Map<String, Object>> dummyCollection = new ArrayList<>();
            Collection<Map<String, Object>> dummyCollection2 = new ArrayList<>();
            Collection<Map<String, Object>> dummyCollection3 = new ArrayList<>();

            TextColumnBuilder[] arr = JsfUtil.getDataSource(consulta,
                            ConectorPool.ESQUEMA_SYSMAN, dummyCollection);

            TextColumnBuilder[] arr1 = JsfUtil.getDataSource(consulta1,
                            ConectorPool.ESQUEMA_SYSMAN, dummyCollection2);

            TextColumnBuilder[] arr2 = JsfUtil.getDataSource(consulta2,
                            ConectorPool.ESQUEMA_SYSMAN, dummyCollection3);

            if (!dummyCollection.isEmpty() && !dummyCollection2.isEmpty()
                && !dummyCollection3.isEmpty()) {
                respuesta = true;

                JasperXlsExporterBuilder xlsExporter = export
                                .xlsExporter(stream)
                                .setDetectCellType(true)
                                .setIgnorePageMargins(true)
                                .setWhitePageBackground(false)
                                .setRemoveEmptySpaceBetweenColumns(true);

                Bar3DChartBuilder chart = cht.bar3DChart()
                                .setTitle(nombreDependencia).setFixedWidth(1200)
                                .setTitleFont(boldFont).setCategory(arr1[0])
                                .series(cht.serie(arr1[1]));

                JasperReportBuilder subReport = report().ignorePageWidth()
                                .ignorePagination().addPageFooter(chart)
                                .setDataSource(dummyCollection2);

                Bar3DChartBuilder chart2 = cht.bar3DChart()
                                .setTitle(nombreDependencia).setFixedWidth(1200)
                                .setTitleFont(boldFont).setCategory(arr2[0])
                                .series(cht.serie(arr2[1]));

                JasperReportBuilder subReport4 = report().ignorePageWidth()
                                .ignorePagination().addPageFooter(chart2)
                                .setDataSource(dummyCollection3);

                JasperReportBuilder subReport5 = report()
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
                                .ignorePageWidth().ignorePagination()
                                .columns(arr1)
                                .sortBy(arr1[0])
                                .setDataSource(dummyCollection2);

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
                                .ignorePageWidth().ignorePagination()
                                .columns(arr2)
                                .sortBy(arr2[0])
                                .setDataSource(dummyCollection3);

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
                                                                nombreDependencia
                                                                    + ", "
                                                                    + destino)
                                                                .setFixedWidth(1200)
                                                                .setTitleFont(boldFont)
                                                                .setCategory(arr[0])
                                                                .series(cht.serie(
                                                                                arr[1])),
                                                cmp.text("TOTAL CONTRATOS POR CLASE DE CONTRATO")
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
                                                cmp.subreport(subReport5),
                                                cmp.subreport(subReport),
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
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return respuesta;

    }
}
